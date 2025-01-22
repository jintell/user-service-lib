package org.meldtech.platform.model.dto;

import lombok.Builder;

@Builder
public record PermissionRecord(String name, String createdOn) {
}
