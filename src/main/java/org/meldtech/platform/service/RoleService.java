package org.meldtech.platform.service;


import lombok.RequiredArgsConstructor;
import org.meldtech.platform.commons.PaginatedResponse;
import org.meldtech.platform.converter.RoleConverter;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.repository.RoleRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.LoggerHelper;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.setPage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final LoggerHelper log = LoggerHelper.newInstance(CountryService.class.getName());

    private final RoleRepository roleRepository;
    private final PaginatedResponse paginatedResponse;

    public Mono<AppResponse> getRoles(ReportSettings settings) {
        return roleRepository.findAllBy(setPage(settings))
                .map(RoleConverter::mapToRecord)
                .collectList()
                .flatMap(roles -> paginatedResponse.getPageIntId(roles, roleRepository, setPage(settings)))
                .doOnNext(registeredClientRecords -> log.info("Roles Records {}", registeredClientRecords));
//                .onErrorResume(t ->
//                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }
}
