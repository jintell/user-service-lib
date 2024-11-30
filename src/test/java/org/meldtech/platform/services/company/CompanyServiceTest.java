package org.meldtech.platform.services.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.domain.Role;
import org.meldtech.platform.model.constant.VerifyType;
import org.meldtech.platform.repository.CompanyRepository;
import org.meldtech.platform.service.CompanyService;
import org.meldtech.platform.service.verifyMe.VerifyMeStrategy;
import org.meldtech.platform.service.verifyMe.VerifyStrategyFactory;
import org.meldtech.platform.stub.AppResponseStub;
import org.meldtech.platform.util.ReportSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @Mock
    CompanyRepository companyRepository;
    @Mock
    VerifyStrategyFactory verifyMeStrategyFactory;
    @Mock
    VerifyMeStrategy verifyStrategy;
    @InjectMocks
    CompanyService companyService;

    @DisplayName("Get User Profiles ")
    @Test
    void whenGetCompanyByIdNumber_thenReturnAppResponse() {
        when(companyRepository.findByIdNumber(any()))
                .thenReturn(Mono.just(company(1)));

        StepVerifier.create(companyService.getCompany(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get User Profiles ")
    @Test
    void givenIdNumber_whenVerifyIdentity_thenReturnAppResponse() {
        when(verifyMeStrategyFactory.getVerifyMeStrategy(any()))
                .thenReturn(verifyStrategy);

        when(verifyStrategy.verifyMe(any()))
                .thenReturn(Mono.just(companyRecord(1)));

        StepVerifier.create(companyService.verifyIdentity(verifyRequest(), VerifyType.CAC))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }
}