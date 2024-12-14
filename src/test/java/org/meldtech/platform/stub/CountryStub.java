package org.meldtech.platform.stub;

import org.meldtech.platform.model.api.response.CountryRecord;
import org.meldtech.platform.model.dto.company.VerificationRequest;

import java.util.List;
import java.util.UUID;

public class CountryStub {

    public static CountryRecord createCountryRecord(String name) {
        return CountryRecord.builder()
                .name(name)
                .currency("Dollars")
                .language(List.of("English", "Spanish"))
                .build();
    }

    public static List<CountryRecord> countryRecordList() {
        return List.of(createCountryRecord("USA"),
                createCountryRecord("Canada"),
                createCountryRecord("Australia"));
    }

    public static VerificationRequest verificationRequest() {
        return VerificationRequest.builder()
                .regNumber(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .phoneNo(UUID.randomUUID().toString())
                .build();
    }


}
