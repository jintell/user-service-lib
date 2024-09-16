package org.meldtech.platform.repository;

import org.meldtech.platform.domain.UserRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Integer> {
}
