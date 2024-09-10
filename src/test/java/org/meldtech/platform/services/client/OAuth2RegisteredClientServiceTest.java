package org.meldtech.platform.services.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.Oauth2RegisteredClientRepository;
import org.meldtech.platform.service.OAuth2RegisteredClientService;
import org.meldtech.platform.stub.AppResponseStub;
import org.meldtech.platform.stub.ClientRequestStub;
import org.meldtech.platform.util.ReportSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuth2RegisteredClientServiceTest {
    @Mock
    Oauth2RegisteredClientRepository clientRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    PaginatedResponse paginatedResponse;
    @InjectMocks
    OAuth2RegisteredClientService clientService;

    @DisplayName("Add New OAuth2 Client")
    @Test
    void givenClient_whenCreateClient_thenReturnClient() {
        when(clientRepository.save(any()))
                .thenReturn(Mono.just(AppResponseStub.create()));

        when(passwordEncoder.encode(any()))
                .thenReturn(UUID.randomUUID().toString());

        StepVerifier.create(clientService.createClient(ClientRequestStub.getClientRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get OAuth2 Clients ")
    @Test
    void givenPagination_whenGetAllClient_thenReturnAppResponse() {
        when(clientRepository.findAllBy(any()))
                .thenReturn(Flux.just(AppResponseStub.clients().toArray(new OAuth2RegisteredClient[0])));

        when(paginatedResponse.getData(any(), any(), any()))
                .thenReturn(Mono.just(AppResponseStub.appResponse()));

        StepVerifier.create(clientService.getClients(ReportSettings.instance().page(1).size(10)))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get OAuth2 Client by Id")
    @Test
    void givenClientId_whenGetClient_thenReturnAppResponse() {
        when(clientRepository.findByClientId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.create()));

        StepVerifier.create(clientService.getClients(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Update OAuth2 Client by Id")
    @Test
    void givenClientIdAnUpdateClient_whenEditClient_thenReturnAppResponse() {
        when(clientRepository.findByClientId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.create()));

        when(clientRepository.save(any()))
                .thenReturn(Mono.just(AppResponseStub.create()));

        StepVerifier.create(clientService.updateClients(UUID.randomUUID().toString(),ClientRequestStub.getClientRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Delete OAuth2 Client by Id")
    @Test
    void givenClientId_whenDeleteClient_thenReturnAppResponse() {
        when(clientRepository.findByClientId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.create()));

        when(clientRepository.delete(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.deleteClient(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Invalid OAuth2 Client Id")
    @Test
    void givenWrongClientId_whenGetClientById_thenReturnException() {
        when(clientRepository.findByClientId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(clientService.getClients(UUID.randomUUID().toString()))
                .expectError(ApiResponseException.class)
                .verify();

    }
}
