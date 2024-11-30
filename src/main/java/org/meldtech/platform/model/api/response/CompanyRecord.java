package org.meldtech.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompanyRecord(String idNumber,
                            String name,
                            String address,
                            String type,
                            Object details,
                            String createdOn) {}