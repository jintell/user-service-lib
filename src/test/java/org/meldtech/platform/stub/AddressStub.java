package org.meldtech.platform.stub;

import org.meldtech.platform.model.api.request.AddressRequestRecord;
import org.meldtech.platform.model.api.response.AddressRecord;

import java.util.UUID;

public class AddressStub {

    public static AddressRequestRecord requestRecord() {
        return AddressRequestRecord.builder()
                .publicId(UUID.randomUUID().toString())
                .pictureName(UUID.randomUUID().toString())
                .documentName(UUID.randomUUID().toString())
                .addressRecord(AddressRecord.builder()
                        .street("No. 14 Jane Lane")
                        .postCode("102312")
                        .language("English")
                        .city("Festac")
                        .state("Lagos")
                        .country("Nigeria")
                        .build())
                .build();
    }
}
