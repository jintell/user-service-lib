package org.meldtech.platform.converter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ObjectToJsonConverter implements Converter<Object, Json> {
    private final ObjectMapper objectMapper;

    @Override
    public Json convert(Object source) {
        try {
            return Json.of(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectToJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
