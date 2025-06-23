package com.manish.employara.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.recruiter.Company;


public interface CompanyRepository extends MongoRepository<Company, String> {
    Optional<Company> findByBusinessEmail(String businessEmail);
    boolean existsByBusinessEmail(String businessEmail);
    Optional<Company> findByUserId(String userId);
}
