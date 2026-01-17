package org.meldtech.platform.service.verifyMe.impl;

import lombok.RequiredArgsConstructor;
import org.meldtech.platform.config.client.HttpConnectorService;
import org.meldtech.platform.converter.CompanyConverter;
import org.meldtech.platform.domain.Company;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.request.signin.AccessToken;
import org.meldtech.platform.model.api.response.CompanyRecord;
import org.meldtech.platform.model.dto.company.CacRecord;
import org.meldtech.platform.model.dto.company.NinRecord;
import org.meldtech.platform.model.dto.company.VerificationRequest;
import org.meldtech.platform.repository.CompanyRepository;
import org.meldtech.platform.service.token.VerifyMeTokenService;
import org.meldtech.platform.service.verifyMe.VerifyMeStrategy;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class NinVerify implements VerifyMeStrategy {
    private final LoggerHelper log = LoggerHelper.newInstance(CacVerify.class.getName());
    private final CompanyRepository companyRepository;
    private final HttpConnectorService httpConnectorService;;
    private final VerifyMeTokenService tokenService;

    @Value("${verification.agent.url}")
    private String agentUrl;
    private static final String INVALID_COMPANY = "The identifier provided is invalid";

    @Override
    public Mono<CompanyRecord> verifyMe(VerificationRequest request) {
        log.info("NIN -- VerifyMe request: ", request);
        return tokenService.getAccessToken()
                .flatMap(accessToken -> performVerification(request, accessToken))
                .map(ninRecord ->  CompanyConverter.mapToRecord(ninRecord, request.address()))
                .map(CompanyConverter::mapToEntity)
                .flatMap(this::saveResponse)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(INVALID_COMPANY), BAD_REQUEST.value()));

    }

    private Mono<NinRecord> performVerification(VerificationRequest request, AccessToken accessToken) {
        var url = String.format("%s%s", agentUrl, request.regNumber());
        return httpConnectorService.post(url, request, headers(accessToken.getAccessTokenV2()), NinRecord.class);
    }

    private Mono<CompanyRecord> saveResponse(Company company) {
        return companyRepository.save(company)
                .map(CompanyConverter::mapToRecord);
    }

    private Map<String, String> headers(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);;
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
