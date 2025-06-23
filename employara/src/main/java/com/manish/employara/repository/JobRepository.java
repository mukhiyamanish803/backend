package com.manish.employara.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.Status;
import com.manish.employara.models.recruiter.Job;

public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findAllByUserId(String userId);

    List<Job> findAllByCompanyId(String companyId);

    List<Job> findAllByStatus(Status status);

    long countByStatus(Status status);

    long countByUserId(String userId);

    List<Job> findTop5ByUserIdOrderByCreatedAtDesc(String userId);

    List<Job> findByJobTitleContainingIgnoreCaseAndStatusAndCategoryAndLocationAndJobTypeAndExperienceLevel(
            String keyword, Status status, String category, String location, String jobType, String experienceLevel);

    List<Job> findByJobTitleContainingIgnoreCaseAndStatus(String keyword, Status status);

    List<Job> findAllByStatusAndCategory(Status status, String category);

    List<Job> findAllByStatusAndJobType(Status status, String jobType);

    List<Job> findAllByStatusAndExperienceLevel(Status status, String experienceLevel);

    List<Job> findAllByStatusAndJobTypeAndExperienceLevel(Status status, String jobType, String experienceLevel);

    List<Job> findAllByStatusAndCategoryAndJobType(Status status, String category, String jobType);

    List<Job> findAllByStatusAndCategoryAndJobTypeAndExperienceLevel(
            Status status, String category, String jobType, String experienceLevel);

    List<Job> findAllByStatusAndCategoryAndExperienceLevel(Status status, String category, String experienceLevel);

    List<Job> findAllByCategory(String category);
}
