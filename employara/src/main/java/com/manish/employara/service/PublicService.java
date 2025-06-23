package com.manish.employara.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.employara.dto.SearchRequestDTO;
import com.manish.employara.dto.response.JobResponseDTO;
import com.manish.employara.exception.ResourceNotFoundException;
import com.manish.employara.models.Notification;
import com.manish.employara.models.Status;
import com.manish.employara.models.User;
import com.manish.employara.models.recruiter.Job;
import com.manish.employara.repository.JobRepository;
import com.manish.employara.repository.NotificationRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicService {
    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;
    private final VerifyUser verifyUser;
    private final NotificationRepository notificationRepository;

    public ResponseEntity<?> findAllJobs() {
        Status status = Status.ACTIVE;
        List<Job> jobs = jobRepository.findAllByStatus(status);

        List<JobResponseDTO> jobResponseDTOs = new ArrayList<>();
        for (Job job : jobs) {
            jobResponseDTOs.add(modelMapper.map(job, JobResponseDTO.class));
        }

        return ResponseEntity.ok(jobResponseDTOs);
    }

    public ResponseEntity<?> findJob(String id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job Not Found."));
        JobResponseDTO jobResponseDTO = modelMapper.map(job, JobResponseDTO.class);
        return ResponseEntity.ok(jobResponseDTO);
    }

    public ResponseEntity<?> searchJobs(SearchRequestDTO searchRequest) {
        List<Job> jobs;
        Status status = Status.ACTIVE;

        if (isAllParamsEmpty(searchRequest)) {
            jobs = jobRepository.findAllByStatus(status);
        }
        // If category, job type and experience level are present
        else if (isCategoryJobTypeAndExperienceLevelPresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndCategoryAndJobTypeAndExperienceLevel(
                    status,
                    searchRequest.getCategory(),
                    searchRequest.getJobType(),
                    searchRequest.getExperienceLevel());
        }
        // If only category and experience level are present
        else if (isCategoryAndExperienceLevelPresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndCategoryAndExperienceLevel(
                    status,
                    searchRequest.getCategory(),
                    searchRequest.getExperienceLevel());
        }
        // If only category and job type are present
        else if (isCategoryAndJobTypePresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndCategoryAndJobType(
                    status,
                    searchRequest.getCategory(),
                    searchRequest.getJobType());
        }
        // If only category is present
        else if (isOnlyCategoryPresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndCategory(status, searchRequest.getCategory());
        }
        // If only job type is present
        else if (isOnlyJobTypePresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndJobType(status, searchRequest.getJobType());
        }
        // If only experience level is present
        else if (isOnlyExperienceLevelPresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndExperienceLevel(status, searchRequest.getExperienceLevel());
        }
        // If only job type and experience level are present
        else if (isOnlyJobTypeAndExperiencePresent(searchRequest)) {
            jobs = jobRepository.findAllByStatusAndJobTypeAndExperienceLevel(
                    status, searchRequest.getJobType(), searchRequest.getExperienceLevel());
        }
        // If only keyword is present
        else if (isOnlyKeywordPresent(searchRequest)) {
            jobs = jobRepository.findByJobTitleContainingIgnoreCaseAndStatus(searchRequest.getKeyword(), status);
        } else {
            jobs = jobRepository
                    .findByJobTitleContainingIgnoreCaseAndStatusAndCategoryAndLocationAndJobTypeAndExperienceLevel(
                            searchRequest.getKeyword() != null ? searchRequest.getKeyword() : "",
                            status,
                            searchRequest.getCategory(),
                            searchRequest.getLocation(),
                            searchRequest.getJobType(),
                            searchRequest.getExperienceLevel());
        }

        List<JobResponseDTO> jobResponseDTOs = new ArrayList<>();
        for (Job job : jobs) {
            jobResponseDTOs.add(modelMapper.map(job, JobResponseDTO.class));
        }
        return ResponseEntity.ok(jobResponseDTOs);
    }

    private boolean isAllParamsEmpty(SearchRequestDTO request) {
        return (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty()) &&
                (request.getJobType() == null || request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty());
    }

    private boolean isOnlyCategoryPresent(SearchRequestDTO request) {
        return (request.getCategory() != null && !request.getCategory().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty()) &&
                (request.getJobType() == null || request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty());
    }

    private boolean isOnlyKeywordPresent(SearchRequestDTO request) {
        return (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty()) &&
                (request.getJobType() == null || request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty());
    }

    private boolean isOnlyJobTypePresent(SearchRequestDTO request) {
        return (request.getJobType() != null && !request.getJobType().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty()) &&
                (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty());
    }

    private boolean isOnlyExperienceLevelPresent(SearchRequestDTO request) {
        return (request.getExperienceLevel() != null && !request.getExperienceLevel().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty()) &&
                (request.getJobType() == null || request.getJobType().trim().isEmpty());
    }

    private boolean isOnlyJobTypeAndExperiencePresent(SearchRequestDTO request) {
        return (request.getJobType() != null && !request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() != null && !request.getExperienceLevel().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getCategory() == null || request.getCategory().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty());
    }

    private boolean isCategoryAndExperienceLevelPresent(SearchRequestDTO request) {
        return (request.getCategory() != null && !request.getCategory().trim().isEmpty()) &&
                (request.getExperienceLevel() != null && !request.getExperienceLevel().trim().isEmpty()) &&
                (request.getJobType() == null || request.getJobType().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty());
    }

    private boolean isCategoryAndJobTypePresent(SearchRequestDTO request) {
        return (request.getCategory() != null && !request.getCategory().trim().isEmpty()) &&
                (request.getJobType() != null && !request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() == null || request.getExperienceLevel().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty());
    }

    private boolean isCategoryJobTypeAndExperienceLevelPresent(SearchRequestDTO request) {
        return (request.getCategory() != null && !request.getCategory().trim().isEmpty()) &&
                (request.getJobType() != null && !request.getJobType().trim().isEmpty()) &&
                (request.getExperienceLevel() != null && !request.getExperienceLevel().trim().isEmpty()) &&
                (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                (request.getLocation() == null || request.getLocation().trim().isEmpty());
    }

    public ResponseEntity<?> findJobsByCategory(String category) {
        List<Job> jobs = jobRepository.findAllByCategory(category);
        if (jobs.isEmpty()) {
            throw new ResourceNotFoundException("No jobs found for the given category.");
        }

        List<JobResponseDTO> jobResponseDTOs = new ArrayList<>();
        for (Job job : jobs) {
            jobResponseDTOs.add(modelMapper.map(job, JobResponseDTO.class));
        }
        return ResponseEntity.ok(jobResponseDTOs);
    }

    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        // Fetch all notifications for the user (not just unread)
        List<Notification> notifications = notificationRepository.findAllByRecipientId(user.getId());

        // Always return 200 OK with the list (even if empty)
        return ResponseEntity.ok(notifications);
    }
}
