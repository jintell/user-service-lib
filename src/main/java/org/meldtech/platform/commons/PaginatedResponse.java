package org.meldtech.platform.commons;

import lombok.Builder;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.enums.NavigationType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.meldtech.platform.model.enums.NavigationType.NEXT;
import static org.meldtech.platform.model.enums.NavigationType.PREVIOUS;

@Component
public class PaginatedResponse {
    public  <T, R> Mono<AppResponse> getData(List<R> resultSet,
                                            ReactiveCrudRepository<T, String> repository,
                                            Pageable pageable) {
        return repository.count()
                .map(result -> new PageImpl<>(resultSet, pageable, result))
                .map(this::buildResponse);
    }

    private  <R> AppResponse buildResponse(PageImpl<R> tPage) {
        int currentPage = tPage.getPageable().getPageNumber() + 1;
            return AppResponse.builder()
                    .status(true)
                    .message("Operation was Successful")
                    .data( CustomPagination.builder()
                            .page(currentPage)
                            .pageSize(tPage.getSize())
                            .totalPages(tPage.getTotalPages())
                            .totalCount(tPage.getTotalElements())
                            .results(tPage.getContent())
                            .previousPage(navigation(currentPage, tPage.getTotalPages(), PREVIOUS))
                            .nextPage(navigation(currentPage, tPage.getTotalPages(), NEXT))
                            .build()
                    ).build();
    }

    @Builder
    record CustomPagination(
        int page,
        int pageSize,
        int totalPages,
        long totalCount,
        int nextPage,
        int previousPage,
        List<?> results
    ){}

    private static int navigation(int currentPage, int totalPages, NavigationType type) {
        if(type == PREVIOUS) {
            if(currentPage >= totalPages) { return  0; }
            if(currentPage > 1) { return currentPage - 1; }
            else { return 0; }
        }else if(type == NEXT) {
            if(currentPage >= totalPages) { return  0; }
            else { return currentPage + 1; }
        }
        return 0;
    }
}
