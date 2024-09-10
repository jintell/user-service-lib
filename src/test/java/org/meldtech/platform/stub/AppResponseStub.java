package org.meldtech.platform.stub;

import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.security.core.ClientSettings;
import org.meldtech.platform.model.security.core.TokenSettings;

import java.time.Instant;
import java.util.List;
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

    public static List<OAuth2RegisteredClient> clients() {
        return List.of(create(), create(), create());
    }

    public static AppResponse appResponse() {
        return AppResponse.builder()
                .status(true)
                .message("Operation Successful")
                .data(create())
                .build();
    }
}