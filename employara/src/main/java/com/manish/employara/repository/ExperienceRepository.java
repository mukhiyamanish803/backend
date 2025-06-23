package com.manish.employara.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.jobseeker.Experience;

public interface ExperienceRepository extends MongoRepository<Experience, String> {
    List<Experience> findByUserId(String userId);
}
