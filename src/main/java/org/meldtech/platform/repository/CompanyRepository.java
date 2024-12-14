package org.meldtech.platform.repository;

import org.jetbrains.annotations.NotNull;
import org.meldtech.platform.domain.Company;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CompanyRepository extends ReactiveCrudRepository<Company, Integer> {
    Mono<Company> findByIdNumber(String idNumber);
    @NotNull Mono<Long> count();
}
