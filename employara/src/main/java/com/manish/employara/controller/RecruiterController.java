package com.manish.employara.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manish.employara.dto.JobApplicationUpdateRequestDTO;
import com.manish.employara.dto.recruiter.JobDTO;
import com.manish.employara.dto.request.InternshipRequestDTO;
import com.manish.employara.service.JobApplicationsService;
import com.manish.employara.service.RecruiterService;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/recruiter")
@RequiredArgsConstructor
public class RecruiterController {
    private final RecruiterService recruiterService;
    private final JobApplicationsService jobApplicationsService;

    @GetMapping("/my-company") // Changed from "/company" to "/my-company"
    public ResponseEntity<?> getCompany(HttpServletRequest request) {
        return recruiterService.getMyCompany(request);
    }

    @PostMapping("/company-details")
    public ResponseEntity<?> companyDetails(
            HttpServletRequest request,
            @RequestParam("data") String data,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {
        return recruiterService.companyDetails(request, data, logo);
    }

    @PostMapping("/save-job")
    public ResponseEntity<?> saveJob(
            HttpServletRequest request,
            @Valid @RequestBody JobDTO job,
            @Nullable @RequestParam(required = false) String id) { // Add RequestParam annotation
        return recruiterService.saveJob(request, job, id);
    }

    @PatchMapping("/update-job-status/{id}")
    public ResponseEntity<?> updateJobStatus(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestParam String status) {
        return recruiterService.updateJobStatus(request, id, status);
    }
    
    @GetMapping("/find-job/{id}")
    public ResponseEntity<?> findJob(
            HttpServletRequest request,
            @PathVariable String id) {
        return recruiterService.findJob(request, id);
    }

    @GetMapping("/find-all-jobs")
    public ResponseEntity<?> findAllJobs(HttpServletRequest request) {
        return recruiterService.findAllJobs(request);
    }

    @DeleteMapping("/delete-job/{id}")
    public ResponseEntity<?> deleteJob(
            HttpServletRequest request,
            @PathVariable String id) {
        return recruiterService.deleteJob(request, id);
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateJobStatus(HttpServletRequest request,
            @Valid @RequestBody JobApplicationUpdateRequestDTO data) {
        return jobApplicationsService.updateJobAapplicationStatus(request, data);
    }

    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidates(HttpServletRequest request) {
        return recruiterService.getCandidates(request);
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<?> candidateDetails(HttpServletRequest request,
            @PathVariable String id) {
        return recruiterService.getCandidateDetails(request, id);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats(HttpServletRequest request) {
        return recruiterService.getDashboardStats(request);
    }

    @GetMapping("/dashboard/recent-jobs")
    public ResponseEntity<?> getRecentJobs(HttpServletRequest request) {
        return recruiterService.getRecentJobs(request);
    }

    @GetMapping("/dashboard/hiring-progress")
    public ResponseEntity<?> getHiringProgress(HttpServletRequest request) {
        return recruiterService.getHiringProgress(request);
    }

    @PostMapping("/save-internship")
    public ResponseEntity<?> saveInternship(
            HttpServletRequest request,
            @RequestParam(required = false) String id,
            @Valid @RequestBody InternshipRequestDTO internshipRequestDTO) {
        return recruiterService.saveInternship(request, id, internshipRequestDTO);
    }

    @GetMapping("/get-all-internships")
    public ResponseEntity<?> getAllInternships(HttpServletRequest request) {
        return recruiterService.getAllInternships(request);
    }

    @DeleteMapping("/delete-internship/{id}")
    public ResponseEntity<?> deleteInternship(
            HttpServletRequest request,
            @PathVariable String id) {
        return recruiterService.deleteInternship(request, id);
    }

    @PatchMapping("/update-internship-status/{id}")
    public ResponseEntity<?> updateInternshipStatus(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestParam String status) {
        return recruiterService.updateInternshipStatus(request, id, status);
    }

    @PostMapping("/save-hackathon")
    public ResponseEntity<?> saveHackathon(
            HttpServletRequest request,
            @RequestParam(required = false) String id,
            @RequestParam("data") String hackathon,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage) {
        return recruiterService.saveHackathon(request, id, hackathon, bannerImage);
    }

    @GetMapping("/get-all-hackathons")
    public ResponseEntity<?> getAllHackathons(HttpServletRequest request) {
        return recruiterService.getAllHackathons(request);
    }
}
