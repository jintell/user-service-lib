package org.meldtech.platform.converter;

import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.domain.RolePermission;
import org.meldtech.platform.model.dto.RolePermissionRecord;

import java.util.Objects;

import static org.meldtech.platform.util.AppUtil.convertToType;

@Slf4j
public class RolePermissionConverter {
    private RolePermissionConverter() {}

    public static synchronized RolePermissionRecord mapToRecord(RolePermission rolePermission) {
        return RolePermissionRecord.builder()
                .roleId(rolePermission.roleId())
                .permissions(Objects.isNull(rolePermission.permission()) ? null :
                        convertToType(rolePermission.permission().asString(), Object.class))
                .build();
    }
}
