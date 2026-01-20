package org.meldtech.platform.stub;

import io.r2dbc.postgresql.codec.Json;
import org.meldtech.platform.domain.*;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.UserProfileRecord;
import org.meldtech.platform.model.api.request.UserRecord;
import org.meldtech.platform.model.api.response.CompanyRecord;
import org.meldtech.platform.model.api.response.FullUserProfileRecord;
import org.meldtech.platform.model.constant.VerificationType;
import org.meldtech.platform.model.dto.*;
import org.meldtech.platform.model.dto.company.VerificationRequest;
import org.meldtech.platform.model.event.EmailTemplate;
import org.meldtech.platform.model.event.GenericRequest;
import org.meldtech.platform.model.security.core.ClientSettings;
import org.meldtech.platform.model.security.core.TokenSettings;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.util.AppUtil.convertToType;

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

    public static OAuth2RegisteredClientResponse clientResponse() {
        return OAuth2RegisteredClientResponse
                .builder()
                .appRegistration(AppRegistrationRecord.builder()
                        .applicationId(UUID.randomUUID().toString())
                        .clientId(UUID.randomUUID().toString())
                        .clientName("Chocolate Test")
                        .clientSecret(UUID.randomUUID().toString())
                        .scope("open_id")
                        .redirectUrl("http://localhost:9000/home")
                        .enabled(true)
                        .build())
                .client(OAuth2RegisteredClientRecord.builder()
                        .clientId(UUID.randomUUID().toString())
                        .clientName("Chocolate Test")
                        .clientSecret(UUID.randomUUID().toString())
                        .scopes(Collections.singleton("open_id"))
                        .redirectUris(Collections.singleton("http://localhost:9000/home"))
                        .postLogoutRedirectUris(Collections.singleton("http://localhost:9000/sign-in"))
                        .authorizationGrantTypes(Collections.singleton("authorization_code"))
                        .clientAuthenticationMethods(Collections.singleton("private_secret_jwk"))
                        .clientSettings(ClientSettingsRequest.builder().build())
                        .tokenSettings(TokenSettingsRequest.builder().build())
                        .build())
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
                .settings(Json.of(convertToType(UserSetting.builder()
                        .isEmailVerified(true)
                        .role("STANDARD")
                        .build(), String.class)))
                .build();
    }

    public static FullUserProfileRecord userProfileRecord() {
        return FullUserProfileRecord
                .builder()
                .username("Name"+UUID.randomUUID().toString())
                .publicId(UUID.randomUUID().toString())
                .profile(UserProfileRecord.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .middleName("Smith")
                        .phoneNumber(UUID.randomUUID().toString())
                        .profilePicture(UUID.randomUUID().toString())
                        .email(UUID.randomUUID().toString())
                        .companyName(UUID.randomUUID().toString())
                        .settings(null)
                        .build())
                .build();
    }

    public static UserProfileRecord profileRecord() {
        return UserProfileRecord.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .middleName("Smith")
                        .phoneNumber(UUID.randomUUID().toString())
                        .profilePicture(UUID.randomUUID().toString())
                        .email(UUID.randomUUID().toString())
                        .companyName(UUID.randomUUID().toString())
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

    public static List<User> users() {
        return List.of(user(), user(), user());
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
        return new UserRecord(UUID.randomUUID().toString(),
                "test@test.com",
                UUID.randomUUID().toString(),
                "smith",
                "Rowe",
                "test@test.com",
                "+234815667281",
                UUID.randomUUID().toString(),
                "USER",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
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

    public static UserRole userRoles() {
        return UserRole.builder()
                .id(1)
                .roleId(1)
                .userId(1)
                .build();
    }

    public static RolePermission rolePermission() {
        return RolePermission.builder()
                .id(1)
                .roleId(1)
                .permission(null)
                .build();
    }

    public static List<Country> countries() {
        return List.of(country(1), country(2), country(3));
    }

    public static Company company(int id) {
        return Company.builder()
                .id(id)
                .idNumber(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .address(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                .details(null)
                .createdOn(Instant.now())
                .build();
    }

    public static CompanyRecord companyRecord(int id) {
        return CompanyRecord.builder()
                .idNumber(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .address(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                .details(null)
                .build();
    }

    public static VerificationRequest verifyRequest() {
        return new VerificationRequest(UUID.randomUUID().toString(), "","", "", "");
    }

    public static AppRegistration appRegistration() {
        return AppRegistration
                .builder()
                .id(1L)
                .clientId(UUID.randomUUID().toString())
                .clientName("Chocolate Test")
                .clientSecret(UUID.randomUUID().toString())
                .scope("open_id")
                .redirectUrl("http://localhost:9000/home")
                .applicationId(UUID.randomUUID().toString())
                .enabled(true)
                .build();
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