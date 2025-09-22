package org.meldtech.platform.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.meldtech.platform.domain.AppRegistration;
import org.meldtech.platform.model.dto.AppRegistrationRecord;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppRegistrationConverter {

    public static synchronized AppRegistrationRecord toRecord(AppRegistration entity) {
        return  AppRegistrationRecord.builder()
                .applicationId(entity.applicationId())
                .clientId(entity.clientId())
                .clientName(entity.clientName())
                .clientSecret(entity.clientSecret())
                .redirectUrl(entity.redirectUrl())
                .scope(entity.scope())
                .enabled(entity.enabled())
                .build();
    }

    public static synchronized AppRegistration toEntity(AppRegistrationRecord record) {
        return  AppRegistration.builder()
                .applicationId(record.applicationId())
                .clientId(record.clientId())
                .clientSecret(record.clientSecret())
                .clientName(record.clientName())
                .redirectUrl(record.redirectUrl())
                .scope(record.scope())
                .enabled(record.enabled())
                .build();
    }
}
