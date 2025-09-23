package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.converter.AppRegistrationConverter;
import org.meldtech.platform.converter.RegisteredClientConverter;
import org.meldtech.platform.domain.AppRegistration;
import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.dto.AppRegistrationRecord;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientRecord;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientResponse;
import org.meldtech.platform.repository.AppRegistrationRepository;
import org.meldtech.platform.repository.Oauth2RegisteredClientRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.meldtech.platform.converter.AppRegistrationConverter.toEntity;
import static org.meldtech.platform.converter.RegisteredClientConverter.toRecord;
import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.meldtech.platform.util.AppUtil.setPage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2RegisteredClientService {
    private final Oauth2RegisteredClientRepository clientRepository;
    private final AppRegistrationRepository appRegistrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaginatedResponse paginatedResponse;
    private final EncDecService encDecService;

    private static final String SUCCESS_MSG = "Get Oauth2 Client(s) Successfully";
    private static final String CREATED_SUCCESS_MSG = "Oauth2 Client(s) Successfully Created";
    private static final String UPDATED_SUCCESS_MSG = "Oauth2 Client(s) Successfully Updated";
    private static final String DELETE_SUCCESS_MSG = "Oauth2 Client(s) Successfully Deleted";
    private static final String INVALID_CLIENT = "Invalid Client Id";

    public Mono<AppResponse> createClient(OAuth2RegisteredClientRecord record) {
        return clientRepository.save(RegisteredClientConverter.mapToEntity(record, passwordEncoder))
                .doOnNext(registeredClientRecord -> log.info("Saved New Record {}", registeredClientRecord))
                .map(RegisteredClientConverter::mapToRecord)
                .doOnNext(registeredClientRecord -> log.info("Registered Client New Record {}", registeredClientRecord))
                .flatMap(newRecord -> registerApp(record))
                .doOnNext(registeredClientRecord -> log.info("Registered New Application {}", registeredClientRecord))
                .map(newRecord -> appResponse(newRecord, CREATED_SUCCESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> getClients(String clientId) {
        return validateClient(clientId)
                .map(RegisteredClientConverter::mapToRecord)
                .doOnNext(registeredClientRecord -> log.info("Registered Client Record {}", registeredClientRecord))
                .flatMap(this::getAppByClientId)
                .map(record -> appResponse(record, SUCCESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> getClients(ReportSettings settings) {
        return clientRepository.findAllBy(setPage(settings))
                .map(RegisteredClientConverter::mapToRecord)
                .flatMap(this::getAppByClientId)
                .collectList()
                .flatMap(clients -> paginatedResponse.getData(clients, clientRepository, setPage(settings)))
                .doOnNext(registeredClientRecords -> log.info("Registered Client Records {}", registeredClientRecords))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> updateClients(String clientId, OAuth2RegisteredClientRecord record) {
        return validateClient(clientId)
                .map(client ->  RegisteredClientConverter.mapToEntity(record, client, passwordEncoder))
                .flatMap(clientRepository::save)
                .map(RegisteredClientConverter::mapToRecord)
                .doOnNext(registeredClientRecords -> log.info("Registered Client Edited Records {}", registeredClientRecords))
                .map(editRecord -> appResponse(editRecord, UPDATED_SUCCESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> deleteClient(String clientId) {
        log.info("Deleting the client {}", clientId);
        return validateClient(clientId)
                .flatMap(clientRepository::delete)
                .then(Mono.fromCallable(() -> appResponse( "Client Deleted", DELETE_SUCCESS_MSG)));
    }

    public Mono<AppRegistrationRecord> getApp(String appId) {
        return appRegistrationRepository.findByApplicationId(appId)
                .<AppRegistration>handle((entity, sink) -> {
                    try {
                        sink.next(AppRegistration.builder()
                                .id(entity.id())
                                .applicationId(entity.applicationId())
                                .clientId(entity.clientId())
                                .clientName(entity.clientName())
                                .clientSecret(encDecService.decrypt(entity.clientSecret()))
                                .redirectUrl(entity.redirectUrl())
                                .appLoginUrl(entity.appLoginUrl())
                                .appLogoutUrl(entity.appLogoutUrl())
                                .appResolvedPathUrl(entity.appResolvedPathUrl())
                                .scope(entity.scope())
                                .enabled(entity.enabled())
                                .build());
                    } catch (Exception e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .map(AppRegistrationConverter::toRecord)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    private Mono<OAuth2RegisteredClientResponse> getAppByClientId(OAuth2RegisteredClientRecord record) {
        return appRegistrationRepository.findByClientId(record.clientId())
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CLIENT), NOT_FOUND.value()))
                .map(AppRegistrationConverter::toRecord)
                .map(app -> toRecord(record, app));
    }

    private Mono<OAuth2RegisteredClientResponse> registerApp(OAuth2RegisteredClientRecord record) {
        try {
            String appId = UUID.randomUUID().toString();
            String appSecret = encDecService.encrypt(record.clientSecret());
            List<String> scope = record.scopes().stream().toList();
            AppRegistrationRecord appRecord = AppRegistrationRecord.builder()
                    .applicationId(appId)
                    .clientName(record.clientName())
                    .clientId(record.clientId())
                    .clientSecret(appSecret)
                    .redirectUrl(concat(record.redirectUris()))
                    .appLoginUrl(Objects.nonNull(record.appRegistration()) ?
                            record.appRegistration().appLoginUrl() : null)
                    .appLogoutUrl(Objects.nonNull(record.appRegistration()) ?
                            record.appRegistration().appLogoutUrl() : null)
                    .appResolvedPathUrl(Objects.nonNull(record.appRegistration()) ?
                            record.appRegistration().appResolvedPathUrl() : null)
                    .enabled(true)
                    .scope(scope.get(0))
                    .build();
            return appRegistrationRepository.save(toEntity(appRecord))
                    .map(AppRegistrationConverter::toRecord)
                    .map(app -> toRecord(record, app));
        }catch (Exception e){
            log.error("Error registering app", e);
            return Mono.error(e);
        }
    }

    private Mono<OAuth2RegisteredClient> validateClient(String clientId) {
        return clientRepository.findByClientId(clientId.toUpperCase())
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CLIENT), NOT_FOUND.value()));
    }

    private static String concat(Set<String> values) {
        return String.join(",", values);
    }

}
