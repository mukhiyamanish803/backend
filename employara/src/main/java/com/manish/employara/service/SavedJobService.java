package com.manish.employara.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.employara.dto.BasicJobDetailsDTO;
import com.manish.employara.exception.ResourceNotFoundException;
import com.manish.employara.models.User;
import com.manish.employara.models.jobseeker.SavedJob;
import com.manish.employara.models.recruiter.Job;
import com.manish.employara.repository.JobRepository;
import com.manish.employara.repository.jobseeker.SavedJobRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedJobService {
    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final VerifyUser verifyUser;

    public ResponseEntity<?> saveJob(HttpServletRequest request, String jobId) {
        User user = verifyUser.verify(request);
        if (user == null) {
            throw new RuntimeException("User Not Found.");
        }

        // Get saved jobs for the user (or create new one)
        SavedJob savedJob = savedJobRepository.findByUserId(user.getId()).orElse(
                SavedJob.builder()
                        .userId(user.getId())
                        .jobId(new HashSet<>()) // initialize if new
                        .build());

        Set<String> jobIds = savedJob.getJobId();
        boolean isAlreadySaved = jobIds.contains(jobId);

        if (isAlreadySaved) {
            jobIds.remove(jobId); // toggle: remove if exists
        } else {
            jobIds.add(jobId); // add if not present
        }

        // Save updated SavedJob
        savedJob.setJobId(jobIds); // optional, just for clarity
        savedJobRepository.save(savedJob);

        String message = isAlreadySaved ? "Job removed from saved list." : "Job saved successfully.";
        return ResponseEntity.ok(Map.of("message", message));
    }

    public ResponseEntity<?> getSavedJob(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            throw new RuntimeException("User Not Found.");
        }

        SavedJob savedJob = savedJobRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No job saved yet."));

        List<BasicJobDetailsDTO> jobs = jobRepository.findAllById(savedJob.getJobId()).stream()
                .map(Job::getBasicDetails)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobs);
    }

}
