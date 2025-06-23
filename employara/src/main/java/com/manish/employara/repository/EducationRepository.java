package com.manish.employara.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.jobseeker.Education;

public interface EducationRepository extends MongoRepository<Education, String> {
    Optional<Education> findByUserId(String userId);
    boolean existsByUserId(String userId);
    List<Education> findAllByUserId(String userId);
}
