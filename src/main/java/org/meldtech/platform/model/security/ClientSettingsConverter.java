package org.meldtech.platform.model.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.model.dto.ClientSettingsRequest;
import org.meldtech.platform.model.security.core.ClientSettings;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;

import java.util.Map;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientSettingsConverter {

    public static ClientSettings convertTo(ClientSettingsRequest request){
        return ClientSettings.builder()
                .requireProofKey(request.requireProofKey())
                .requireAuthorizationConsent(request.requireAuthorizationConsent())
                .jwkSetUrl(Objects.nonNull(request.jwkSetUrl())?request.jwkSetUrl():"")
                .tokenEndpointAuthenticationSigningAlgorithm(request.authenticationSigningAlgorithm())
                .x509CertificateSubjectDN(Objects.nonNull(request.x509CertificateSubjectDN())?
                        request.x509CertificateSubjectDN() : "")
                .build();
    }

    public static ClientSettingsRequest convertTo(Map<String, Object> settings){
        if(Objects.isNull(settings) || settings.isEmpty()) return ClientSettingsRequest.builder().build();
            return ClientSettingsRequest.builder()
                .requireProofKey((Boolean) settings.getOrDefault("settings.client.require-proof-key",false))
                .requireAuthorizationConsent((Boolean) settings.getOrDefault("settings.client.require-authorization-consent",false))
                .jwkSetUrl((String) settings.get("settings.client.jwk-set-url"))
                .authenticationSigningAlgorithm((JwsAlgorithm) settings.get("settings.client.x509-certificate-subject-dn"))
                .build();
    }
}
