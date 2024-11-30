package org.meldtech.platform.model.dto.company.verifyMe;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NinCheck(String status, Object fieldMatches) {
}
