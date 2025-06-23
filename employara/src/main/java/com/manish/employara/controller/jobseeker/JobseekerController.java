package com.manish.employara.controller.jobseeker;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manish.employara.dto.JobApplicationsRequestDTO;
import com.manish.employara.service.JobApplicationsService;
import com.manish.employara.service.JobseekerService;
import com.manish.employara.service.SavedJobService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/jobseeker")
@RequiredArgsConstructor
public class JobseekerController {
    private final JobseekerService userService;
    private final JobApplicationsService jobApplicationsService;
    private final SavedJobService savedJobService;

    @PostMapping("/save-education")
    public ResponseEntity<?> saveEducation(
            HttpServletRequest request,
            @RequestPart("education") String educationJson,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents,
            @RequestParam(value = "deletedDocuments", required = false) List<String> deletedDocuments,
            @RequestParam(value = "id", required = false) String id) {
        return userService.saveEducation(request, educationJson, documents, deletedDocuments, id);
    }

    @GetMapping("/education")
    public ResponseEntity<?> getAllEducations(HttpServletRequest request) {
        return userService.getEducations(request);
    }

    @GetMapping("/get-my-profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("userData") String userData,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletRequest request,
            @RequestParam(value = "deleteImageUrl", required = false) String url) {
        return userService.updateProfile(userData, profileImage, request, url);
    }

    @DeleteMapping("/education/{id}")
    public ResponseEntity<?> deleteEducation(
            @PathVariable String id,
            HttpServletRequest request) {
        return userService.deleteEducation(id, request);
    }

    @GetMapping("/get-all-certificates")
    public ResponseEntity<?> getAllCertificates(HttpServletRequest request) {
        System.out.println("Getting all certificates for user"); // Debug log
        ResponseEntity<?> response = userService.getAllCertificates(request);
        System.out.println("Response status: " + response.getStatusCode()); // Debug log
        return response;
    }

    @PostMapping("/add-certificate")
    public ResponseEntity<?> addCertificate(@RequestParam("data") String data,
            @RequestParam(value = "documents", required = false) MultipartFile documents, HttpServletRequest request) {
        return userService.addCertificate(request, data, documents);
    }

    @PutMapping("/update-certificate/{id}")
    public ResponseEntity<?> updateCertificate(
            @PathVariable String id,
            @RequestParam("data") String data,
            @RequestParam(value = "documents", required = false) MultipartFile documents,
            HttpServletRequest request) {
        return userService.updateCertificate(id, data, documents, request);
    }

    @DeleteMapping("/delete-certificate/{id}")
    public ResponseEntity<?> deleteCertificate(
            @PathVariable String id,
            HttpServletRequest request) {
        return userService.deleteCertificate(id, request);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(HttpServletRequest request,
            @Valid @RequestBody JobApplicationsRequestDTO application) {
        return jobApplicationsService.apply(request, application);
    }

    @PostMapping("/save-job/{jobId}")
    public ResponseEntity<?> saveJob(HttpServletRequest request, @PathVariable String jobId) {
        return savedJobService.saveJob(request, jobId);
    }

    @GetMapping("/saved-jobs")
    public ResponseEntity<?> getAllSavedJobs(HttpServletRequest request) {
        return savedJobService.getSavedJob(request);
    }

    @GetMapping("/get-all-applied-jobs")
    public ResponseEntity<?> getAppliedJobs(HttpServletRequest request) {
        return jobApplicationsService.appliedJobs(request);
    }

    /**
     * Save or update a job experience entry
     * 
     * @param request    The HTTP request
     * @param file       Optional certificate file (PDF, JPG, JPEG, PNG)
     * @param experience JSON string containing:
     *                   - jobTitle (required)
     *                   - companyName (required)
     *                   - companyUrl
     *                   - startDate (required)
     *                   - endDate
     *                   - current (boolean)
     *                   - certificateLink
     *                   - technologies (string array)
     *                   - description
     *                   - companyLocation (object with street, city, state,
     *                   country, zipCode)
     * @return ResponseEntity with the saved experience data
     */
    @PostMapping("/save-experience")
    public ResponseEntity<?> saveOrUpdateExperience(
            HttpServletRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("data") String experience) {
        return userService.saveOrUpdateExperience(request, file, experience);
    }

    @GetMapping("/experiences")
    public ResponseEntity<?> getAllExperiences(HttpServletRequest request) {
        return userService.getAllExperiences(request);
    }

    @DeleteMapping("/experience/{id}")
    public ResponseEntity<?> deleteExperience(
            @PathVariable String id,
            HttpServletRequest request) {
        return userService.deleteExperience(id, request);
    }

    @PostMapping("/save-project")
    public ResponseEntity<?> saveOrUpdateProject(
            HttpServletRequest request,
            @RequestPart(value = "screenshots", required = false) List<MultipartFile> screenshots,
            @RequestParam(value = "deletedScreenshots", required = false) List<String> deletedScreenshots,
            @RequestParam("data") String projectJson) {
        return userService.saveOrUpdateProject(request, screenshots, deletedScreenshots, projectJson);
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getAllProjects(HttpServletRequest request) {
        return userService.getAllProjects(request);
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable String id,
            HttpServletRequest request) {
        return userService.deleteProject(id, request);
    }

}
