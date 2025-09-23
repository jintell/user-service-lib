package org.meldtech.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AppRegistrationRequest(String appLoginUrl,
                                     String appLogoutUrl,
                                     String appResolvedPathUrl) {
}
