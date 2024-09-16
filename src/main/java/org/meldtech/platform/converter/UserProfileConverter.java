package org.meldtech.platform.converter;


import io.r2dbc.postgresql.codec.Json;
import org.meldtech.platform.domain.UserProfile;
import org.meldtech.platform.model.api.request.UserProfileRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public class UserProfileConverter {
    private UserProfileConverter() {}

    public static synchronized UserProfile mapToEntity(UserProfileRecord profileRecord) {
        return UserProfile.builder()
                .userId(profileRecord.userid())
                .companyName(profileRecord.companyName())
                .firstName(getOrDefault(profileRecord.firstName(), ""))
                .middleName(getOrDefault(profileRecord.middleName(), ""))
                .lastName(getOrDefault(profileRecord.lastName(), ""))
                .profilePicture(profileRecord.profilePicture())
                .language(profileRecord.language())
                .website(profileRecord.website())
                .email(profileRecord.email())
                .phoneNumber(profileRecord.phoneNumber())
                .settings(Objects.isNull(profileRecord.settings()) ? null : Json.of(profileRecord.settings()))
                .build();
    }

    public static synchronized UserProfile mapToEntity(UserProfile userProfile, UserProfileRecord profileRecord) {
        userProfile.setCompanyName(profileRecord.companyName());
        userProfile.setFirstName(profileRecord.firstName());
        userProfile.setMiddleName(profileRecord.middleName());
        userProfile.setLastName(profileRecord.lastName());
        userProfile.setProfilePicture(profileRecord.profilePicture());
        userProfile.setLanguage(profileRecord.language());
        userProfile.setWebsite(profileRecord.website());
        userProfile.setEmail(profileRecord.email());
        userProfile.setPhoneNumber(profileRecord.phoneNumber());
        return userProfile;
    }

    public static synchronized UserProfileRecord mapToRecord(UserProfile profile) {
        return  UserProfileRecord.builder()
                .createdOn(profile.getCreatedOn())
                .firstName(profile.getFirstName())
                .middleName(profile.getMiddleName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .email(profile.getEmail())
                .companyName(profile.getCompanyName())
                .website(profile.getWebsite())
                .language(profile.getLanguage())
                .profilePicture(profile.getProfilePicture())
                .settings(Objects.isNull(profile.getSettings())? null : profile.getSettings().asString())
                .build();
    }

    public static synchronized List<UserProfileRecord> mapToRecordList(List<UserProfile> profiles) {
        return profiles
                .stream()
                .map(UserProfileConverter::mapToRecord)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }

    private static boolean isTypeNull(Object value){
        return Objects.isNull(value);
    }

}
