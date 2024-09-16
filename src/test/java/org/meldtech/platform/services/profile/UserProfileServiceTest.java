package org.meldtech.platform.services.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.domain.UserProfile;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.service.UserProfileService;
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

import static org.meldtech.platform.stub.AppResponseStub.userProfiles;
import static org.meldtech.platform.stub.FullUserProfileRecordStub.createFullUserProfileRecord;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserProfileRepository profileRepository;
    @Mock
    PaginatedResponse paginatedResponse;
    @InjectMocks
    UserProfileService userProfileService;


    @DisplayName("Get User Profiles ")
    @Test
    void givenPagination_whenGetAllUserProfiles_thenReturnAppResponse() {
        when(profileRepository.findAllBy(any()))
                .thenReturn(Flux.just(userProfiles().toArray(new UserProfile[0])));

        when(paginatedResponse.getPageIntId(any(), any(), any()))
                .thenReturn(Mono.just(AppResponseStub.appResponses(userProfiles())));

        when(userRepository.findById(anyInt()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        StepVerifier.create(userProfileService.getUserProfiles(ReportSettings.instance().page(1).size(10)))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get User Profile By Pubic Id ")
    @Test
    void givenPublicId_whenGetUserProfile_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));

        StepVerifier.create(userProfileService.getUserProfile(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get User Profiles By Pubic Id (Admin)")
    @Test
    void givenPublicIdByAdmin_whenGetUserProfile_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));

        StepVerifier.create(userProfileService.getUserProfileByAdmin(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Create User Profiles")
    @Test
    void givenUserProfile_whenCreateUserProfile_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.empty());

        when(profileRepository.save(any()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));

        StepVerifier.create(userProfileService.createUserProfile(createFullUserProfileRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Edit User Profiles")
    @Test
    void givenUserProfile_whenEditUserProfile_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));

        when(profileRepository.save(any()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));

        StepVerifier.create(userProfileService.updateUserProfile(createFullUserProfileRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Create User Profiles Already Exists")
    @Test
    void givenUserProfile_whenUserProfileAlreadyExist_thenReturnException() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.just(AppResponseStub.userProfile(1)));


        StepVerifier.create(userProfileService.createUserProfile(createFullUserProfileRecord()))
                .expectError(ApiResponseException.class)
                .verify();
    }

    @DisplayName("Get User Profile By Pubic Id Not Found ")
    @Test
    void givenPublicId_whenGetUserProfileNotFound_thenReturnException() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(AppResponseStub.user()));

        when(profileRepository.findById(anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userProfileService.getUserProfile(UUID.randomUUID().toString()))
                .expectError(ApiResponseException.class)
                .verify();

    }
}
