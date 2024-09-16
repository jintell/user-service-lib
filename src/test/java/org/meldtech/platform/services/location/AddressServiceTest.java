package org.meldtech.platform.services.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meldtech.platform.exception.ApiResponseException;
import org.meldtech.platform.repository.AddressRepository;
import org.meldtech.platform.repository.CountryRepository;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.service.AddressService;
import org.meldtech.platform.service.CountryService;
import org.meldtech.platform.stub.AddressStub;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.meldtech.platform.stub.AppResponseStub.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    AddressRepository addressRepository;
    @Mock
    UserProfileRepository userProfileRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CountryRepository countryRepository;
    @InjectMocks
    AddressService addressService;

    @DisplayName("Get Address by Public Id")
    @Test
    void givenPublicId_whenGetAddress_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.just(user()));
        when(addressRepository.findById(anyInt()))
                .thenReturn(Mono.just(address(1)));

        StepVerifier.create(addressService.getAddress(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Get Address by Public Id doest not exist")
    @Test
    void givenWrongPublicId_whenGetAddress_thenReturnException() {
        when(userRepository.findByPublicId(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(addressService.getAddress(UUID.randomUUID().toString()))
                .expectError(ApiResponseException.class)
                .verify();

    }

    @DisplayName("Add New Address")
    @Test
    void givenAddressRecord_whenCreateAddress_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(countryRepository.findByName(anyString())).thenReturn(Mono.just(country(1)));
        when(userProfileRepository.findById(anyInt())).thenReturn(Mono.just(userProfile(1)));
        when(userProfileRepository.save(any())).thenReturn(Mono.just(userProfile(1)));
        when(addressRepository.save(any())).thenReturn(Mono.just(address(1)));

        StepVerifier.create(addressService.addAddress(AddressStub.requestRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();

    }

    @DisplayName("Add New Address - Invalid Country")
    @Test
    void givenAddressRecordWithWrongCountry_whenCreateAddress_thenReturnException() {
        when(countryRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(addressService.addAddress(AddressStub.requestRecord()))
                .expectError(ApiResponseException.class)
                .verify();
    }

    @DisplayName("Add New Address - Invalid User")
    @Test
    void givenAddressRecordWithWrongUser_whenCreateAddress_thenReturnException() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.empty());
        when(countryRepository.findByName(anyString())).thenReturn(Mono.just(country(1)));

        StepVerifier.create(addressService.addAddress(AddressStub.requestRecord()))
                .expectError(ApiResponseException.class)
                .verify();
    }

    @DisplayName("Update Address")
    @Test
    void givenAddressRecord_whenUpdateAddress_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(countryRepository.findByName(anyString())).thenReturn(Mono.just(country(1)));
        when(addressRepository.findById(anyInt())).thenReturn(Mono.just(address(1)));
        when(addressRepository.save(any())).thenReturn(Mono.just(address(1)));

        StepVerifier.create(addressService.updateAddress(AddressStub.requestRecord()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @DisplayName("Delete Address")
    @Test
    void givenPublicId_whenDeleteAddress_thenReturnAppResponse() {
        when(userRepository.findByPublicId(anyString())).thenReturn(Mono.just(user()));
        when(addressRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(addressService.deleteAddress(UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

}