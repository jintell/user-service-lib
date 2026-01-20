package org.meldtech.platform.repository;

import org.meldtech.platform.domain.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.meldtech.platform.repository.projection.NativeSql.*;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Integer> {
    Mono<UserProfile> findByEmail(String email);
    Flux<UserProfile> findAllBy(Pageable pageable);
    @Query(USER_BY_APPID)
    Flux<UserProfile> findAllByAppId(Pageable pageable, String appId);
    @Query(USER_BY_APPID_TENANT)
    Flux<UserProfile> findAllByAppIdAndTenantId(Pageable pageable, String appId, String tenantId);
    @Query(USER_SEARCH)
    Flux<UserProfile> getSearchResult(String firstName, String lastName, String middleName, String email, String phoneNumber);

}
