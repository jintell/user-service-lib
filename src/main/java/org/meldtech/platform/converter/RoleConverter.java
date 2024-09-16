package org.meldtech.platform.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.meldtech.platform.domain.Country;
import org.meldtech.platform.domain.Role;
import org.meldtech.platform.model.api.response.CountryRecord;
import org.meldtech.platform.model.api.response.RoleRecord;
import org.meldtech.platform.util.AppUtil;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleConverter {

    public static synchronized RoleRecord mapToRecord(Role entity) {
        return  RoleRecord.builder()
                    .name(entity.getName())
                    .build();
    }
}
