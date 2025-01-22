package org.meldtech.platform.repository;

import org.meldtech.platform.domain.Permission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PermissionRepository extends ReactiveCrudRepository<Permission, Integer> {
}
