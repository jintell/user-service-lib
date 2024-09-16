package org.meldtech.platform.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.domain.User;
import org.meldtech.platform.domain.UserRole;
import org.meldtech.platform.domain.Verification;
import org.meldtech.platform.event.EmailEvent;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.PasswordRestRecord;
import org.meldtech.platform.model.api.request.UserRecord;
import org.meldtech.platform.model.api.response.NewUserRecord;
import org.meldtech.platform.model.api.response.OtpRecord;
import org.meldtech.platform.model.constant.VerificationType;
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


import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.model.constant.VerificationType.OTHERS;
import static org.meldtech.platform.model.constant.VerificationType.PASSWORD_RESET;
import static org.meldtech.platform.util.AppUtil.appResponse;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailEvent emailEvent;

    private static final String USER_MSG = "User Action Request Executed Successfully";
    private static final String USER_PASSWORD_MSG = "Password change was successful";

    @Value("${email.activation.template}")
    private String activationMailTemplateId;
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
                ).doOnNext(account -> log.info("New User Account {} Created", account.getUsername()));
    }

    @Override
    protected Mono<User> assignDefaultPermissionToUser(User newAccount, UserRecord accountToCreate) throws AppException {
        return roleRepository.findByName(accountToCreate.role())
                .flatMap(role -> userRoleRepository.save(UserRole.builder()
                                .userId(newAccount.getId())
                                .roleId(role.getId())
                                .build()))
                .map(r -> newAccount);
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
                        newAccount.getPublicId()), USER_MSG));
    }

    public Mono<AppResponse> verifyOtp(String otp) {
        return verificationRepository.findByUserOtp(otp)
                .flatMap(verification -> {
                    verification.setUserOtp(null);
                    if(verification.getType().equals(OTHERS.name())) deleteOtp(verification);
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

    public Mono<AppResponse> resendOtp(String username, String email, String templateId) {
        String newOTP = AppUtil.generateOTP(6);
        return userRepository.findByUsername(username)
                .flatMap(user -> verificationRepository.findByUserId(user.getId())
                .flatMap(verification -> {
                            verification.setUserOtp(newOTP);
                            verification.setType(OTHERS.name());
                            verification.setDurationInHours(1L);
                            return verificationRepository.save(verification)
                                    .map(v -> user);
                        }))
                .flatMap(user -> sendMail(user, newOTP, email, templateId))
                .switchIfEmpty(
                        handleOnErrorResume(new AppException(INVALID_EMAIL), BAD_REQUEST.value())
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> resendOtp(Integer userId, String email, String templateId, VerificationType type) {
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
                .flatMap(user -> sendMail(user, newOTP, email, templateId))
                .onErrorResume(throwable ->
                        handleOnErrorResume(new AppException(DUPLICATE_ERROR), BAD_REQUEST.value()) );
    }

    public Mono<AppResponse> resetPasswordRequest(String email, String templateId) {
        log.info("resetPasswordRequest: {}", email);
        return userProfileRepository.findByEmail(email)
                .flatMap(userProfile -> resendOtp(userProfile.getId(),
                        userProfile.getEmail(),
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
                    if(passwordEncoder.matches(restRecord.currentPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(restRecord.newPassword()));
                        return userRepository.save(user);
                    }else return handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value());
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
                }).switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid User"), BAD_REQUEST.value())
                );
    }

    private void deleteOtp(Verification verification) {
        verificationRepository.delete(verification)
                .subscribe();
    }

    private Mono<Boolean> sendMail(Verification verification, UserRecord accountToCreate) {
        return emailEvent.sendMail(GenericRequest.builder()
                .to(accountToCreate.email())
                .templateId(activationMailTemplateId)
                .template(EmailTemplate.builder().link(
                                accountToCreate.email())
                        .otp(verification.getUserOtp())
                        .build())
                .build());
    }

    private Mono<AppResponse> sendMail(User foundUser, String newOTP, String email, String templateId) {
        return emailEvent.sendMail(GenericRequest.builder()
                        .to(email)
                        .templateId(templateId)
                        .template(EmailTemplate.builder().link(email).otp(newOTP).build())
                        .build())
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

    private String encodeSecret(String secret) {
        return Objects.isNull(secret) ? null : passwordEncoder.encode(secret);
    }

}