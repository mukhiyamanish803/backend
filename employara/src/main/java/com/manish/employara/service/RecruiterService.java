package com.manish.employara.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish.employara.dto.DocumentsDTO;
import com.manish.employara.dto.recruiter.CompanyDTO;
import com.manish.employara.dto.recruiter.JobDTO;
import com.manish.employara.dto.request.InternshipRequestDTO;
import com.manish.employara.dto.response.CertificateResponseDTO;
import com.manish.employara.dto.response.EducationResponseDTO;
import com.manish.employara.dto.response.ExperienceResponseDTO;
import com.manish.employara.dto.response.ProfileResponseDTO;
import com.manish.employara.dto.response.ProjectsResponseDTO;
import com.manish.employara.dto.response.UserResponseDTO;
import com.manish.employara.exception.ResourceNotFoundException;
import com.manish.employara.models.Documents;
import com.manish.employara.models.JobApplications;
import com.manish.employara.models.Role;
import com.manish.employara.models.Status;
import com.manish.employara.models.User;
import com.manish.employara.models.jobseeker.Certificate;
import com.manish.employara.models.jobseeker.Education;
import com.manish.employara.models.jobseeker.Experience;
import com.manish.employara.models.jobseeker.Profile;
import com.manish.employara.models.jobseeker.Project;
import com.manish.employara.models.recruiter.Company;
import com.manish.employara.models.recruiter.Hackathon;
import com.manish.employara.models.recruiter.Internship;
import com.manish.employara.models.recruiter.Job;
import com.manish.employara.repository.CertificateRepository;
import com.manish.employara.repository.CompanyRepository;
import com.manish.employara.repository.EducationRepository;
import com.manish.employara.repository.ExperienceRepository;
import com.manish.employara.repository.HackathonRepository;
import com.manish.employara.repository.InternshipRepository;
import com.manish.employara.repository.JobApplicationsRepository;
import com.manish.employara.repository.JobRepository;
import com.manish.employara.repository.ProjectRepository;
import com.manish.employara.repository.UserRepository;
import com.manish.employara.repository.admin.management.CategoryRepository;
import com.manish.employara.repository.jobseeker.ProfileRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final InternshipRepository internshipRepository;
    private final HackathonRepository hackathonRepository;
    private final VerifyUser verifyUser;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final CompanyRepository companyRepository;
    private final JobApplicationsRepository jobApplicationsRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;
    private static final String COMPANY_UPLOAD_PATH = "employara/user/recruiter/company";

    public ResponseEntity<?> getMyCompany(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Company not found"));
        }

        CompanyDTO responseDTO = modelMapper.map(company, CompanyDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

    public ResponseEntity<?> companyDetails(HttpServletRequest request, String data, MultipartFile logo) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        try {
            CompanyDTO companyDTO = objectMapper.readValue(data, CompanyDTO.class);

            // Validate incoming DTO
            Set<ConstraintViolation<CompanyDTO>> violations = validator.validate(companyDTO);
            if (!violations.isEmpty()) {
                Map<String, String> errors = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage));
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Validation failed", "errors", errors));
            }

            Company company = companyRepository.findByUserId(user.getId()).orElse(new Company());
            // Handle logo upload if present
            if (logo != null && !logo.isEmpty()) {
                try {
                    String fileUrl = fileStorageService.uploadFile(logo, COMPANY_UPLOAD_PATH);
                    DocumentsDTO doc = DocumentsDTO.builder()
                            .fileName(logo.getOriginalFilename())
                            .fileType(logo.getContentType())
                            .fileUrl(fileUrl)
                            .uploadDate(LocalDate.now())
                            .build();
                    companyDTO.setLogo(doc);

                    if (company.getLogo() != null && company.getLogo().getFileUrl() != null) {
                        fileStorageService.deleteFromCloudinary(company.getLogo().getFileUrl());
                    }
                } catch (Exception e) {
                    log.error("Failed to upload logo: {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Failed to upload logo", "error", e.getMessage()));
                }
            }

            // Optionally update existing company or create new

            modelMapper.map(companyDTO, company); // map updated fields
            company.setUserId(user.getId());

            // Save the updated or new company
            companyRepository.save(company);

            return ResponseEntity.ok(Map.of("message", "Company details saved successfully", "company", companyDTO));
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON: {}", e.getOriginalMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid JSON for company data", "error", e.getOriginalMessage()));
        }
    }

    public ResponseEntity<?> saveJob(
            HttpServletRequest request,
            JobDTO jobDTO,
            @Nullable String id) {
        System.out.println(id);
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Complete the Company profile first."));
        }

        Job job = (id != null)
                ? jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found."))
                : new Job();

        modelMapper.map(jobDTO, job);

        job.setUserId(user.getId());
        job.setCompanyId(company.getId());
        job.setCompanyName(company.getCompanyName());
        job.setCompanyAddress(company.getAddress());
        job.setCompanyLogoUrl(company.getLogo().getFileUrl());
        job.setWebsite(company.getWebsite());
        job.setAboutCompany(company.getDescription());
        if (id == null) {
            job.setCreatedAt(LocalDate.now());
            job.setStatus(Status.ACTIVE);
            // Increment job count for the category when creating a new job
            categoryRepository.incrementJobCount(job.getCategory());
        }
        if (id != null) {
            job.setUpdatedAt(LocalDate.now());
        }

        jobRepository.save(job);
        job.setUserId(null);
        return ResponseEntity.ok(job);
    }

    public ResponseEntity<?> findJob(HttpServletRequest request, String id) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job Not Found."));

        if (!job.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You don't have permission to access this job"));
        }

        job.setUserId(null);

        return ResponseEntity.ok(job);
    }

    public ResponseEntity<?> findAllJobs(
            HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        List<Job> jobs = jobRepository.findAllByUserId(user.getId());

        for (Job job : jobs) {
            job.setUserId(null);
        }
        return ResponseEntity.ok(jobs);
    }

    public ResponseEntity<?> deleteJob(
            HttpServletRequest request,
            String id) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        if (!job.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You don't have permission to update this job."));
        }

        // Only decrement if the job was active
        if (job.getStatus() == Status.ACTIVE) {
            categoryRepository.decrementJobCount(job.getCategory());
        }

        job.setStatus(Status.CLOSED);
        job.setClosedAt(LocalDate.now());
        jobRepository.save(job);
        return ResponseEntity.ok("Job is closed now.");
    }

    public ResponseEntity<?> getCandidates(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        List<JobApplications> applications = jobApplicationsRepository.findAllByCompanyId(
                companyRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Company not found"))
                        .getId());

        return ResponseEntity.ok(applications);
    }

    public ResponseEntity<?> getCandidateDetails(
            HttpServletRequest request,
            String id) {

        User user = verifyUser.verify(request);
        if (user == null) {
            throw new RuntimeException("User Not found.");
        }

        if (!user.getRole().equals(Role.RECRUITER)) {
            throw new RuntimeException("You don't have permission to access this resource");
        }

        User candidate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found."));

        Profile candidateProfile = profileRepository.findByUserId(candidate.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Fetch all related data using correct repository methods
        List<Experience> experiences = experienceRepository.findByUserId(candidate.getId());
        List<Education> education = educationRepository.findAllByUserId(candidate.getId());
        List<Project> projects = projectRepository.findByUserId(candidate.getId());
        List<Certificate> certificates = certificateRepository.findByUser(candidate);

        // Collect all skills from different sources
        Set<String> skills = new HashSet<>();
        experiences.forEach(exp -> {
            if (exp.getSkills() != null) {
                skills.addAll(exp.getSkills());
            }
        });
        education.forEach(edu -> {
            if (edu.getSkills() != null) {
                skills.addAll(edu.getSkills());
            }
        });
        certificates.forEach(cert -> {
            if (cert.getSkillsAcquired() != null) {
                skills.addAll(cert.getSkillsAcquired());
            }
        });

        Map<String, Object> candidateDetails = new HashMap<>();

        candidateDetails.put("user", UserResponseDTO.createResponse(candidate));
        candidateDetails.put("profile", ProfileResponseDTO.createResponse(candidateProfile));
        List<EducationResponseDTO> e = new ArrayList<>();

        for (Education ed : education) {
            e.add(EducationResponseDTO.createResponse(ed));
        }

        candidateDetails.put("education", e);

        List<ExperienceResponseDTO> exp = new ArrayList<>();

        for (Experience ex : experiences) {
            exp.add(ExperienceResponseDTO.createResponse(ex));
        }

        candidateDetails.put("experience", exp);

        List<ProjectsResponseDTO> pr = new ArrayList<>();

        for (Project p : projects) {
            pr.add(ProjectsResponseDTO.createResponse(p));
        }

        candidateDetails.put("projects", pr);

        List<CertificateResponseDTO> crt = new ArrayList<>();

        for (Certificate c : certificates) {
            crt.add(CertificateResponseDTO.createResponse(c));
        }

        candidateDetails.put("certificates", crt);

        // Add collected skills
        candidateDetails.put("skills", new ArrayList<>(skills));

        return ResponseEntity.ok(candidateDetails);
    }

    public ResponseEntity<?> getDashboardStats(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Complete the Company profile first."));
        }

        // Get counts
        long totalJobs = jobRepository.countByUserId(user.getId());
        long activeCandidates = jobApplicationsRepository.countByCompanyIdAndStatus(
                company.getId(),
                Status.ACTIVE);
        long interviews = jobApplicationsRepository.countByCompanyIdAndStatus(
                company.getId(),
                Status.INTERVIEW);
        long hired = jobApplicationsRepository.countByCompanyIdAndStatus(
                company.getId(),
                Status.HIRED);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total_jobs", totalJobs);
        stats.put("active_candidates", activeCandidates);
        stats.put("interviews", interviews);
        stats.put("hired", hired);

        return ResponseEntity.ok(stats);
    }

    public ResponseEntity<?> getRecentJobs(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Complete the Company profile first."));
        }

        // Get 5 most recent jobs with their application counts
        List<Map<String, Object>> recentJobs = jobRepository.findTop5ByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(job -> {
                    Map<String, Object> jobData = new HashMap<>();
                    jobData.put("title", job.getJobTitle());
                    jobData.put("applicants", jobApplicationsRepository.countByJobId(job.getId()));
                    jobData.put("status", job.getStatus().toString());
                    return jobData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(recentJobs);
    }

    public ResponseEntity<?> getHiringProgress(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }

        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Complete the Company profile first."));
        }

        // Get total applications for percentage calculations
        long totalApplications = jobApplicationsRepository.countByCompanyId(company.getId());

        if (totalApplications == 0) {
            Map<String, Integer> progress = new HashMap<>();
            progress.put("applicationsReview", 0);
            progress.put("interviews", 0);
            progress.put("offersSent", 0);
            return ResponseEntity.ok(progress);
        }

        // Calculate percentages for each stage
        long inReview = jobApplicationsRepository.countByCompanyIdAndStatus(company.getId(), Status.IN_REVIEW);
        long interviews = jobApplicationsRepository.countByCompanyIdAndStatus(company.getId(), Status.INTERVIEW);
        long offersSent = jobApplicationsRepository.countByCompanyIdAndStatus(company.getId(), Status.HIRED);

        Map<String, Integer> progress = new HashMap<>();
        progress.put("applicationsReview", (int) ((inReview * 100.0) / totalApplications));
        progress.put("interviews", (int) ((interviews * 100.0) / totalApplications));
        progress.put("offersSent", (int) ((offersSent * 100.0) / totalApplications));

        return ResponseEntity.ok(progress);
    }

    public ResponseEntity<?> saveInternship(
            HttpServletRequest request,
            String id,
            InternshipRequestDTO internshipRequestDTO) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        Company company = companyRepository.findByUserId(user.getId())
                .orElse(null);
        if (company == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Complete the Company profile first."));
        }
        Internship internship = (id != null)
                ? internshipRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Internship not found."))
                : new Internship();
        modelMapper.map(internshipRequestDTO, internship);

        internship.setRecruiterId(user.getId());
        internship.setCompanyName(company.getCompanyName());
        internship.setCompanyLogoUrl(company.getLogo().getFileUrl());
        internship.setCompanyWebsite(company.getWebsite());
        internship.setCompanyDescription(company.getDescription());
        internship.setCompanyAddress(company.getAddress());
        internship.setUpdatedAt(LocalDate.now());
        internshipRepository.save(internship);
        return ResponseEntity.ok(internship);
    }

    public ResponseEntity<?> getAllInternships(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        List<Internship> internships = internshipRepository.findAllByRecruiterId(user.getId());
        return ResponseEntity.ok(internships);
    }

    public ResponseEntity<?> deleteInternship(
            HttpServletRequest request,
            String id) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found."));
        if (!internship.getRecruiterId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You don't have permission to delete this internship."));
        }
        internshipRepository.delete(internship);
        return ResponseEntity.ok(Map.of("message", "Internship deleted successfully."));
    }

    public ResponseEntity<?> updateInternshipStatus(
            HttpServletRequest request,
            String id,
            String status) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found."));
        if (!internship.getRecruiterId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You don't have permission to update this internship."));
        }
        internship.setStatus(Status.valueOf(status.toUpperCase()));
        internshipRepository.save(internship);
        return ResponseEntity.ok(internship);
    }

    public ResponseEntity<?> updateJobStatus(
            HttpServletRequest request,
            String id,
            String status) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        if (!job.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You don't have permission to update this job."));
        }
        job.setStatus(Status.valueOf(status.toUpperCase()));
        jobRepository.save(job);
        return ResponseEntity.ok(job);
    }

    // saveHackathon
    public ResponseEntity<?> saveHackathon(
            HttpServletRequest request,
            String id,
            String data,
            MultipartFile bannerImage) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        Hackathon hackathon = (id != null)
                ? hackathonRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found."))
                : new Hackathon();
        hackathon.setRecruiterId(user.getId());
        hackathon.setDetails(data);
        if (bannerImage != null) {
            try {
                String fileUrl = fileStorageService.uploadFile(bannerImage, "employara/user/recruiter/hackathon");
                Documents doc = Documents.builder()
                        .fileName(bannerImage.getOriginalFilename())
                        .fileType(bannerImage.getContentType())
                        .fileUrl(fileUrl)
                        .uploadDate(LocalDate.now())
                        .build();
                hackathon.setBannerImage(doc);
            } catch (Exception e) {
                log.error("Failed to upload banner image: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Failed to upload banner image", "error", e.getMessage()));
            }
        }
        hackathonRepository.save(hackathon);
        return ResponseEntity.ok(hackathon);
    }

    public ResponseEntity<?> getAllHackathons(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized user"));
        }
        List<Hackathon> hackathons = hackathonRepository.findAllByRecruiterId(user.getId());
        return ResponseEntity.ok(hackathons);
    }
}
