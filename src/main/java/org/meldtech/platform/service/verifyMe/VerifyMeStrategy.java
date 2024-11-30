package org.meldtech.platform.service.verifyMe;

import org.meldtech.platform.model.api.response.CompanyRecord;
import org.meldtech.platform.model.dto.company.VerificationRequest;
import reactor.core.publisher.Mono;

public interface VerifyMeStrategy {
    Mono<CompanyRecord> verifyMe(VerificationRequest request);
}
