package org.meldtech.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import org.meldtech.platform.converter.AddressConverter;
import org.meldtech.platform.domain.Address;
import org.meldtech.platform.domain.Country;
import org.meldtech.platform.domain.User;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.request.AddressRequestRecord;
import org.meldtech.platform.repository.AddressRepository;
import org.meldtech.platform.repository.CountryRepository;
import org.meldtech.platform.repository.UserProfileRepository;
import org.meldtech.platform.repository.UserRepository;
import org.meldtech.platform.util.AppError;
import org.meldtech.platform.util.AppUtil;
import org.meldtech.platform.util.LoggerHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.meldtech.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.meldtech.platform.util.AppUtil.appResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    private final LoggerHelper log = LoggerHelper.newInstance(CountryService.class.getName());
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CountryRepository countryRepository;

//    @Value("${aws.s3.resource.profile}")
    private String profileFolder;


    private static final String ADDRESS_MSG = "Address request executed successfully";
    private static final String INVALID_COUNTRY = "The country selected is invalid";
    private static final String INVALID_USER = "The user id is invalid";
    private static final String INVALID_USER_PROFILE = "No basic user profile found. Invalid user id";
    private static final String ERROR_MSG = "The address record mutation could not be performed";


    public Mono<AppResponse> getAddress(String publicId) {
        log.info("Fetching the user address");
        return validateUser(publicId)
                        .flatMap(user -> addressRepository.findById(getOrDefault(user.getId())))
                        .map(AddressConverter::mapToRecord)
                        .map(addressRecord -> appResponse(addressRecord, ADDRESS_MSG))
                        .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER_PROFILE), NOT_FOUND.value())
                );
    }

    public Mono<AppResponse> addAddress(AddressRequestRecord requestRecord) {
        log.info("Address is being added and linked to user");
        return validateCountry(requestRecord.addressRecord().country())
                .flatMap(country -> validateUser(requestRecord.publicId())
                        .flatMap(user -> userProfileRepository.findById(getOrDefault(user.getId())))
                        .flatMap(userProfile -> {
                            userProfile.setProfilePicture(requestRecord.pictureName() );
                            return userProfileRepository.save(userProfile);
                        }).flatMap(userProfile -> {
                            Address address = AddressConverter.mapToEntity(requestRecord.addressRecord());
                            address.setUserId(userProfile.getId());
                            log.info("user details ", address);
                            return addressRepository.save(address);
                        })
                        .map(AddressConverter::mapToRecord)
                        .map(addressRecord -> appResponse(addressRecord, ADDRESS_MSG))
                        .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER_PROFILE), BAD_REQUEST.value()))
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> updateAddress(AddressRequestRecord requestRecord) {
        log.info("updating the user address");
        return validateCountry(requestRecord.addressRecord().country())
                .flatMap(country -> validateUser(requestRecord.publicId())
                        .flatMap(user ->  addressRepository.findById(getOrDefault(user.getId()))
                                        .flatMap(address -> {
                                            address.setCity(requestRecord.addressRecord().city());
                                            address.setStreet(requestRecord.addressRecord().street());
                                            address.setState(requestRecord.addressRecord().state());
                                            address.setPostCode(requestRecord.addressRecord().postCode());
                                            address.setCountry(requestRecord.addressRecord().country());
                                            address.setLanguage(requestRecord.addressRecord().language());
                                            return addressRepository.save(address);
                                        })
                            .map(AddressConverter::mapToRecord)
                            .map(addressRecord -> appResponse(addressRecord, ADDRESS_MSG))
                            .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                        )
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> deleteAddress(String publicId) {
        log.info("Deleting the user address");
        return validateUser(publicId)
                .flatMap(user ->  addressRepository.deleteById(getOrDefault(user.getId())))
                .then(Mono.fromCallable(() -> appResponse( "User Deleted", ADDRESS_MSG)));
    }


    private Mono<Country> validateCountry(String name) {
        return countryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_COUNTRY), NOT_FOUND.value()));
    }
    private Mono<User> validateUser(String publicId) {
        return userRepository.findByPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), NOT_FOUND.value()));
    }

    private static List<String> json(String value) throws JsonProcessingException {
        return AppUtil.getMapper().readValue(value, new TypeReference<>(){});
    }


    private int getOrDefault(Integer value) {
        return Objects.isNull(value) ? 0 : value;
    }

}
