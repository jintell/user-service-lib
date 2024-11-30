package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import org.meldtech.platform.converter.CompanyConverter;
import org.meldtech.platform.domain.Company;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.constant.VerifyType;
import org.meldtech.platform.model.dto.company.VerificationRequest;
import org.meldtech.platform.repository.CompanyRepository;
import org.meldtech.platform.service.verifyMe.VerifyStrategyFactory;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final LoggerHelper log = LoggerHelper.newInstance(CompanyService.class.getName());
    private final CompanyRepository companyRepository;
    private final VerifyStrategyFactory verifyMeStrategy;
    private static final String COMPANY_MSG = "Company Record request returned successfully";
    private static final String INVALID_COMPANY = "The identifier provided is invalid";

    public Mono<AppResponse> verifyIdentity(VerificationRequest verifyRequest, VerifyType type) {
        log.info("Verifying identity record ", verifyRequest, " for ", type);
        return getCompany(verifyRequest, type);
    }

    private Mono<AppResponse> getCompany(VerificationRequest verifyRequest, VerifyType type) {
        log.info("Getting Company Record by ", verifyRequest.regNumber());
        return validateCompany(verifyRequest.regNumber().replace("RC", ""))
                .map(CompanyConverter::mapToRecord)
                .map(countryRecord -> appResponse(countryRecord, COMPANY_MSG))
                .switchIfEmpty(doIdentityVerification(verifyRequest, type));
    }

    private Mono<AppResponse> doIdentityVerification(VerificationRequest verifyRequest, VerifyType type) {
        log.info("Verifying identity record ", verifyRequest, " for ", type);
        return verifyMeStrategy.getVerifyMeStrategy(type.name())
                .verifyMe(verifyRequest)
                .map(company -> appResponse(company, COMPANY_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    private Mono<Company> validateCompany(String idNumber) {
        return companyRepository.findByIdNumber(idNumber);
    }
}
