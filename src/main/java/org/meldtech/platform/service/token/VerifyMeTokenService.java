package org.meldtech.platform.service.token;

import lombok.RequiredArgsConstructor;
import org.meldtech.platform.config.client.HttpConnectorService;
import org.meldtech.platform.model.api.request.signin.AccessToken;
import org.meldtech.platform.service.CompanyService;
import org.meldtech.platform.service.encoding.MessageEncoding;
import org.meldtech.platform.util.AppUtil;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerifyMeTokenService {
    private final LoggerHelper log = LoggerHelper.newInstance(VerifyMeTokenService.class.getName());
    private final HttpConnectorService httpConnectorService;

    @Value("${verification.clientId}")
    private String clientId;
    @Value("${verification.secret}")
    private String clientSecret;
    @Value("${verification.auth.url}")
    private String authorizationUrl;

    public Mono<AccessToken> getAccessToken() {
        var url = authorizationUrl;
        log.trace("Fetching verify me token at: {}", url);
        return httpConnectorService.post(url, new GrantRequest(clientId, clientSecret), headers(), AccessToken.class);
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content_Type", MediaType.APPLICATION_JSON_VALUE);;
        return headers;
    }

    private record GrantRequest(String clientId, String secret) {}
}
