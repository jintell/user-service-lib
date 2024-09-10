package org.meldtech.platform.model.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.model.dto.TokenSettingsRequest;
import org.meldtech.platform.model.security.core.TokenSettings;
import org.meldtech.platform.model.security.format.OAuth2TokenFormat;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenSettingsConverter {

    public static TokenSettings convertTo(TokenSettingsRequest request){
        return TokenSettings.builder()
                .authorizationCodeTimeToLive(time(request.authorizationCodeTimeToLive()))
                .accessTokenTimeToLive(time(request.accessTokenTimeToLive()))
                .refreshTokenTimeToLive(time(request.refreshTokenTimeToLive()))
                .reuseRefreshTokens(request.reuseRefreshTokens())
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .deviceCodeTimeToLive(time(request.deviceCodeTimeToLive()))
                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                .x509CertificateBoundAccessTokens(request.x509CertificateBoundAccessTokens())
                .build();
    }

    public static TokenSettingsRequest convertTo(Map<String, Object> settings){
        if(Objects.isNull(settings) || settings.isEmpty()) return TokenSettingsRequest.builder().build();
        return TokenSettingsRequest.builder()
                .authorizationCodeTimeToLive(duration(settings.get("settings.token.authorization-code-time-to-live")))
                .accessTokenTimeToLive(duration(settings.get("settings.token.access-token-time-to-live")))
                .reuseRefreshTokens((Boolean) settings.getOrDefault("settings.token.reuse-refresh-tokens", false))
                .refreshTokenTimeToLive(duration(settings.get("settings.token.refresh-token-time-to-live")))
                .deviceCodeTimeToLive(duration(settings.get("settings.token.device-code-time-to-live")))
                .x509CertificateBoundAccessTokens((Boolean) settings.getOrDefault("settings.token.x509-certificate-bound-access-tokens", false))
                .build();
    }

    private static Duration time(long time) {
        return time > 0? Duration.ofSeconds(time) : Duration.ofSeconds(5L);
    }

    @SuppressWarnings("unchecked")
    private static Long duration(Object time) {
        if(Objects.isNull(time)) return 0L;
        return (long) ((List<Integer>) time).get(0);
    }
}