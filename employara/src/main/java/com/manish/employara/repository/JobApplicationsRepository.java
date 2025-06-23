package com.manish.employara.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manish.employara.models.JobApplications;
import com.manish.employara.models.Status;

public interface JobApplicationsRepository extends MongoRepository<JobApplications, String> {
    boolean existsByApplicantIdAndJobId(String applicantId, String jobId);

    Optional<JobApplications> findByApplicantIdAndJobId(String applicantId, String jobId);

    List<JobApplications> findAllByCompanyId(String companyId);

    List<JobApplications> findAllByApplicantId(String applicantId);

    long countByJobId(String jobId);

    long countByCompanyId(String companyId);

    long countByCompanyIdAndStatus(String companyId, Status status);
}
