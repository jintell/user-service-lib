package org.meldtech.platform.model.security.core;

import org.meldtech.platform.model.security.settings.AbstractSettings;
import org.meldtech.platform.model.security.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

import static org.meldtech.platform.model.security.settings.ConfigurationSettingNames.Client.*;

public final class ClientSettings extends AbstractSettings {
    private ClientSettings(Map<String, Object> settings) {
        super(settings);
    }

    public boolean isRequireProofKey() {
        return this.getSetting(REQUIRE_PROOF_KEY);
    }

    public boolean isRequireAuthorizationConsent() {
        return this.getSetting(REQUIRE_AUTHORIZATION_CONSENT);
    }

    public String getJwkSetUrl() {
        return this.getSetting(ConfigurationSettingNames.Client.JWK_SET_URL);
    }

    public JwsAlgorithm getTokenEndpointAuthenticationSigningAlgorithm() {
        return this.getSetting(ConfigurationSettingNames.Client.TOKEN_ENDPOINT_AUTHENTICATION_SIGNING_ALGORITHM);
    }

    public String getX509CertificateSubjectDN() {
        return this.getSetting(X509_CERTIFICATE_SUBJECT_DN);
    }

    public static ClientSettings.Builder builder() {
        return (new ClientSettings.Builder()).requireProofKey(false).requireAuthorizationConsent(false);
    }

    public static ClientSettings.Builder withSettings(Map<String, Object> settings) {
        Assert.notEmpty(settings, "settings cannot be empty");
        return (ClientSettings.Builder)(new ClientSettings.Builder()).settings((s) -> {
            s.putAll(settings);
        });
    }

    public static final class Builder extends AbstractSettings.AbstractBuilder<ClientSettings, ClientSettings.Builder> {
        private Builder() {
        }

        public ClientSettings.Builder requireProofKey(boolean requireProofKey) {
            return (ClientSettings.Builder)this.setting(REQUIRE_PROOF_KEY, requireProofKey);
        }

        public ClientSettings.Builder requireAuthorizationConsent(boolean requireAuthorizationConsent) {
            return (ClientSettings.Builder)this.setting(REQUIRE_AUTHORIZATION_CONSENT, requireAuthorizationConsent);
        }

        public ClientSettings.Builder jwkSetUrl(String jwkSetUrl) {
            return (ClientSettings.Builder)this.setting(ConfigurationSettingNames.Client.JWK_SET_URL, jwkSetUrl);
        }

        public ClientSettings.Builder tokenEndpointAuthenticationSigningAlgorithm(JwsAlgorithm authenticationSigningAlgorithm) {
            return (ClientSettings.Builder)this.setting(ConfigurationSettingNames.Client.TOKEN_ENDPOINT_AUTHENTICATION_SIGNING_ALGORITHM, authenticationSigningAlgorithm);
        }

        public ClientSettings.Builder x509CertificateSubjectDN(String x509CertificateSubjectDN) {
            return (ClientSettings.Builder)this.setting(X509_CERTIFICATE_SUBJECT_DN, x509CertificateSubjectDN);
        }

        public ClientSettings build() {
            return new ClientSettings(this.getSettings());
        }
    }

    @Override
    public String toString() {
        return strip("{" +
                "\"@class\":\"java.util.Collections$UnmodifiableMap\"," +
                "\""+REQUIRE_PROOF_KEY+"\":"+isRequireProofKey()+"," +
                "\""+REQUIRE_AUTHORIZATION_CONSENT+"\":"+isRequireAuthorizationConsent()+ ","+
                (Objects.nonNull(getJwkSetUrl()) && !getJwkSetUrl().isEmpty() ?
                        "\""+JWK_SET_URL+"\":\"" +getJwkSetUrl() + "\"," : "") +
                (Objects.nonNull(getTokenEndpointAuthenticationSigningAlgorithm())?
                "\""+TOKEN_ENDPOINT_AUTHENTICATION_SIGNING_ALGORITHM+"\":"+getTokenEndpointAuthenticationSigningAlgorithm()
                        + "," : "") +
                (Objects.nonNull(getX509CertificateSubjectDN()) && !getX509CertificateSubjectDN().isEmpty()?
                "\""+X509_CERTIFICATE_SUBJECT_DN+"\":\""+getX509CertificateSubjectDN() +"\"" : "") +
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
