package org.meldtech.platform.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.meldtech.platform.model.api.request.reminder.Defaulter;
import org.meldtech.platform.model.api.request.reminder.ReminderData;
import org.meldtech.platform.model.api.request.reminder.ReminderInfo;
import org.meldtech.platform.model.event.GenericRequest;
import org.meldtech.platform.repository.CompanyRepository;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.meldtech.platform.util.AppUtil.getMapper;

@Configuration
@RequiredArgsConstructor
public class DemandNoticeReminderEvent {
    private final LoggerHelper log = LoggerHelper.newInstance(DemandNoticeReminderEvent.class.getName());
    private final CompanyRepository companyRepository;
    private final EmailEvent event;

    @Value("${email.reminder.single.template}")
    private String singleTemplate;

    @Value("${email.reminder.multiple.template}")
    private String multipleTemplate;

    @Value("${email.enforcer}")
    private String enforcerEmail;

    @Bean
    public Consumer<String> reminderData() {
        return s -> {
            log.info("Preparing to send defaulter reminder --- ", s);
            ReminderInfo request = buildReminderInfo(s);
            log.info("reminder for: ",request);
            if(Objects.isNull(request.type())) return;
            if(request.type().equalsIgnoreCase("SINGLE")) {
                sendSingleDefaulter(request)
                        .flatMap(defaulter ->
                                buildGenericRequest(defaulter, defaulter.to(), 1))
                        .flatMap(event::sendMail)
                        .subscribe(aBoolean -> log.info("Finished sending reminder"));
            }else if(request.type().equalsIgnoreCase("MULTIPLE")) {
                request.reminders().forEach(reminderData -> {
                    sendSingleDefaulter(reminderData)
                            .flatMap(defaulter ->
                            buildGenericRequest(defaulter, defaulter.to(), 1))
                            .flatMap(event::sendMail)
                            .subscribe(aBoolean -> log.info("Finished sending reminders "));
                });
                buildGenericRequest(request.reminders(), enforcerEmail, 2)
                        .subscribe(aBoolean -> log.info("Finished sending ", request.reminders().size(),
                                " reminders sent to enforcer ", enforcerEmail));
            }

        };
    }

    private ReminderInfo buildReminderInfo(String data) {
        try {
            return getMapper().readValue(data, ReminderInfo.class);
        } catch (JsonProcessingException e) {
            return  new ReminderInfo("", null, List.of());
        }
    }
    private Mono<Defaulter> sendSingleDefaulter(ReminderInfo reminderInfo) {
        return companyRepository.findByIdNumber(reminderInfo.data().applicant().id())
                .map(company -> Defaulter.builder()
                        .name(reminderInfo.data().applicant().name())
                        .reminders(reminderInfo.data().remindedOn())
                        .to(company.getContact())
                        .build());
    }

    private Mono<Defaulter> sendSingleDefaulter(ReminderData reminderData) {
        return companyRepository.findByIdNumber(reminderData.applicant().id())
                .map(company -> Defaulter.builder()
                        .name(reminderData.applicant().name())
                        .reminders(reminderData.remindedOn())
                        .to(company.getContact())
                        .build());
    }

    private <T> Mono<GenericRequest> buildGenericRequest(T defaulter, String to, int type) {
        return Mono.just(GenericRequest.builder()
                        .to(to)
                        .template(defaulter)
                        .templateId(type == 1?singleTemplate : multipleTemplate)
                .build());
    }
}
