package org.meldtech.platform.event;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.meldtech.platform.domain.Company;
import org.meldtech.platform.model.api.response.CompanyData;
import org.meldtech.platform.repository.CompanyRepository;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Consumer;

import static org.meldtech.platform.util.AppUtil.getMapper;

@Configuration
@RequiredArgsConstructor
public class CompanyEmailUpdateEvent {
    private final LoggerHelper log = LoggerHelper.newInstance(CompanyEmailUpdateEvent.class.getName());
    private final CompanyRepository companyRepository;

    @Bean
    public Consumer<String> updateCompanyEmail() {
        return s -> {
            log.info("update Company Email Status --- ", s);
            CompanyData request = buildCompanyData(s);
            log.info("document: ",request);
            if(Objects.isNull(request.name())) return;
            companyRepository.findByName(request.name())
                    .flatMap(company -> construct(company, request))
                    .subscribe(company -> log.info("Updated ",
                            company.getName() ," email to ",
                            company.getContact()));
        };
    }

    private CompanyData buildCompanyData(String data) {
        try {
            return getMapper().readValue(data, CompanyData.class);
        } catch (JsonProcessingException e) {
            return  new CompanyData("", "");
        }
    }

    private Mono<Company> construct(Company company, CompanyData request) {
        if(Objects.nonNull(company) && Objects.nonNull(request) &&
                company.getContact().trim().equalsIgnoreCase(request.email().trim())) {
            return Mono.empty();
        }
        company.setContact(request.email().trim());
        return companyRepository.save(company);
    }
}
