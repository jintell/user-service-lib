package org.meldtech.platform.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.config.client.HttpConnectorService;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.signin.AccessToken;
import org.meldtech.platform.service.encoding.MessageEncoding;
import org.meldtech.platform.util.AppUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final HttpConnectorService httpConnectorService;

    @Value("${oauth2.client.id}")
    private String clientId;
    @Value("${oauth2.client.secret}")
    private String clientSecret;
    @Value("${oauth2.authorization.url}")
    private String authorizationUrl;
    @Value("${oauth2.authorization.grant_type}")
    private String grantType;
    @Value("${oauth2.authorization.redirect_uri}")
    private String redirectUrl;

    public Mono<AppResponse> exchangeWithAccessToken(String code, String deviceId) {
        return askForExchange(code, deviceId)
                .map(accessToken -> AppUtil.appResponse(accessToken, "User Access token"));
    }

    private Mono<AccessToken> askForExchange(String code, String deviceId) {
        String codeVerifier = AppUtil.USER_DEVICE.get(deviceId);
        var url = authorizationUrl;
        log.trace("Fetching auth token with: {}", url);
        return httpConnectorService.postForm(url, params(code, codeVerifier), headers(), AccessToken.class);
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content_Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Authorization", "Basic " +
                MessageEncoding.base64Encoding(String.format("%s:%s", clientId, clientSecret)) );
        return headers;
    }

    private MultiValueMap<String, String> params(String code, String codeVerifier) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("redirect_uri", redirectUrl);
        params.add("client_id", clientId);
        params.add("code", code);
        params.add("code_verifier", codeVerifier);
        return params;
    }
}
