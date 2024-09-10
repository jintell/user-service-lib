package org.meldtech.platform.model.api;

import lombok.Builder;

@Builder
public record PasswordUpdateRecord(String publicId, String newPassword, String hash, String salt) {}

