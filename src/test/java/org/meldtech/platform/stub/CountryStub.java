package org.meldtech.platform.stub;

import org.meldtech.platform.model.api.response.CountryRecord;

import java.util.List;

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
}
