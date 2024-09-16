package org.meldtech.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileRecord (@NotBlank(message = "First name must be provided")
                                 String firstName,
                                 String middleName,
                                 @NotBlank(message = "Last name must be provided")
                                 String lastName,
                                 @NotBlank(message = "Email must be provided")
                                 String email,
                                 String phoneNumber,
                                 String companyName,
                                 String website,
                                 String language,
                                 String profilePicture,
                                 String settings,
                                 Integer userid,
                                 Instant createdOn){}
