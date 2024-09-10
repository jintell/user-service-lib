package org.meldtech.platform.stub;

import org.meldtech.platform.model.dto.ClientSettingsRequest;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientRecord;
import org.meldtech.platform.model.dto.TokenSettingsRequest;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.util.Set;
import java.util.UUID;

public class ClientRequestStub {

    public static String REDIRECT_URI = "https://meldtech.io";

    public static OAuth2RegisteredClientRecord getClientRecord() {
        return OAuth2RegisteredClientRecord.builder()
                .clientId(UUID.randomUUID().toString())
                .clientSecret(UUID.randomUUID().toString())
                .clientName("Chocolate")
                .clientAuthenticationMethods(Set.of("private_client_jwk"))
                .authorizationGrantTypes(Set.of("authorization_code"))
                .scopes(Set.of("open_id"))
                .redirectUris(Set.of(REDIRECT_URI + "/HOME"))
                .postLogoutRedirectUris(Set.of(REDIRECT_URI))
                .clientSettings(ClientSettingsRequest.builder().build())
                .tokenSettings(TokenSettingsRequest.builder().build())
                .build();
    }
}
