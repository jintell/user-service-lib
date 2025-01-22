package org.meldtech.platform.model.dto;

import lombok.Builder;

@Builder
public record RolePermissionRecord(Integer roleId, Object permissions) {
}
