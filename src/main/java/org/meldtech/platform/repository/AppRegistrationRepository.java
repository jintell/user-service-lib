package org.meldtech.platform.repository;

import org.meldtech.platform.domain.AppRegistration;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppRegistrationRepository extends ReactiveCrudRepository<AppRegistration, Integer> {
    Mono<AppRegistration> findByApplicationId(String applicationId);
    Mono<AppRegistration> findByClientId(String clientId);
}
