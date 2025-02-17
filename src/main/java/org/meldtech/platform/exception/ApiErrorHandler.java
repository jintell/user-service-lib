package org.meldtech.platform.exception;

import org.meldtech.platform.service.CountryService;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.meldtech.platform.util.AppUtil.getMapper;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public class ApiErrorHandler {
    private static final LoggerHelper log = LoggerHelper.newInstance(ApiErrorHandler.class.getName());
    private ApiErrorHandler() {}

    public static <T> Mono<T> handleOnErrorResume(Throwable err, int httpStatusCode) {
        HttpStatus status = getFromHttpStatusCode(httpStatusCode);
        String message = err.getMessage();

        if(err instanceof WebClientResponseException) {
            ApiGlobalErrorAttributes.AtomicApiError atomicApiError = getMapper()
                    .convertValue(((WebClientResponseException) err).getResponseBodyAsString(),
                            ApiGlobalErrorAttributes.AtomicApiError.class);
//            ApiGlobalErrorAttributes.AtomicApiError atomicApiError = new GsonBuilder().create()
//                    .fromJson(((WebClientResponseException) err).getResponseBodyAsString(),
//                            ApiGlobalErrorAttributes.AtomicApiError.class);
            status = getStatus(err);
            message = atomicApiError.getMessage();
        }

        return Mono.error(new ApiResponseException(status, message, err));
    }

    public static ApiResponseException handleOnErrorReturn(Throwable err, int httpStatusCode) {
        HttpStatus status = getFromHttpStatusCode(httpStatusCode);
        String message = err.getMessage();
        return new ApiResponseException(status, message, err);
    }

    private static HttpStatus getStatus(Throwable err) {
        if(err.getClass().getName().equals( WebClientResponseException.NotFound.class.getName() ))
            return HttpStatus.NOT_FOUND;
        if(err.getClass().getName().equals( WebClientResponseException.BadRequest.class.getName() ))
            return HttpStatus.BAD_REQUEST;
        else if(err.getClass().getName().equals(WebClientResponseException.Unauthorized.class.getName()))
            return HttpStatus.UNAUTHORIZED;
        else if(err.getClass().getName().equals(WebClientResponseException.Forbidden.class.getName()))
            return HttpStatus.FORBIDDEN;
        else if(err.getClass().getName().equals(WebClientResponseException.MethodNotAllowed.class.getName()))
            return HttpStatus.METHOD_NOT_ALLOWED;
        else if(err.getClass().getName().equals(WebClientResponseException.UnprocessableEntity.class.getName()))
            return HttpStatus.UNPROCESSABLE_ENTITY;
        else return HttpStatus.SERVICE_UNAVAILABLE;
    }
    private static HttpStatus getFromHttpStatusCode(int statusCode) {
        if(HttpStatus.NOT_FOUND.value() == statusCode) return HttpStatus.NOT_FOUND;
        else if(HttpStatus.FORBIDDEN.value() == statusCode) return HttpStatus.FORBIDDEN;
        else if(HttpStatus.UNAUTHORIZED.value() == statusCode) return HttpStatus.UNAUTHORIZED;
        else return HttpStatus.BAD_REQUEST;
    }

}
