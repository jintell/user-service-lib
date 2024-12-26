package org.meldtech.platform.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.domain.*;
import org.meldtech.platform.event.EmailEvent;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.PasswordRestRecord;
import org.meldtech.platform.model.api.request.UserProfileRecord;
import org.meldtech.platform.model.api.request.UserRecord;
import org.meldtech.platform.model.api.response.NewUserRecord;
import org.meldtech.platform.model.api.response.OtpRecord;
import org.meldtech.platform.model.constant.VerificationType;
import org.meldtech.platform.model.dto.UserSetting;
import org.meldtech.platform.model.event.EmailTemplate;
import org.meldtech.platform.model.event.GenericRequest;
import org.meldtech.platform.repository.*;
import org.meldtech.platform.service.encoding.MessageEncoding;
import org.meldtech.platform.service.template.UserSignupTemplate;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.AppUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;


import static org.meldtech.platform.converter.UserProfileConverter.*;
import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.model.constant.VerificationType.*;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.meldtech.platform.util.AppUtil.getValueOrDefault;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSignUpService extends UserSignupTemplate<UserRecord, User, AppResponse> {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final VerificationRepository verificationRepository;
    private final UserProfileService userProfileService;
    private final PasswordEncoder passwordEncoder;
    private final EmailEvent emailEvent;

    private static final String USER_MSG = "User Action Request Executed Successfully";
    private static final String USER_PASSWORD_MSG = "Password change was successful";
    private static final String STANDARD_ROLE = "STANDARD";

    @Value("${email.activation.template}")
    private String activationMailTemplateId;
    @Value("${email.verification.link}")
    private String emailVerificationLink;
    private static final String INVALID_USER = "Invalid username";
    private static final String INVALID_HASH = "Invalid hash";
    private static final String INVALID_EMAIL = "Username/Email is not on our record";
    private static final String DUPLICATE_ERROR = "We could not resolve the email";
    private static final String INVALID_ROLE = "Invalid/No role was provided";

    @Override
    protected Mono<User> createUserAccount(UserRecord accountToCreate) throws AppException {
        if(Objects.isNull(accountToCreate.role()))
            return handleOnErrorResume(new AppException(INVALID_ROLE), BAD_REQUEST.value());
        return userRepository.findByUsername(accountToCreate.username())
                .flatMap(alreadyExist -> handleOnErrorResume(new AppException("01 - Username Already Exist "),
                                        BAD_REQUEST.value() ).map(s -> alreadyExist)
                ).switchIfEmpty(userRepository.save(
                                        User.builder()
                                                .username(accountToCreate.username() == null ?
                                                        accountToCreate.email()
                                                        : accountToCreate.username())
                                                .password(encodeSecret(accountToCreate.password()))
                                .build()
                        )
                ).doOnNext(account -> log.info("New User Account {} Created", account.getUsername()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @Override
    protected Mono<User> assignDefaultPermissionToUser(User newAccount, UserRecord accountToCreate) throws AppException {
        return findRole(accountToCreate.role())
                .flatMap(role -> userRoleRepository.save(UserRole.builder()
                                .userId(newAccount.getId())
                                .roleId(role.getId())
                                .build()))
                .map(r -> newAccount)
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @Override
    protected Mono<User> createUserProfile(User newAccount, UserRecord accountToCreate) throws AppException {
        return userProfileService.createUserProfile(UserProfileRecord.builder()
                        .email(accountToCreate.email())
                        .phoneNumber(accountToCreate.phone())
                        .firstName(accountToCreate.firstName())
                        .lastName(accountToCreate.lastName())
                        .profilePicture(accountToCreate.profilePicture())
                        .settings(new UserSetting(accountToCreate.role(), false))
                .build(), getOrDefault(newAccount.getId()))
                .map(account -> newAccount);
    }

    @Override
    protected Mono<AppResponse> sendActivationMail(User newAccount, UserRecord accountToCreate) throws AppException {
        String otp = AppUtil.generateOTP(6);
        return verificationRepository.save(Verification.builder()
                        .userId(newAccount.getId())
                        .userOtp(otp)
                        .type(OTHERS.name())
                        .build() )
                .flatMap(verification -> sendMail(verification, accountToCreate))
                .map(r -> appResponse(new NewUserRecord("New User Account Created",
                        newAccount.getPublicId()), USER_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> verifyOtp(String otp) {
        return verificationRepository.findByUserOtp(otp)
                .flatMap(verification -> {
                    verification.setUserOtp(null);
                    if(verification.getType().equals(OTHERS.name()) || verification.getType().equals(RE_ACTIVATE.name()))
                        deleteOtp(verification);
                    return activateUser(verification.getUserId())
                            .map(user -> appResponse(OtpRecord.builder()
                                    .message("Valid OTP")
                                    .publicId(user.getPublicId())
                                    .build(), USER_MSG));
                })
                .switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid OTP Entered"), BAD_REQUEST.value())
                );
    }

    public Mono<AppResponse> verifyPasswordResetOtp(String otp, PasswordRestRecord passwordRestRecord) {
        return verificationRepository.findByUserOtp(otp)
                .flatMap(verification -> {
                    verification.setUserOtp(null);
                    if(verification.getType().equals(PASSWORD_RESET.name())) deleteOtp(verification);
                    return userRepository.findById(verification.getUserId());
                }).flatMap(user -> changePassword(user.getPublicId(), passwordRestRecord))
                .switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid OTP Entered"), BAD_REQUEST.value())
                );
    }

    public Mono<AppResponse> resendOtp(String username, String email, String templateId) {
        String newOTP = AppUtil.generateOTP(6);
        log.info("email {}\nuserName {}", email, username.length());
        return userRepository.findByUsername(username)
                .doOnNext(user -> log.info("user: {}", user))
                .flatMap(user -> verificationRepository.findByUserId(user.getId())
                .flatMap(verification -> {
                            verification.setUserOtp(newOTP);
                            verification.setType(OTHERS.name());
                            verification.setDurationInHours(1L);
                            return verificationRepository.save(verification)
                                    .map(v -> user);
                        }))
                .flatMap(user -> sendMail(user, newOTP, email, email, templateId))
                .switchIfEmpty(
                        handleOnErrorResume(new AppException(INVALID_EMAIL), BAD_REQUEST.value())
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> resendOtp(Integer userId, String email,  String firstName,
                                       String templateId, VerificationType type) {
        String newOTP = AppUtil.generateOTP(6);
        log.info("resendOtp: {} -- {}", email, userId);
        return userRepository.findById(userId)
                .flatMap(user -> verificationRepository.save(Verification.builder()
                                        .userId(user.getId())
                                        .userOtp(newOTP)
                                        .type(type.name())
                                        .durationInHours(1L)
                                        .build() )
                                    .map(v -> user)
                        )
                .flatMap(user -> sendMail(user, newOTP, email, Objects.nonNull(firstName)? firstName : email, templateId))
                .onErrorResume(throwable ->
                        handleOnErrorResume(new AppException(DUPLICATE_ERROR), BAD_REQUEST.value()) );
    }

    public Mono<AppResponse> reActivateUser(String publicId, String templateId, VerificationType type) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> resendOtp(user.getId(), user.getUsername(),null, templateId, type))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()) );
    }

    public Mono<AppResponse> resetPasswordRequest(String email, String templateId) {
        log.info("resetPasswordRequest: {}", email);
        return userProfileRepository.findByEmail(email)
                .flatMap(userProfile -> resendOtp(userProfile.getId(),
                        userProfile.getEmail(),
                        userProfile.getFirstName(),
                        templateId,
                        PASSWORD_RESET))
                .switchIfEmpty(handleOnErrorResume(
                        new AppException("No record found"), BAD_REQUEST.value()))
                .onErrorResume(t ->
                handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }


    public Mono<AppResponse> changePassword(String publicId, PasswordRestRecord restRecord){
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> {
                    if(!restRecord.currentPassword().isBlank()) {
                        if (passwordEncoder.matches(restRecord.currentPassword(), user.getPassword())) {
                            user.setPassword(passwordEncoder.encode(restRecord.newPassword()));
                            return userRepository.save(user);
                        } else return handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value());
                    }else {
                        user.setPassword(passwordEncoder.encode(restRecord.newPassword()));
                        return userRepository.save(user);
                    }
                }).map(profileRecord -> appResponse(
                        profileRecord.getUsername() + "'s "+ USER_PASSWORD_MSG, USER_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
    }


    public Mono<AppResponse> deActivateUser(String publicId){
        return userRepository.findByPublicId(publicId)
                .flatMap( user ->  {
                    user.setEnabled(false);
                    return userRepository.save(user)
                            .map(cred -> appResponse("[]",
                                            user.getUsername() + " has been De-Activated"));
                        }
                ).switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid User"), BAD_REQUEST.value())
                );
    }


    private Mono<User> activateUser(Integer id){
        return userRepository.findById(id)
                .flatMap( user ->  {
                    user.setEnabled(true);
                    return userRepository.save(user);
                }).flatMap(this::updateEmailSettings)
                .switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid User"), BAD_REQUEST.value())
                );
    }

    private void deleteOtp(Verification verification) {
        verificationRepository.delete(verification)
                .subscribe();
    }

    private Mono<User> updateEmailSettings(User user) {
        return userProfileRepository.findById(getOrDefault(user.getId()))
                .map(userProfile -> updateEntitySettings(userProfile,updateSettingEmail(userProfile)))
                .flatMap(userProfileRepository::save)
                .map(usr -> user)
                .switchIfEmpty(Mono.just(user));
    }

    private UserSetting updateSettingEmail(UserProfile profile) {
        UserSetting userSetting = mapToUserSetting(profile);
        return updateUserSetting(userSetting, UserSetting.builder().isEmailVerified(true).build());
    }

    private Mono<Boolean> sendMail(Verification verification, UserRecord accountToCreate) {
        String company = "ESGC";
        return emailEvent.sendMail(GenericRequest.builder()
                .to(accountToCreate.email())
                .templateId(activationMailTemplateId)
                .template(EmailTemplate.builder()
                        .link(emailVerificationLink)
                        .otp(verification.getUserOtp())
                        .firstName(accountToCreate.firstName())
                        .company(company)
                        .username(accountToCreate.username())
                        .password(accountToCreate.password())
                        .build())
                .build());
    }

    private Mono<AppResponse> sendMail(User foundUser, String newOTP, String email, String firstName, String templateId) {
        return emailEvent.sendMail(GenericRequest.builder()
                        .to(email)
                        .templateId(templateId)
                        .template(EmailTemplate.builder()
                                .link(emailVerificationLink)
                                .firstName(firstName)
                                .otp(newOTP)
                                .company("")
                                .username("")
                                .password("")
                                .build())
                        .build())
                .doOnNext(aBoolean -> log.info("sendMail {}", aBoolean))
                .map(r -> appResponse(OtpRecord.builder()
                        .message("OTP sent to, " +
                                foundUser.getUsername())
                        .publicId(foundUser.getPublicId()).build(), USER_MSG)
                );
    }

    private Mono<User> validateHash(String publicId, String userHash, String salt) {
        return verifyCredentials(publicId)
                .flatMap(aggregate -> {
                   String pattern = String.format("%s%s%s", aggregate.getT2().getUserOtp(), publicId, salt);
                   String hashedValue = MessageEncoding.hash512(pattern);
                   if(hashedValue.equalsIgnoreCase(userHash)) {
                       deleteOtp(aggregate.getT2());
                       return Mono.just(aggregate.getT1());
                   }
                   return handleOnErrorResume(new AppException(INVALID_HASH), BAD_REQUEST.value());
                });
    }

    private Mono<Tuple2<User, Verification>> verifyCredentials(String publicId) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> verificationRepository.findByUserIdAndType(user.getId(), PASSWORD_RESET.name())
                        .flatMap(verification -> Mono.zip(Mono.just(user), Mono.just(verification))
                                .onErrorResume(throwable ->
                                        handleOnErrorResume(new AppException("Wrong Otp/User"), BAD_REQUEST.value()))
                )).switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
    }

    private Mono<Role> findRole(String name) {
        return roleRepository.findByName(name)
                .switchIfEmpty(roleRepository.findByName(STANDARD_ROLE));
    }

    private String encodeSecret(String secret) {
        return Objects.isNull(secret) ? null : passwordEncoder.encode(secret);
    }

    private Integer getOrDefault(Integer id) {
        return Objects.isNull(id) ? -1 : id;
    }

}
