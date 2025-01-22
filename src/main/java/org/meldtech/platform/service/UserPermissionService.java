package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.converter.RolePermissionConverter;
import org.meldtech.platform.domain.User;
import org.meldtech.platform.domain.UserRole;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.dto.RolePermissionRecord;
import org.meldtech.platform.repository.RolePermissionRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionService {
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private static final String USER_PROFILE_MSG = "User Permissions Retrieved Successfully";
    private static final String INVALID_USER = "Invalid/No User id was provided";

    public Mono<AppResponse> getUserPermissions(String publicId) {
        return getUserByPublicId(publicId)
                .flatMap(this::getUserPermission)
                .map(record -> appResponse(record, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    private Mono<User> getUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId);
    }

    private Mono<RolePermissionRecord> getUserPermission(User user) {
        return roleRepository.findByUserId(user.getId())
                .flatMap(this::getRolePermission);
    }

    private Mono<RolePermissionRecord> getRolePermission(UserRole userRole) {
        return rolePermissionRepository.findByRoleId(userRole.getRoleId())
                .map(RolePermissionConverter::mapToRecord);
    }
}
