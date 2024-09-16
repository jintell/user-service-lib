package org.meldtech.platform.services.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.domain.Role;
import org.meldtech.platform.repository.RoleRepository;
import org.meldtech.platform.service.RoleService;
import org.meldtech.platform.stub.AppResponseStub;
import org.meldtech.platform.util.ReportSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.meldtech.platform.stub.AppResponseStub.roles;
import static org.meldtech.platform.stub.AppResponseStub.userProfiles;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    RoleRepository roleRepository;
    @Mock
    PaginatedResponse paginatedResponse;
    @InjectMocks
    RoleService roleService;


    @DisplayName("Get User Profiles ")
    @Test
    void givenPagination_whenGetAllRoles_thenReturnAppResponse() {
        when(roleRepository.findAllBy(any()))
                .thenReturn(Flux.just(roles().toArray(new Role[0])));

        when(paginatedResponse.getPageIntId(any(), any(), any()))
                .thenReturn(Mono.just(AppResponseStub.appResponses(userProfiles())));

        StepVerifier.create(roleService.getRoles(ReportSettings.instance().page(1).size(10)))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }
}