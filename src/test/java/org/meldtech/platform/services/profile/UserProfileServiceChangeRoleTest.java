package org.meldtech.platform.services.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.domain.User;
import org.meldtech.platform.domain.UserProfile;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.RoleRepository;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.repository.UserRoleRepository;
import org.meldtech.platform.service.UserProfileService;
import org.meldtech.platform.service.crypto.HmacUtil;
import org.meldtech.platform.stub.AppResponseStub;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceChangeRoleTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserProfileRepository profileRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserRoleRepository roleUserRepository;
    @Mock
    org.meldtech.platform.commons.PaginatedResponse paginatedResponse;

    private UserProfileService service;

    private final byte[] secret = "unit-test-secret-key".getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    void setup() throws Exception {
        // Manually construct the service to supply the secret key
        service = new UserProfileService(
                userRepository,
                profileRepository,
                paginatedResponse,
                profileRepository,
                roleRepository,
                roleUserRepository,
                secret
        );
        setField(service, "roleTransitionFrom", "STANDARD");
        setField(service, "roleTransitionTo", "ADMIN");
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    @DisplayName("changeRole: valid HMAC changes permission and returns success AppResponse")
    void shouldChangeRoleOnValidHmac() {
        // Arrange
        User user = AppResponseStub.user();
        String publicId = user.getPublicId();
        String salt = UUID.randomUUID().toString();
        String payload = String.format("%s:%s:%s:%s", publicId, user.getUsername(), "STANDARD", salt);
        String hashB64 = HmacUtil.signToBase64(payload, secret);

        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user));
        when(roleRepository.findByName(eq("ADMIN"))).thenReturn(Mono.just(role(1)));
        when(roleUserRepository.findByUserId(anyInt())).thenReturn(Mono.just(userRole()));
        when(roleUserRepository.save(any())).thenReturn(Mono.just(userRole()));
        when(profileRepository.findById(anyInt())).thenReturn(Mono.just(userProfile(1)));
        when(profileRepository.save(any(UserProfile.class))).thenReturn(Mono.just(userProfile(1)));

        // Act & Assert
        StepVerifier.create(service.changeRole(publicId, hashB64, salt))
                .expectSubscription()
                .expectNextMatches(resp -> resp != null && resp.isStatus())
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("changeRole: invalid HMAC returns ApiResponseException with INVALID_HASH")
    void shouldFailOnInvalidHmac() {
        User user = AppResponseStub.user();
        String publicId = user.getPublicId();
        String salt = UUID.randomUUID().toString();
        String invalidHash = "not-a-valid-base64-signature";

        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(service.changeRole(publicId, invalidHash, salt))
                .expectError(ApiResponseException.class)
                .verify();
    }
}
