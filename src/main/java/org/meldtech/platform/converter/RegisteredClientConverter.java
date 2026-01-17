package org.meldtech.platform.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.meldtech.platform.model.dto.AppRegistrationRecord;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientRecord;
import org.meldtech.platform.model.dto.OAuth2RegisteredClientResponse;
import org.meldtech.platform.model.security.ClientSettingsConverter;
import org.meldtech.platform.model.security.TokenSettingsConverter;
import org.meldtech.platform.util.AppUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.meldtech.platform.model.constant.AppConstant.PATTERN;
import static org.meldtech.platform.util.AppUtil.fromJson;
import static org.meldtech.platform.util.AppUtil.toJson;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisteredClientConverter {
    public static OAuth2RegisteredClient mapToEntity(OAuth2RegisteredClientRecord record, PasswordEncoder encoder) {
        return OAuth2RegisteredClient.builder()
                .clientId(sanitize(record.clientId()).toUpperCase())
                .clientName(sanitize(record.clientName()).toUpperCase())
                .clientSecret(encoder.encode(record.clientSecret()))
                .clientIdIssuedAt(Instant.now())
                .authorizationGrantTypes(concat(record.authorizationGrantTypes()))
                .clientAuthenticationMethods(concat(record.clientAuthenticationMethods()))
                .scopes(concat(record.scopes()))
                .redirectUris(concat(record.redirectUris()))
                .postLogoutRedirectUris(concat(record.postLogoutRedirectUris()))
                .clientSettings(ClientSettingsConverter.convertTo(record.clientSettings()).toString())
                .tokenSettings(TokenSettingsConverter.convertTo(record.tokenSettings()).toString())
                .build();
    }

    public static OAuth2RegisteredClientRecord mapToRecord(OAuth2RegisteredClient entity) {
        return OAuth2RegisteredClientRecord.builder()
                .clientId(entity.getClientId())
                .clientName(entity.getClientName())
                .authorizationGrantTypes(toSet(entity.getAuthorizationGrantTypes()))
                .clientAuthenticationMethods(toSet(entity.getClientAuthenticationMethods()))
                .clientIdIssuedAt(entity.getClientIdIssuedAt().toString())
                .scopes(toSet(entity.getScopes()))
                .redirectUris(toSet(entity.getRedirectUris()))
                .postLogoutRedirectUris(toSet(entity.getPostLogoutRedirectUris()))
                .tokenSettings(TokenSettingsConverter.convertTo(convetToMap(entity.getTokenSettings())))
                .clientSettings(ClientSettingsConverter.convertTo(convetToMap(entity.getClientSettings())))
                .build();
    }

    public static OAuth2RegisteredClient mapToEntity(OAuth2RegisteredClientRecord record,
                                              OAuth2RegisteredClient entity,
                                              PasswordEncoder encoder) {
        if(Objects.nonNull(record.clientName())) entity.setClientName(record.clientName().toUpperCase());
        if(Objects.nonNull(record.clientSecret())) entity.setClientSecret(encoder.encode(record.clientSecret()));
        if(Objects.nonNull(record.clientAuthenticationMethods()))
            entity.setClientAuthenticationMethods(concat(record.clientAuthenticationMethods()));
        if(Objects.nonNull(record.authorizationGrantTypes()))
            entity.setAuthorizationGrantTypes(concat(record.authorizationGrantTypes()));
        if(Objects.nonNull(record.redirectUris()))
            entity.setRedirectUris(concat(record.redirectUris()));
        if(Objects.nonNull(record.postLogoutRedirectUris()))
            entity.setPostLogoutRedirectUris(concat(record.postLogoutRedirectUris()));
        if(Objects.nonNull(record.scopes()))
            entity.setScopes(concat(record.scopes()));
        if(Objects.nonNull(record.clientSettings()))
            entity.setClientSettings(ClientSettingsConverter.convertTo(record.clientSettings()).toString());
        if(Objects.nonNull(record.tokenSettings()))
            entity.setTokenSettings(TokenSettingsConverter.convertTo(record.tokenSettings()).toString());
        return entity;

    }

    public static OAuth2RegisteredClientResponse toRecord(OAuth2RegisteredClientRecord record, AppRegistrationRecord appRecord) {
        return OAuth2RegisteredClientResponse.builder()
                .client(OAuth2RegisteredClientRecord.builder()
                        .clientId(record.clientId())
                        .clientName(record.clientName())
                        .clientSecret(masked(record.clientSecret()))
                        .authorizationGrantTypes(record.authorizationGrantTypes())
                        .clientAuthenticationMethods(record.clientAuthenticationMethods())
                        .clientIdIssuedAt(record.clientIdIssuedAt())
                        .scopes(record.scopes())
                        .redirectUris(record.redirectUris())
                        .postLogoutRedirectUris(record.postLogoutRedirectUris())
                        .tokenSettings(record.tokenSettings())
                        .clientSettings(record.clientSettings())
                        .build())
                .appRegistration(AppRegistrationRecord.builder()
                        .applicationId(appRecord.applicationId())
                        .clientId(appRecord.clientId())
                        .clientName(appRecord.clientName())
                        .clientSecret(masked(appRecord.clientSecret()))
                        .redirectUrl(appRecord.redirectUrl())
                        .appLoginUrl(appRecord.appLoginUrl())
                        .appLogoutUrl(appRecord.appLogoutUrl())
                        .appResolvedPathUrl(appRecord.appResolvedPathUrl())
                        .scope(appRecord.scope())
                        .enabled(appRecord.enabled())
                        .build())
                .build();
    }

    private static String concat(Set<String> values) {
        return String.join(",", values);
    }

    private static Set<String> toSet(String values) {
        if(!values.contains(",")) return new HashSet<>(Collections.singleton(values));
        return Arrays
                .stream(values.split(","))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> convetToMap(Object entity) {
        String strip = ((String) entity).replaceAll(PATTERN,"");
        try {
            return AppUtil.getMapper().readValue(strip, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }



    public static String sanitize(String values) {
        String regex = "[*|!|&|^|%|$|#|`|~]+";
        return values.replaceAll(regex, "");
    }

    private static String masked(String value) {
        if(Objects.isNull(value)) return "**";
        else if(value.length() < 4) return "****";
        return value.substring(0, 3) + "*****";
    }

}
