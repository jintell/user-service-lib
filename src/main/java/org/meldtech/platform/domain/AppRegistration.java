package org.meldtech.platform.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("public.app_registered_client")
public record AppRegistration(@Id
                              Long id,
                              String applicationId,
                              String clientName,
                              String clientId,
                              String clientSecret,
                              String redirectUrl,
                              String scope,
                              boolean enabled,
                              Instant createdOn) implements Persistable<Long> {
    @Builder
    public AppRegistration {
        if(isNew()) { createdOn = Instant.now(); }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
