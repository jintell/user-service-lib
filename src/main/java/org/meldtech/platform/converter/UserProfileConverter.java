package org.meldtech.platform.converter;


import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.domain.UserProfile;
import org.meldtech.platform.model.api.request.UserProfileRecord;
import org.meldtech.platform.model.dto.UserSetting;

import java.util.List;
import java.util.Objects;

import static org.meldtech.platform.util.AppUtil.convertToType;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Slf4j
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
                .settings(Objects.isNull(profileRecord.settings()) ? null :
                        Json.of(convertToType(profileRecord.settings(), String.class)))
                .build();
    }

    public static synchronized UserProfile mapToEntity(UserProfile userProfile, UserProfileRecord profileRecord, boolean isAdmin) {
        if(Objects.nonNull(profileRecord.companyName()) && isAdmin) userProfile.setCompanyName(profileRecord.companyName());
        if(Objects.nonNull(profileRecord.firstName()) && isAdmin) userProfile.setFirstName(profileRecord.firstName());
        if(Objects.nonNull(profileRecord.middleName()) && isAdmin) userProfile.setMiddleName(profileRecord.middleName());
        if(Objects.nonNull(profileRecord.lastName()) && isAdmin) userProfile.setLastName(profileRecord.lastName());
        if(Objects.nonNull(profileRecord.profilePicture()) && isAdmin) userProfile.setProfilePicture(profileRecord.profilePicture());
        if(Objects.nonNull(profileRecord.language()) && isAdmin) userProfile.setLanguage(profileRecord.language());
        if(Objects.nonNull(profileRecord.website()) && isAdmin) userProfile.setWebsite(profileRecord.website());
        if(Objects.nonNull(profileRecord.email()) && isAdmin) userProfile.setEmail(profileRecord.email());
        if(Objects.nonNull(profileRecord.phoneNumber()) && isAdmin) userProfile.setPhoneNumber(profileRecord.phoneNumber());
        if(isAdmin) userProfile.setSettings(Objects.isNull(profileRecord.settings()) ? null :
                Json.of(convertToType(profileRecord.settings(), String.class)));
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
                .settings(Objects.isNull(profile.getSettings())? null :
                        convertToType(profile.getSettings().asString(), UserSetting.class))
                .build();
    }

    public static synchronized UserProfile updateEntityRole(UserProfile profile, String newRole) {
        UserSetting userSetting = convertToType(profile.getSettings().asString(), UserSetting.class);
        profile.setSettings(Json.of(convertToType(new UserSetting(newRole, userSetting.isEmailVerified()), String.class)));
        return profile;
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
