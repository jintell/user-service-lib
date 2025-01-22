package org.meldtech.platform.domain;

import lombok.Builder;
import org.meldtech.platform.util.AppUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Table("public.permission")
public record Permission(@Id
                         Integer id,
                         String name,
                         Instant createdOn) implements Serializable, Persistable<Integer> {
    public Permission {
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
