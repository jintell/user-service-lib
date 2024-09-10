package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.converter.RegisteredClientConverter;
import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientRecord;
import org.meldtech.platform.repository.Oauth2RegisteredClientRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    private final PasswordEncoder passwordEncoder;
    private final PaginatedResponse paginatedResponse;

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
                .map(newRecord -> appResponse(newRecord, CREATED_SUCCESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> getClients(String clientId) {
        return validateClient(clientId)
                .map(RegisteredClientConverter::mapToRecord)
                .doOnNext(registeredClientRecord -> log.info("Registered Client Record {}", registeredClientRecord))
                .map(record -> appResponse(record, SUCCESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> getClients(ReportSettings settings) {
        return clientRepository.findAllBy(setPage(settings))
                .map(RegisteredClientConverter::mapToRecord)
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

    private Mono<OAuth2RegisteredClient> validateClient(String clientId) {
        return clientRepository.findByClientId(clientId.toUpperCase())
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_CLIENT), NOT_FOUND.value()));
    }

}
