package com.manish.employara.repository.jobseeker;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.manish.employara.models.jobseeker.SavedJob;


public interface SavedJobRepository extends MongoRepository<SavedJob, String> {
    Optional<SavedJob> findByUserId(String userId);
}
