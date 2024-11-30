package org.meldtech.platform.model.dto.company.verifyMe;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Nin(String nin,
                  @JsonProperty("firstname") String firstName,
                  @JsonProperty("lastname") String lastName,
                  @JsonProperty("middlename") String middleName,
                  Residence residence,
                  String phone,
                  String gender,
                  String picture,
                  @JsonProperty("birthdate") String dateOfBirth) {
}