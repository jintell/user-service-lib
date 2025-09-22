package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.util.AppUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceOwnerService {
    private final OAuth2RegisteredClientService authService;

    @Value("${oauth2.authorize.url}")
    private String authorizeUrl;
    @Value("${oauth2.authorize.challengeMethod}")
    private String challengeMethod;
    @Value("${oauth2.logout.url}")
    private String logoutUrl;

    /**
     * Clients MUST prevent injection (replay) of authorization codes into the authorization response by attackers.
     * Using code_challenge and code_verifier prevents injection of authorization codes since the authorization server
     * will reject a token request with a mismatched code_verifier. See Section 7.6 for more details.
     * @return Mono<String>
     */
    public Mono<String> requestAuthorizedUrl(String deviceId, String appId) {
        System.err.println("Requesting Authorized URL for App:");
        return Mono.just(generateCodeChallenge(deviceId))
                .flatMap(s -> createAuthorizeRequest(s, deviceId, appId));
    }

    public Mono<String> requestLogoutEndpoint() {
        return Mono.just(logoutUrl);
    }

    private Mono<String> createAuthorizeRequest(String challenge, String state, String appId) {
        return authService.getApp(appId)
                        .map(appRegistrationRecord ->
                                String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&" +
                                            "code_challenge=%s&code_challenge_method=%s&state=%s",
                                        authorizeUrl,
                                        appRegistrationRecord.clientId(),
                                        appRegistrationRecord.redirectUrl(),
                                        appRegistrationRecord.scope(),
                                        challenge,
                                        challengeMethod,
                                        state)
                        );
    }

    private String generateCodeChallenge(String deviceId) {
        try {
            String codeVerifier = generateCodeVerifier();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            String encodedVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            AppUtil.USER_DEVICE.put(deviceId, codeVerifier);
            return encodedVerifier;
        } catch (NoSuchAlgorithmException var6) {
            return  "";
        }
    }

    private String generateCodeVerifier() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
