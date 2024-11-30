package org.meldtech.platform.model.dto.company.verifyMe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Summary(@JsonProperty("cac_check") String cacCheck,
                      @JsonProperty("nin_check") NinCheck ninCheck) {
}
