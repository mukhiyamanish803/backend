package com.manish.employara.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.DialCode;

public interface DialCodeRepository extends MongoRepository<DialCode, String> {
    Optional<DialCode> findByCountryName(String countryName);

    boolean existsByCountryNameAndCountryCodeAndDialCode(String countryName, String countryCode, String dialCode);

    void deleteByCountryNameAndCountryCodeAndDialCode(String countryName, String countryCode, String dialCode);


}
