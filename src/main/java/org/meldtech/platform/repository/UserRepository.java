package org.meldtech.platform.repository;

import org.meldtech.platform.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
    Mono<User> findById(int id);
    Mono<User> findByUsername(String username);
    Mono<User> findByUsernameAndEnabled(String username, boolean isEnabled);
    Mono<User> findByPublicId(String publicId);
    Flux<User> findByEnabled(boolean enabled);
}
