package org.meldtech.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ClientSettingsRequest(boolean requireProofKey,
                                    boolean requireAuthorizationConsent,
                                    String jwkSetUrl,
                                    JwsAlgorithm authenticationSigningAlgorithm,
                                    String x509CertificateSubjectDN) {}