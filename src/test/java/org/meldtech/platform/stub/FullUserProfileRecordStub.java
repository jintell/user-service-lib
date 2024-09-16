package org.meldtech.platform.stub;

import org.meldtech.platform.model.api.request.UserProfileRecord;
import org.meldtech.platform.model.api.response.FullUserProfileRecord;

import java.util.UUID;

public class FullUserProfileRecordStub {
    public static FullUserProfileRecord createFullUserProfileRecord() {
        return FullUserProfileRecord.builder()
                .publicId(UUID.randomUUID().toString())
                .profile(UserProfileRecord.builder()
                        .email("test@test.com")
                        .firstName("John")
                        .lastName("Doe")
                        .middleName("Smith")
                        .phoneNumber(UUID.randomUUID().toString())
                        .profilePicture(UUID.randomUUID().toString())
                        .companyName(UUID.randomUUID().toString())
                        .website(UUID.randomUUID().toString())
                        .language(UUID.randomUUID().toString())
                        .settings(null)
                        .build())
                .build();
    }
}
