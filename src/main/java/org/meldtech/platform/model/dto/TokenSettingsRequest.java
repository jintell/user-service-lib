package org.meldtech.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TokenSettingsRequest(@Positive(message = "Authorization Code time must be greater than 0")
                                   long authorizationCodeTimeToLive,
                                   @Positive(message = "Access token time must be greater than 0")
                                   long accessTokenTimeToLive,
                                   long deviceCodeTimeToLive,
                                   boolean reuseRefreshTokens,
                                   long refreshTokenTimeToLive,
                                   boolean x509CertificateBoundAccessTokens
                                   ) {}