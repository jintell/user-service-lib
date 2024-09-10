package org.meldtech.platform.repository;

import org.meldtech.platform.domain.OAuth2RegisteredClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface Oauth2RegisteredClientRepository extends ReactiveCrudRepository<OAuth2RegisteredClient, String> {
    Mono<OAuth2RegisteredClient> findByClientId(String clientId);
    Flux<OAuth2RegisteredClient> findAllBy(Pageable pageable);
}
