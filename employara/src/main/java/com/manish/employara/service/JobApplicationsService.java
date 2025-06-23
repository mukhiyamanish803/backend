package com.manish.employara.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.manish.employara.dto.JobApplicationUpdateRequestDTO;
import com.manish.employara.dto.JobApplicationsRequestDTO;
import com.manish.employara.exception.ResourceNotFoundException;
import com.manish.employara.models.JobApplications;
import com.manish.employara.models.Status;
import com.manish.employara.models.User;
import com.manish.employara.models.recruiter.Job;
import com.manish.employara.repository.JobApplicationsRepository;
import com.manish.employara.repository.JobRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationsService {
    private final JobApplicationsRepository jobApplicationsRepository;
    private final NotificationService notificationService;
    private final JobRepository jobRepository;
    private final VerifyUser verifyUser;

    private boolean hasAlreadyApplied(String applicantId, String jobId) {
        return jobApplicationsRepository.existsByApplicantIdAndJobId(applicantId, jobId);
    }

    public ResponseEntity<?> apply(
            HttpServletRequest request,
            JobApplicationsRequestDTO jobApplication) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        if (hasAlreadyApplied(user.getId(), jobApplication.getJobId())) {
            throw new IllegalStateException("Already applied to this job");
        }

        Job job = jobRepository.findById(jobApplication.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Not Found"));

        if (job.getStatus().equals(Status.CLOSED)) {
            throw new IllegalStateException("This job is no longer accepting applications.");
        }

        // Create JobApplications object manually instead of using ModelMapper
        JobApplications application = JobApplications.builder()
                .jobId(jobApplication.getJobId())
                .jobTitle(jobApplication.getJobTitle())
                .companyId(jobApplication.getCompanyId())
                .companyName(job.getCompanyName())
                .interest(jobApplication.getInterest())
                .coverLetter(jobApplication.getCoverLetter())
                .applicantId(user.getId())
                .applicantEmail(user.getEmail())
                .applicantFirstName(user.getFirstName())
                .applicantLastName(user.getLastName())
                .status(Status.ACTIVE)
                .createdAt(LocalDate.now())
                .build();

        jobApplicationsRepository.save(application);
        int appliedCount = job.getAppliedCount() + 1;
        job.setAppliedCount(appliedCount);
        jobRepository.save(job);

        notificationService.notifyUser(
                job.getUserId(),
                Map.of("message", user.getFirstName() + " has applied to your job: " + job.getJobTitle()),
                "JOB_APPLICATION");

        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    public ResponseEntity<?> updateJobAapplicationStatus(HttpServletRequest request,
            JobApplicationUpdateRequestDTO data) {

        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        JobApplications jobApplication = jobApplicationsRepository
                .findByApplicantIdAndJobId(data.getAppplicantId(), data.getJobId())
                .orElseThrow(() -> new IllegalStateException("Applicant Not Found."));

        jobApplication.setStatus(data.getStatus());
        jobApplicationsRepository.save(jobApplication);

        // Send notification to the applicant
        Map<String, String> message;
        if (data.getStatus() == Status.REJECTED) {
            message = Map.of(
                    "message", "Your application for " + jobApplication.getJobTitle() + " has been rejected.",
                    "jobId", jobApplication.getJobId(),
                    "status", jobApplication.getStatus().name());
        } else if (data.getStatus() == Status.SHORTLISTED) {
            message = Map.of(
                    "message", "Congratulations! Your application for " + jobApplication.getJobTitle()
                            + " has been shortlisted.",
                    "jobId", jobApplication.getJobId(),
                    "status", jobApplication.getStatus().name());
        } else if (data.getStatus() == Status.IN_REVIEW) {
            message = Map.of(
                    "message",
                    "Your application for " + jobApplication.getJobTitle()
                            + " is in review process. We will inform you soon.",
                    "jobId", jobApplication.getJobId(),
                    "status", jobApplication.getStatus().name());
        } else if (data.getStatus() == Status.INTERVIEW) {
            message = Map.of(
                    "message", "Congratulations! You application for " + jobApplication.getJobTitle()
                            + " is schedule for interview.",
                    "jobId", jobApplication.getJobId(),
                    "status", jobApplication.getStatus().name());
        } else {
            message = Map.of(
                    "message",
                    "The status of your application for " + jobApplication.getJobTitle() + " has been updated to "
                            + data.getStatus().name(),
                    "jobId", jobApplication.getJobId(),
                    "status", jobApplication.getStatus().name());
        }

        notificationService.notifyUser(
                data.getAppplicantId(),
                message,
                "APPLICATION_STATUS_UPDATE");

        return ResponseEntity.ok(Map.of("message", "Application status updated successfully"));
    }

    public ResponseEntity<?> appliedJobs(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        List<JobApplications> jobApplications = jobApplicationsRepository.findAllByApplicantId(user.getId());

        return ResponseEntity.ok(jobApplications);
    }
}
