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
import org.meldtech.platform.model.api.response.UserMetrics;
import org.meldtech.platform.repository.RoleRepository;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.repository.UserRoleRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.meldtech.platform.converter.UserProfileConverter.updateEntityRole;
import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.meldtech.platform.util.AppUtil.setPage;
import static org.springframework.http.HttpStatus.*;

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
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private static final String INVALID_USER = "Unauthorized User Action";
    private static final String USER_PROFILE_MSG = "User Profile Detail Executed Successfully";
    private static final String DUPLICATE_CREATION = "User with public Id already exist";
    private static final String INVALID_ROLE = "Invalid/No role was provided";


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
                                .settings(userProfile.profile().settings())
                                .userid(user.getId())
                                .build()))
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> appResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<UserProfileRecord> createUserProfile(UserProfileRecord userProfile, int userId) {
        log.info("Creating user profile at sign up {}", userProfile);
        return  profileRepository.save(UserProfileConverter
                        .mapToEntity(UserProfileRecord.builder()
                                .firstName(userProfile.firstName())
                                .middleName(userProfile.middleName())
                                .lastName(userProfile.lastName())
                                .phoneNumber(userProfile.phoneNumber())
                                .language(userProfile.language())
                                .email(userProfile.email())
                                .settings(userProfile.settings())
                                .userid(userId)
                                .build())
                ).map(UserProfileConverter::mapToRecord)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> updateUserProfile(FullUserProfileRecord userProfileRecord, boolean isAdmin) {
        log.info("About to update user profile {}", userProfileRecord);
        return userRepository.findByPublicId(userProfileRecord.publicId())
                .flatMap(user -> profileRepository.findById(getOrDefault(user.getId())))
                .doOnNext(profile -> log.info("Found Profile {}, {}", profile.getFirstName(), profile.getLastName()))
                .flatMap(userProfile -> profileRepository.save(UserProfileConverter
                                .mapToEntity(userProfile, userProfileRecord.profile(), isAdmin) )
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
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), NOT_FOUND.value()));
    }

    public Mono<AppResponse> getUserMetrics() {
        return getTotalUserCount()
                .zipWith(getActiveUsersCount())
                .map(tuple -> new UserMetrics(tuple.getT1(), tuple.getT2(), tuple.getT1() - tuple.getT2()))
                .map(metrics -> appResponse(metrics, USER_PROFILE_MSG))
                .onErrorResume(t -> handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> searchByValue(ReportSettings settings) {
        String value = enhance(settings);
        return userProfileRepository.getSearchResult(value, value, value, value, value)
                .flatMap(this::getUserProfileRecord)
                .collectList()
                .flatMap(profileRecords -> paginatedResponse.getPageIntId(profileRecords, profileRepository, setPage(settings)))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), NOT_FOUND.value()));
    }

    public Mono<AppResponse> changePermission(String publicId, String roleName) {
        return roleRepository.findByName(roleName)
                .flatMap(role -> userRepository.findByPublicId(publicId)
                        .flatMap(user -> userRoleRepository.findByUserId(getOrDefault(user.getId())))
                        .flatMap(userRole -> {
                                    userRole.setRoleId(role.getId());
                                    return userRoleRepository.save(userRole);
                        })
                        .flatMap(userRole -> userProfileRepository.findById(userRole.getUserId()))
                        .map(userProfile -> updateEntityRole(userProfile, role.getName()))
                ).map(role -> appResponse(role, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_ROLE), BAD_REQUEST.value()));
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

    private Mono<Long> getTotalUserCount() {
        return userRepository.count();
    }

    private Mono<Long> getActiveUsersCount() {
        return userRepository.findByEnabled(true)
                .count();
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

    private String enhance(ReportSettings settings) {
        if(Objects.isNull(settings.getSearch())) return "";
        return "%".concat(settings.getSearch().toLowerCase()).concat("%");
    }

    private Integer getOrDefault(Integer id) {
        return Objects.isNull(id) ? -1 : id;
    }
}
