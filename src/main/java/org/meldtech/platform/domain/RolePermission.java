package org.meldtech.platform.domain;

import io.r2dbc.postgresql.codec.Json;
import lombok.Builder;
import org.meldtech.platform.util.AppUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Table("public.role_permission")
public record RolePermission(@Id Integer id,
                             Integer roleId,
                             Json permission,
                             Instant createdOn) implements Serializable, Persistable<Integer> {
    public RolePermission {
        if(isNew()) { createdOn = Instant.now(); }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return AppUtil.isNewRecord(id);
    }
}
