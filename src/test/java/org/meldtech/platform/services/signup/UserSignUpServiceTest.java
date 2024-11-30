package org.meldtech.platform.services.signup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.event.EmailEvent;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.*;
import org.meldtech.platform.service.UserProfileService;
import org.meldtech.platform.service.UserSignUpService;
import org.meldtech.platform.stub.PasswordResetStub;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.model.constant.VerificationType.OTHERS;
import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {
    @Mock
    UserProfileRepository userProfileRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    VerificationRepository verificationRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailEvent emailEvent;
    @Mock
    UserProfileService userProfileService;
    @InjectMocks
    UserSignUpService signUpService;

    @DisplayName("Create New User Account ")
    @Test
    void givenUserAccount_whenCreateUser_thenReturnAppResponse() {
        when(userRepository.findByUsername(any())).thenReturn(Mono.empty());
        when(userRepository.save(any())).thenReturn(Mono.just(user()));
        when(roleRepository.findByName(anyString())).thenReturn(Mono.just(role(1)));
        when(verificationRepository.save(any())).thenReturn(Mono.just(verification(1, "009876", OTHERS)));
        when(userRoleRepository.save(any())).thenReturn(Mono.just(userRole()));
        when(emailEvent.sendMail(any())).thenReturn(Mono.just(emailRequest("009876")));
        when(userProfileService.createUserProfile(any(), anyInt()))
                .thenReturn(Mono.just(profileRecord()));

        StepVerifier.create(signUpService.createUser(userRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Verify User Otp")
    @Test
    void givenOtp_whenVerifyOtp_thenReturnAppResponse() {
        when(verificationRepository.findByUserOtp(anyString()))
                .thenReturn(Mono.just(verification(1, "009876", OTHERS)));
        when(verificationRepository.delete(any())).thenReturn(Mono.empty());
        when(userRepository.findById(anyInt())).thenReturn(Mono.just(user()));
        when(userRepository.save(any())).thenReturn(Mono.just(user()));
        when(userProfileRepository.findById(anyInt())).thenReturn(Mono.just(userProfile(1)));
        when(userProfileRepository.save(any())).thenReturn(Mono.just(userProfile(1)));

        StepVerifier.create(signUpService.verifyOtp(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Resend Otp")
    @Test
    void givenOtp_whenResendOtp_thenReturnAppResponse() {
        when(verificationRepository.findByUserId(anyInt()))
                .thenReturn(Mono.just(verification(1, "009876", OTHERS)));
        when(verificationRepository.save(any())).thenReturn(Mono.just(verification(1, "009876", OTHERS)));
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user()));
        when(emailEvent.sendMail(any())).thenReturn(Mono.just(emailRequest("009876")));

        StepVerifier.create(signUpService.resendOtp(UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Reset User Password (Public)")
    @Test
    void givenNewPassword_whenResetPasswordPublic_thenReturnAppResponse() {
        when(userProfileRepository.findByEmail(any()))
                .thenReturn(Mono.just(userProfile(1)));
        when(verificationRepository.save(any())).thenReturn(Mono.just(verification(1, "009876", OTHERS)));
        when(userRepository.findById(anyInt())).thenReturn(Mono.just(user()));
        when(emailEvent.sendMail(any())).thenReturn(Mono.just(emailRequest("009876")));

        StepVerifier.create(signUpService.resetPasswordRequest(UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Change User Password - Valid Current Password")
    @Test
    void givenNewPassword_whenChangePassword_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(userRepository.save(any())).thenReturn(Mono.just(user()));
        when(passwordEncoder.encode(anyString())).thenReturn(UUID.randomUUID().toString());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        StepVerifier.create(signUpService.changePassword(UUID.randomUUID().toString(),
                        PasswordResetStub.createRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Change User Password - InValid Current Password")
    @Test
    void givenInvalidCurrentPassword_whenChangePassword_thenReturnException() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(signUpService.changePassword(UUID.randomUUID().toString(),
                        PasswordResetStub.createRecord()))
                .expectError(ApiResponseException.class)
                .verify();
    }

    @DisplayName("De-Activate a User Account")
    @Test
    void givenPublicId_whenDeactivate_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(userRepository.save(any())).thenReturn(Mono.just(user()));

        StepVerifier.create(signUpService.deActivateUser(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

}