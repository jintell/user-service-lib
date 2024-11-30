package org.meldtech.platform.model.dto.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record VerificationRequest(@JsonProperty("regNumber") String regNumber,
                                  @JsonProperty("firstname")
                                  String firstName,
                                  @JsonProperty("lastname")
                                  String lastName,
                                  @JsonProperty("phone_no")
                                  String phoneNo) {}