package org.meldtech.platform.repository;

import org.meldtech.platform.domain.RolePermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermission, Integer> {
    Mono<RolePermission> findByRoleId(Integer roleId);
}
