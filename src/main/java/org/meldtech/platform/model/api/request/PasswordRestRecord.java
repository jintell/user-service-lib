package org.meldtech.platform.model.api.request;

import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
public record PasswordRestRecord(
        @NotNull(message = "Current Password is Required")
        String currentPassword,
        @NotNull(message = "New Password is Required")
        String newPassword) {}
