package org.meldtech.platform.model.dto;

import lombok.Builder;

@Builder
public record AppRegistrationRecord(String applicationId,
                                    String clientName,
                                    String clientId,
                                    String clientSecret,
                                    String redirectUrl,
                                    String appLoginUrl,
                                    String appLogoutUrl,
                                    String appResolvedPathUrl,
                                    String scope,
                                    boolean enabled) {
}
