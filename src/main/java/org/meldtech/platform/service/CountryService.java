package org.meldtech.platform.service;

import lombok.RequiredArgsConstructor;
import org.meldtech.platform.converter.CountryConverter;
import org.meldtech.platform.domain.Country;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.response.CountryRecord;
import org.meldtech.platform.repository.CountryRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Service
@RequiredArgsConstructor
public class CountryService {
    private final LoggerHelper log = LoggerHelper.newInstance(CountryService.class.getName());
    private final CountryRepository countryRepository;
    private static final String COUNTRY_MSG = "Country request executed successfully";
    private static final String INVALID_COUNTRY = "The country name is invalid";
    private static final String ERROR_MSG = "The country record mutation could not be performed";

    public Mono<AppResponse> getCountries() {
        log.info("Getting ALl Country Records...");
        return countryRepository.findAll()
                .collectList()
                .map(CountryConverter::mapToRecords)
                .map(countryRecords -> appResponse(countryRecords, COUNTRY_MSG));
    }
    public Mono<AppResponse> getCountry(String name) {
        log.info("Getting Country Record by ", name);
        return validateCountry(name)
                .map(CountryConverter::mapToRecord)
                .map(countryRecord -> appResponse(countryRecord, COUNTRY_MSG));
    }

    public Mono<AppResponse> createCountry(CountryRecord countryRecord) {
        log.info("Saving New  Country Records");
        return countryRepository.save(CountryConverter.mapToEntity(countryRecord))
                .map(CountryConverter::mapToRecord)
                .map(savedRecord -> appResponse(savedRecord, COUNTRY_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }


    public Mono<AppResponse> updateCountries(CountryRecord countryRecord) {
        log.info("Update Country Record for ", countryRecord.name());
        return validateCountry(countryRecord.name())
                .flatMap(country -> {
                    Country converted = CountryConverter.mapToEntity(countryRecord);
                    country.setCurrency(countryRecord.currency());
                    country.setLanguage(converted.getLanguage());
                    return countryRepository.save(country);
                })
                .map(CountryConverter::mapToRecord)
                .map(updateRecord -> appResponse(updateRecord, COUNTRY_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }


    public Mono<AppResponse> deleteCountry(CountryRecord countryRecord) {
        log.info("Delete Country Record for ", countryRecord.name());
        return validateCountry(countryRecord.name())
                .flatMap(countryRepository::delete)
                .then(Mono.fromCallable(() -> appResponse(countryRecord.name() + " Deleted", COUNTRY_MSG)));
    }


    public Mono<AppResponse> createCountries(List<CountryRecord> countryRecords) {
        log.info("Creating Country Records for ", countryRecords.size());
        return countryRepository.saveAll(CountryConverter.mapToEntities(countryRecords))
                .collectList()
                .map(CountryConverter::mapToRecords)
                .map(createdRecord -> appResponse(
                        createdRecord.size() + " Countries created",
                        COUNTRY_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    private Mono<Country> validateCountry(String name) {
        return countryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_COUNTRY), BAD_REQUEST.value()));
    }
}
