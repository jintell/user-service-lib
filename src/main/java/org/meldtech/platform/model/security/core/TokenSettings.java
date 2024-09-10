package org.meldtech.platform.model.security.core;

import org.meldtech.platform.model.security.format.OAuth2TokenFormat;
import org.meldtech.platform.model.security.settings.AbstractSettings;
import org.meldtech.platform.model.security.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static org.meldtech.platform.model.security.settings.ConfigurationSettingNames.Token.*;

public final class TokenSettings extends AbstractSettings {
    private TokenSettings(Map<String, Object> settings) {
        super(settings);
    }

    public Duration getAuthorizationCodeTimeToLive() {
        return this.getSetting(AUTHORIZATION_CODE_TIME_TO_LIVE);
    }

    public Duration getAccessTokenTimeToLive() {
        return this.getSetting(ACCESS_TOKEN_TIME_TO_LIVE);
    }

    public OAuth2TokenFormat getAccessTokenFormat() {
        return this.getSetting(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT);
    }

    public Duration getDeviceCodeTimeToLive() {
        return this.getSetting(ConfigurationSettingNames.Token.DEVICE_CODE_TIME_TO_LIVE);
    }

    public boolean isReuseRefreshTokens() {
        return this.getSetting(ConfigurationSettingNames.Token.REUSE_REFRESH_TOKENS);
    }

    public Duration getRefreshTokenTimeToLive() {
        return this.getSetting(ConfigurationSettingNames.Token.REFRESH_TOKEN_TIME_TO_LIVE);
    }

    public SignatureAlgorithm getIdTokenSignatureAlgorithm() {
        return this.getSetting(ConfigurationSettingNames.Token.ID_TOKEN_SIGNATURE_ALGORITHM);
    }

    public boolean isX509CertificateBoundAccessTokens() {
        return this.getSetting(X509_CERTIFICATE_BOUND_ACCESS_TOKENS);
    }

    public static TokenSettings.Builder builder() {
        return (new TokenSettings.Builder()).authorizationCodeTimeToLive(Duration.ofMinutes(5L)).accessTokenTimeToLive(Duration.ofMinutes(5L)).accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).deviceCodeTimeToLive(Duration.ofMinutes(5L)).reuseRefreshTokens(true).refreshTokenTimeToLive(Duration.ofMinutes(60L)).idTokenSignatureAlgorithm(SignatureAlgorithm.RS256).x509CertificateBoundAccessTokens(false);
    }

    public static TokenSettings.Builder withSettings(Map<String, Object> settings) {
        Assert.notEmpty(settings, "settings cannot be empty");
        return (TokenSettings.Builder)(new TokenSettings.Builder()).settings((s) -> {
            s.putAll(settings);
        });
    }

    public static final class Builder extends AbstractSettings.AbstractBuilder<TokenSettings, TokenSettings.Builder> {
        private Builder() {
        }

        public TokenSettings.Builder authorizationCodeTimeToLive(Duration authorizationCodeTimeToLive) {
            Assert.notNull(authorizationCodeTimeToLive, "authorizationCodeTimeToLive cannot be null");
            Assert.isTrue(authorizationCodeTimeToLive.getSeconds() > 0L, "authorizationCodeTimeToLive must be greater than Duration.ZERO");
            return (TokenSettings.Builder)this.setting(AUTHORIZATION_CODE_TIME_TO_LIVE, authorizationCodeTimeToLive);
        }

        public TokenSettings.Builder accessTokenTimeToLive(Duration accessTokenTimeToLive) {
            Assert.notNull(accessTokenTimeToLive, "accessTokenTimeToLive cannot be null");
            Assert.isTrue(accessTokenTimeToLive.getSeconds() > 0L, "accessTokenTimeToLive must be greater than Duration.ZERO");
            return (TokenSettings.Builder)this.setting(ACCESS_TOKEN_TIME_TO_LIVE, accessTokenTimeToLive);
        }

        public TokenSettings.Builder accessTokenFormat(OAuth2TokenFormat accessTokenFormat) {
            Assert.notNull(accessTokenFormat, "accessTokenFormat cannot be null");
            return (TokenSettings.Builder)this.setting(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT, accessTokenFormat);
        }

        public TokenSettings.Builder deviceCodeTimeToLive(Duration deviceCodeTimeToLive) {
            Assert.notNull(deviceCodeTimeToLive, "deviceCodeTimeToLive cannot be null");
            Assert.isTrue(deviceCodeTimeToLive.getSeconds() > 0L, "deviceCodeTimeToLive must be greater than Duration.ZERO");
            return (TokenSettings.Builder)this.setting(ConfigurationSettingNames.Token.DEVICE_CODE_TIME_TO_LIVE, deviceCodeTimeToLive);
        }

        public TokenSettings.Builder reuseRefreshTokens(boolean reuseRefreshTokens) {
            return (TokenSettings.Builder)this.setting(ConfigurationSettingNames.Token.REUSE_REFRESH_TOKENS, reuseRefreshTokens);
        }

        public TokenSettings.Builder refreshTokenTimeToLive(Duration refreshTokenTimeToLive) {
            Assert.notNull(refreshTokenTimeToLive, "refreshTokenTimeToLive cannot be null");
            Assert.isTrue(refreshTokenTimeToLive.getSeconds() > 0L, "refreshTokenTimeToLive must be greater than Duration.ZERO");
            return (TokenSettings.Builder)this.setting(ConfigurationSettingNames.Token.REFRESH_TOKEN_TIME_TO_LIVE, refreshTokenTimeToLive);
        }

        public TokenSettings.Builder idTokenSignatureAlgorithm(SignatureAlgorithm idTokenSignatureAlgorithm) {
            Assert.notNull(idTokenSignatureAlgorithm, "idTokenSignatureAlgorithm cannot be null");
            return (TokenSettings.Builder)this.setting(ConfigurationSettingNames.Token.ID_TOKEN_SIGNATURE_ALGORITHM, idTokenSignatureAlgorithm);
        }

        public TokenSettings.Builder x509CertificateBoundAccessTokens(boolean x509CertificateBoundAccessTokens) {
            return (TokenSettings.Builder)this.setting(X509_CERTIFICATE_BOUND_ACCESS_TOKENS, x509CertificateBoundAccessTokens);
        }

        public TokenSettings build() {
            return new TokenSettings(this.getSettings());
        }
    }

    @Override
    public String toString() {
        return strip("{" +
                "\"@class\":\"java.util.Collections$UnmodifiableMap\"," +
                "\""+AUTHORIZATION_CODE_TIME_TO_LIVE+"\":[\"java.time.Duration\"," + getAuthorizationCodeTimeToLive().getSeconds() + "]," +
                "\""+ACCESS_TOKEN_TIME_TO_LIVE+"\":[\"java.time.Duration\"," + getAccessTokenTimeToLive().getSeconds() + "]," +
                "\""+ACCESS_TOKEN_FORMAT+"\":" +
                "{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\"," +
                "\"value\":\"self-contained\"}," +
                (Objects.nonNull(getDeviceCodeTimeToLive())
                        && getDeviceCodeTimeToLive().getSeconds() - 5L > 0L ?
                "\""+DEVICE_CODE_TIME_TO_LIVE+"\":[\"java.time.Duration\"," + getDeviceCodeTimeToLive().getSeconds() + "]," : "") +
                "\""+REUSE_REFRESH_TOKENS+"\":" + isReuseRefreshTokens() + "," +
                "\""+REFRESH_TOKEN_TIME_TO_LIVE+"\":[\"java.time.Duration\"," + getRefreshTokenTimeToLive().getSeconds() + "]," +
                "\""+ID_TOKEN_SIGNATURE_ALGORITHM+"\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\""
                + getIdTokenSignatureAlgorithm() + "\"]," +
                (isX509CertificateBoundAccessTokens() ?
                "\""+X509_CERTIFICATE_BOUND_ACCESS_TOKENS+"\":" + isX509CertificateBoundAccessTokens() : "" ) +
                "}");
    }

    private String strip(String value) {
        int index = 0;
        if(value.charAt(value.length()-2) ==',') {
            index = value.lastIndexOf(',');
            return value.substring(0, index) + value.substring(index + 1);
        }
        return value;
    }
}
