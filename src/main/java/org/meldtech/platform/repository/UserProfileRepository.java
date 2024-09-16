package org.meldtech.platform.repository;

import org.meldtech.platform.domain.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Integer> {
    Mono<UserProfile> findByEmail(String email);
    Flux<UserProfile> findAllBy(Pageable pageable);
}
