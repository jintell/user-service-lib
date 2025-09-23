package org.meldtech.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record OAuth2RegisteredClientRecord(@NotBlank(message = "Client Id must be provided")
                                           String clientId,
                                           String clientIdIssuedAt,
                                           @NotBlank(message = "Client Secret must be provided")
                                           String clientSecret,
                                           String clientSecretExpiresAt,
                                           @NotBlank(message = "Client Name must be provided")
                                           String clientName,
                                           @NotNull(message = "Client Id must be provided")
                                           Set<@Pattern(message = "Authentication mode can be one of of the following: client_secret_basic, client_secret_post, private_key_jwt, client_secret_jwt, and none",
                                                                                              regexp = "client_secret_basic|client_secret_post|private_key_jwt|client_secret_jwt|none")
                                                                                              String> clientAuthenticationMethods,
                                           @NotNull(message = "Authentication grant type must be provided")
                                           Set<@Pattern(message = "Authentication grant type can be one of of the following: authorization_code, client_credentials, and refresh_token",
                                                   regexp = "authorization_code|client_credentials|refresh_token")
                                                   String> authorizationGrantTypes,
                                           Set<String> redirectUris,
                                           Set<String> postLogoutRedirectUris,
                                           Set<String> scopes,
                                           AppRegistrationRequest appRegistration,
                                           ClientSettingsRequest clientSettings,
                                           TokenSettingsRequest tokenSettings) {}