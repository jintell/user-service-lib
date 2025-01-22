package org.meldtech.platform.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.repository.RolePermissionRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.repository.UserRoleRepository;
import org.meldtech.platform.service.UserPermissionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPermissionServiceTest {
    @Mock
    RolePermissionRepository permissionRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserRoleRepository userRoleRepository;
    @InjectMocks
    UserPermissionService userPermissionService;

    @Test
    @DisplayName("Get User Permissions ")
    void givenPublicId_whenGetUserPermission_thenReturnAppResponse() {

        when(userRepository.findByPublicId(any()))
                .thenReturn(Mono.just(user()));
        when(userRoleRepository.findByUserId(any()))
                .thenReturn(Mono.just(userRoles()));
        when(permissionRepository.findByRoleId(any()))
                        .thenReturn(Mono.just(rolePermission()));

        StepVerifier.create(userPermissionService.getUserPermissions(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

//    @DisplayName("Get User Profile By Pubic Id Not Found ")
//    @Test
//    void givenPublicId_whenGetUserProfileNotFound_thenReturnException() {
//        when(userRepository.findByPublicId(anyString()))
//                .thenReturn(Mono.just(AppResponseStub.user()));
//
//        when(profileRepository.findById(anyInt()))
//                .thenReturn(Mono.empty());
//
//        StepVerifier.create(userProfileService.getUserProfile(UUID.randomUUID().toString()))
//                .expectError(ApiResponseException.class)
//                .verify();
//
//    }
}