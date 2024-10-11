package org.meldtech.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */

@JsonIgnoreProperties
public record UserRecord(String publicId,
                         @NotBlank(message = "Username is Required") String username,
                         @NotBlank(message = "Password is Required") String password,
                         String firstName,
                         String lastName,
                         @NotBlank(message = "Email is Required")
                         @Email(message = "Email should be valid")
                         String email,
                         String phone,
                         String profilePicture,
                         @NotBlank(message = "Valid Role is Required")
                         String role)  {}
