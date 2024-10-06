package org.meldtech.platform.model.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.meldtech.platform.util.ReportSettings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.server.ServerRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiRequest {
    public static PasswordUpdateRecord getHashAndSalt(ServerRequest request) {
        return PasswordUpdateRecord.builder()
                .hash(request.headers().firstHeader("hash"))
                .salt(request.headers().firstHeader("salt"))
                .build();
    }

    public static ReportSettings reportSettings(ServerRequest request) {
        return ReportSettings.instance()
                .page(Integer.parseInt(request.queryParam("page").orElse("0")))
                .size(Integer.parseInt(request.queryParam("size").orElse("10")))
                .sortIn(request.queryParam("sortIn").orElse("asc"))
                .sortBy(request.queryParam("sortBy").orElse("createdOn"))
                .search(request.queryParam("search").orElse(""));
    }

    public static Pageable setPage(ReportSettings settings) {
        return PageRequest.of(settings.getPage(), settings.getSize(),
                Sort.Direction.fromString(settings.getSortIn()), settings.getSortBy());
    }
}
