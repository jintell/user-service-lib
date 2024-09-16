package org.meldtech.platform.services.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.domain.Country;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.CountryRepository;
import org.meldtech.platform.service.CountryService;
import org.meldtech.platform.stub.CountryStub;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    CountryRepository countryRepository;
    @InjectMocks
    CountryService countryService;

    @DisplayName("Get All Countries")
    @Test
    void whenGetAllCountries_thenReturnAppResponse() {
        when(countryRepository.findAll())
                .thenReturn(Flux.just(countries().toArray(new Country[0])));

        StepVerifier.create(countryService.getCountries())
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get Country ny Name")
    @Test
    void givenName_whenGetCountry_thenReturnAppResponse() {
        when(countryRepository.findByName(anyString()))
                .thenReturn(Mono.just(country(1)));

        StepVerifier.create(countryService.getCountry(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Create New Country")
    @Test
    void givenCountryRecord_whenCreateCountry_thenReturnAppResponse() {
        when(countryRepository.save(any()))
                .thenReturn(Mono.just(country(1)));

        StepVerifier.create(countryService.createCountry(CountryStub.createCountryRecord("USA")))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Create New Country Error Creating")
    @Test
    void givenCountryRecord_whenCreateCountry_thenReturnExcetion() {
        when(countryRepository.save(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(countryService.createCountry(CountryStub.createCountryRecord("USA")))
                .expectError(ApiResponseException.class)
                .verify();

    }

    @DisplayName("Update Country")
    @Test
    void givenCountryRecord_whenUpdateCountry_thenReturnAppResponse() {
        when(countryRepository.save(any()))
                .thenReturn(Mono.just(country(1)));
        when(countryRepository.findByName(anyString()))
                .thenReturn(Mono.just(country(1)));

        StepVerifier.create(countryService.updateCountries(CountryStub.createCountryRecord("USA")))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Delete Country")
    @Test
    void givenCountryRecord_whenDeleteCountry_thenReturnAppResponse() {
        when(countryRepository.delete(any()))
                .thenReturn(Mono.empty());
        when(countryRepository.findByName(anyString()))
                .thenReturn(Mono.just(country(1)));

        StepVerifier.create(countryService.deleteCountry(CountryStub.createCountryRecord("USA")))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Create New Country")
    @SuppressWarnings("unchecked")
    @Test
    void givenCountryRecords_whenCreateCountries_thenReturnAppResponse() {
        when(countryRepository.saveAll(any(Iterable.class)))
                .thenReturn(Flux.just(countries().toArray(new Country[0])));

        StepVerifier.create(countryService.createCountries(CountryStub.countryRecordList()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

}