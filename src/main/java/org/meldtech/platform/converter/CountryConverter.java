package org.meldtech.platform.converter;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.meldtech.platform.domain.Country;
import org.meldtech.platform.model.api.response.CountryRecord;
import org.meldtech.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public class CountryConverter {
    private CountryConverter() {}

    public static synchronized Country mapToEntity(CountryRecord dto) {
        try {
            return Country.builder()
                    .name(dto.name())
                    .currency(dto.currency())
                    .language(AppUtil.getMapper().writeValueAsString(dto.language()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized CountryRecord mapToRecord(Country entity) {
        try {
            return  CountryRecord.builder()
                    .name(entity.getName())
                    .currency(entity.getCurrency())
                    .language(AppUtil.getMapper().readValue(entity.getLanguage(), new TypeReference<>() {}))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized List<CountryRecord> mapToRecords(List<Country> entities) {
        return entities
                .stream()
                .map(CountryConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Country> mapToEntities(List<CountryRecord> records) {
        return records
                .stream()
                .map(CountryConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }
}
