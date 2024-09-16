package org.meldtech.platform.stub;

import org.meldtech.platform.model.api.request.PasswordRestRecord;

import java.util.UUID;

public class PasswordResetStub {
    public static PasswordRestRecord createRecord() {
        return new PasswordRestRecord(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    };
}
