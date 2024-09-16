package org.meldtech.platform.stub;

import org.meldtech.platform.domain.*;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.UserRecord;
import org.meldtech.platform.model.constant.VerificationType;
import org.meldtech.platform.model.event.EmailTemplate;
import org.meldtech.platform.model.event.GenericRequest;
import org.meldtech.platform.model.security.core.ClientSettings;
import org.meldtech.platform.model.security.core.TokenSettings;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AppResponseStub {

    public static OAuth2RegisteredClient create() {
        return OAuth2RegisteredClient
                .builder()
                .id(UUID.randomUUID().toString())
                .clientId(UUID.randomUUID().toString())
                .clientName("Chocolate Test")
                .clientSecret(UUID.randomUUID().toString())
                .clientIdIssuedAt(Instant.now())
                .scopes("open_id")
                .redirectUris("http://localhost:9000/home")
                .postLogoutRedirectUris("http://localhost:9000/sign-in")
                .authorizationGrantTypes("authorization_code")
                .clientAuthenticationMethods("private_secret_jwk")
                .clientSettings(ClientSettings.builder().build().toString())
                .tokenSettings(TokenSettings.builder().build().toString())
                .build();
    }

    public static UserProfile userProfile(int id) {
        return UserProfile
                .builder()
                .id(id)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .phoneNumber(UUID.randomUUID().toString())
                .profilePicture(UUID.randomUUID().toString())
                .companyName(UUID.randomUUID().toString())
                .website(UUID.randomUUID().toString())
                .language(UUID.randomUUID().toString())
                .settings(null)
                .build();
    }

    public static Role role(int id) {
        return Role
                .builder()
                .id(id)
                .name("ROLE_ADMIN")
                .createdOn(Instant.now())
                .build();
    }

    public static User user() {
        return User
                .builder()
                .id(1)
                .username("test@test.com")
                .password(UUID.randomUUID().toString())
                .enabled(true)
                .publicId(UUID.randomUUID().toString())
                .build();
    }

    public static UserRole userRole() {
        return UserRole
                .builder()
                .id(1)
                .roleId(1)
                .userId(1)
                .build();
    }

    public static Verification verification(int id, String otp, VerificationType OTHERS) {
        return Verification
                .builder()
                .id(1)
                .userId(id)
                .type(OTHERS.name())
                .durationInHours(1)
                .userOtp(otp)
                .createdOn(Instant.now())
                .build();
    }

    public static Country country(int id) {
        return Country
                .builder()
                .id(id)
                .name(UUID.randomUUID().toString())
                .language("[\"English\", \"Spanish\"]")
                .currency(UUID.randomUUID().toString())
                .createdOn(Instant.now())
                .build();
    }

    public static Address address(int id) {
        return Address
                .builder()
                .id(id)
                .postCode(UUID.randomUUID().toString())
                .street(UUID.randomUUID().toString())
                .city(UUID.randomUUID().toString())
                .state(UUID.randomUUID().toString())
                .country(UUID.randomUUID().toString())
                .language(UUID.randomUUID().toString())
                .userId(id)
                .createdOn(Instant.now())
                .build();
    }

    public static Boolean emailRequest(String otp) {
        return Objects.nonNull(GenericRequest.builder()
                .to("test@test.com")
                .templateId(UUID.randomUUID().toString())
                .template(EmailTemplate.builder()
                        .otp(otp)
                        .link(UUID.randomUUID().toString())
                        .username("test@test.com")
                        .password(UUID.randomUUID().toString())
                        .build())
                .build() );
    }


    public static UserRecord userRecord() {
        return new UserRecord(UUID.randomUUID().toString(), "test@test.com", UUID.randomUUID().toString(),
                "test@test.com", "+234815667281", "USER");
    }



    public static List<OAuth2RegisteredClient> clients() {
        return List.of(create(), create(), create());
    }

    public static List<UserProfile> userProfiles() {
        return List.of(userProfile(1), userProfile(2), userProfile(3));
    }

    public static List<Role> roles() {
        return List.of(role(1), role(2), role(3));
    }

    public static List<Country> countries() {
        return List.of(country(1), country(2), country(3));
    }

    public static <T> AppResponse appResponse(T item) {
        return AppResponse.builder()
                .status(true)
                .message("Operation Successful")
                .data(item)
                .build();
    }

    public static <T> AppResponse appResponses(List<T> item) {
        return AppResponse.builder()
                .status(true)
                .message("Operation Successful")
                .data(item)
                .build();
    }
}