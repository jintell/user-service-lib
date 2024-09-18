package org.meldtech.platform.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.converter.UserProfileConverter;
import org.meldtech.platform.domain.User;
import org.meldtech.platform.domain.UserProfile;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.UserProfileRecord;
import org.meldtech.platform.model.api.response.FullUserProfileRecord;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.meldtech.platform.util.AppUtil.setPage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PaginatedResponse paginatedResponse;

    private static final String INVALID_USER = "Unauthorized User Action";
    private static final String USER_PROFILE_MSG = "User Profile Detail Executed Successfully";
    private static final String DUPLICATE_CREATION = "User with public Id already exist";

    public Mono<AppResponse> createUserProfile(FullUserProfileRecord userProfile) {
        log.info("About to create user profile {}", userProfile);
        return checkUserExistence(userProfile.publicId())
                .flatMap(user -> profileRepository.save(UserProfileConverter
                        .mapToEntity(UserProfileRecord.builder()
                                .firstName(userProfile.profile().firstName())
                                .middleName(userProfile.profile().middleName())
                                .lastName(userProfile.profile().lastName())
                                .phoneNumber(userProfile.profile().phoneNumber())
                                .language(userProfile.profile().language())
                                .email(userProfile.profile().email())
                                .settings("{}" )
                                .userid(user.getId())
                                .build()))
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> appResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> updateUserProfile(FullUserProfileRecord userProfileRecord) {
        log.info("About to update user profile {}", userProfileRecord);
        return userRepository.findByPublicId(userProfileRecord.publicId())
                .flatMap(user -> profileRepository.findById(getOrDefault(user.getId())))
                .flatMap(userProfile -> profileRepository.save(UserProfileConverter
                                .mapToEntity(userProfile, userProfileRecord.profile()) )
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> appResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }


    public Mono<AppResponse> getUserProfile(String publicId) {
        return userRepository.findByPublicId(publicId)
                .flatMap(this::getUserProfileFromDB)
                .map(profileRecord -> appResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }


    public Mono<AppResponse> getUserProfileByAdmin(String userPublicId) {
        return userRepository.findByPublicId(userPublicId)
                .flatMap(this::getUserProfileFromDB)
                .map(profileRecord -> appResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }


    public Mono<AppResponse> getUserProfiles(ReportSettings settings) {
        return profileRepository.findAllBy(setPage(settings))
                .flatMap(this::getUserProfileRecord)
                .collectList()
                .flatMap(profileRecords -> paginatedResponse.getPageIntId(profileRecords, profileRepository, setPage(settings)))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    private Mono<FullUserProfileRecord> getUserProfileRecord(UserProfile profile) {
        return userRepository.findById(getOrDefault(profile.getId()))
                .map(user -> FullUserProfileRecord.builder()
                        .username(user.getUsername())
                        .publicId(user.getPublicId())
                        .profile(UserProfileConverter.mapToRecord(profile))
                        .build()
                );
    }

    private Mono<FullUserProfileRecord> getUserProfileFromDB(User user) {
        return getUserProfileFromDB(user.getId())
                .map(userProfile -> FullUserProfileRecord.builder()
                        .username(user.getUsername())
                        .publicId(user.getPublicId())
                        .profile(UserProfileConverter.mapToRecord(userProfile))
                        .build()
                );
    }

    private Mono<UserProfile> getUserProfileFromDB(Integer id) {
        return profileRepository.findById(getOrDefault(id));
    }

    private Mono<User> checkUserExistence(String publicId) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> checkForProfile(user)
                        .flatMap(isFound -> isFound ?
                                handleOnErrorResume(new AppException(DUPLICATE_CREATION), BAD_REQUEST.value())
                                : Mono.just(user))
                );
    }


    private Mono<Boolean> checkForProfile(User user) {
        return profileRepository.findById(getOrDefault(user.getId()))
                .map(userProfile -> true)
                .switchIfEmpty( Mono.just(false) );
    }


    private Integer getOrDefault(Integer id) {
        return Objects.isNull(id) ? -1 : id;
    }
}
