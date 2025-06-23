package com.manish.employara.repository.jobseeker;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.jobseeker.Profile;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    boolean existsByUserId(String userId);
    Optional<Profile> findByUserId(String userId);
}
