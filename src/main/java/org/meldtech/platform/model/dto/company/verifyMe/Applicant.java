package org.meldtech.platform.model.dto.company.verifyMe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Applicant(@JsonProperty("firstname") String firstName,
                        @JsonProperty("lastname") String lastName,
                        @JsonProperty("phone no") String phoneNo,
                        String paymentMethodCode) {
}