package org.meldtech.platform.model.dto;

import lombok.Builder;

@Builder
public record UserSetting(String role, boolean isEmailVerified) {
}
