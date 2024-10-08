package org.meldtech.platform.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.meldtech.platform.model.api.AppResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public class AppUtil {
    private static final Random RANDOM2 = new Random(System.nanoTime());
    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private AppUtil(){}
    public static boolean isNewRecord(Object id) {
        return Objects.isNull(id);
    }
    public static int userRole() {
        return 1;
    }

    public static String generateOTP(int digits) {
        StringBuilder otp = new StringBuilder();
        Random rand = new Random();
        while(digits > 0) {
            otp.append(rand.nextInt(10));
            digits--;
        }
        return otp.toString();
    }

    public static String generateReferenceNumber(String prefix) {

        if(prefix == null || prefix.length() < 3) return null;

        String extractedPrefix  = prefix.substring(0, 3);
        long currentTimeStamp = System.nanoTime();

        return (extractedPrefix.toUpperCase() + "-" + currentTimeStamp);
    }

    public static String generateIdAndSecrets(int num, String prefix){
        String prefix3Letters = (prefix == null || prefix.isEmpty()) || prefix.length() < 3?
                "SCR" : prefix.substring(0,3);

        String generatedCharacters = generateRandomCharacters(num, ALPHABETS);

        return prefix3Letters +
                "_" +
                generatedCharacters;
    }

    public static AppResponse appResponse(Object data, String message) {
        return AppResponse.builder()
                .status(true)
                .data(data)
                .message(message)
                .build();
    }

    public static String getValueOrDefault(String account) {
        return Objects.isNull(account) ? "" : account;
    }
    public static String generateToken(int count) {
        StringBuilder token = new StringBuilder(UUID.randomUUID().toString());
        for(int i = 1; i <= count; i++) {
            token
                    .append("-")
                    .append(UUID.randomUUID());
        }
        return token.toString();
    }

    public static String generateRandomCharacters(int num, String characterSampleSpace){
        StringBuilder generatedString = new StringBuilder();
        for (int i = 0; i < num; i++) {
            char letter = (characterSampleSpace).charAt(RANDOM2.nextInt(characterSampleSpace.length()));
            generatedString.append(letter);
        }
        return generatedString.toString();
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    }

    public static <T> T convertToType(Object value, Class<T> type) {
        try {
            return type.getCanonicalName().equals("java.lang.String") ?
                    type.cast(getMapper().writer().writeValueAsString(value)) :
                    getMapper().readValue(value.toString(), type);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static final Map<String, String> USER_DEVICE = new ConcurrentHashMap<>();

    public static Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage() - 1, settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }
}
