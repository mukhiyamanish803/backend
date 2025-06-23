package com.manish.employara.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.manish.employara.dto.CertificateDTO;
import com.manish.employara.dto.DocumentsDTO;
import com.manish.employara.dto.EducationDTO;
import com.manish.employara.dto.ProjectDTO;
import com.manish.employara.dto.jobseeker.ProfileDTO;
import com.manish.employara.exception.ResourceNotFoundException;
import com.manish.employara.models.Address;
import com.manish.employara.models.Documents;
import com.manish.employara.models.User;
import com.manish.employara.models.jobseeker.Certificate;
import com.manish.employara.models.jobseeker.Education;
import com.manish.employara.models.jobseeker.Experience;
import com.manish.employara.models.jobseeker.Profile;
import com.manish.employara.models.jobseeker.Project;
import com.manish.employara.repository.CertificateRepository;
import com.manish.employara.repository.EducationRepository;
import com.manish.employara.repository.ExperienceRepository;
import com.manish.employara.repository.ProjectRepository;
import com.manish.employara.repository.jobseeker.ProfileRepository;
import com.manish.employara.utils.VerifyUser;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobseekerService {
    private final EducationRepository educationRepository;
    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final VerifyUser verifyUser;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public ResponseEntity<?> saveEducation(
            HttpServletRequest request,
            String educationJson,
            List<MultipartFile> documents,
            List<String> deletedDocuments,
            @Nullable String id) {

        try {
            User user = verifyUser.verify(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Unauthorized User"));
            }

            // Validate files first
            if (documents != null && !documents.isEmpty()) {
                for (MultipartFile file : documents) {
                    if (!isValidFileType(file.getContentType())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Invalid file type. Only PDF, DOC, DOCX allowed"));
                    }
                    if (file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "File size should not exceed 10MB"));
                    }
                }
            } // Parse education JSON and validate
            EducationDTO educationDTO = objectMapper.readValue(educationJson, EducationDTO.class);
            Set<ConstraintViolation<EducationDTO>> violations = validator.validate(educationDTO);
            if (!violations.isEmpty()) {
                Map<String, String> errors = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage));
                return ResponseEntity.badRequest().body(Map.of("errors", errors));
            }

            // Perform additional validations
            try {
                validateEducation(educationDTO, user.getId(), id);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }

            // Initialize or fetch education
            Education education = (id != null) ? educationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Education not found")) : new Education();

            if (id != null && !education.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You don't have permission to edit this education"));
            }

            // Initialize documents list if null
            if (education.getDocuments() == null) {
                education.setDocuments(new ArrayList<>());
            }

            // Handle deleted documents
            if (deletedDocuments != null && !deletedDocuments.isEmpty()) {
                for (String url : deletedDocuments) {
                    fileStorageService.deleteFromCloudinary(url);
                    education.getDocuments().removeIf(doc -> url.equals(doc.getFileUrl()));
                }
            }

            // Process new documents
            if (documents != null && !documents.isEmpty()) {
                for (MultipartFile file : documents) {
                    if (!isValidFileType(file.getContentType())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Invalid file type. Only PDF, DOC, DOCX allowed"));
                    }
                    if (file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "File size should not exceed 10MB"));
                    }

                    String fileUrl = fileStorageService.uploadFile(file, "employara/education/certificate/");
                    Documents doc = Documents.builder()
                            .fileName(file.getOriginalFilename())
                            .fileType(file.getContentType())
                            .fileUrl(fileUrl)
                            .uploadDate(LocalDate.now())
                            .build();
                    education.getDocuments().add(doc);
                }
            }

            // Map DTO to entity
            modelMapper.map(educationDTO, education);
            education.setUserId(user.getId());

            // Save and create response
            Education savedEducation = educationRepository.save(education);
            EducationDTO responseDTO = modelMapper.map(savedEducation, EducationDTO.class);

            return ResponseEntity.ok(Map.of(
                    "message", "Education saved successfully",
                    "education", responseDTO));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid education data format: " + e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to save education: " + e.getMessage()));
        }
    }

    // Validation methods
    private boolean isValidFileType(String contentType) {
        return contentType != null && (contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    private boolean hasOverlappingEducation(EducationDTO newEducation, String userId, String excludeId) {
        List<Education> existingEducations = educationRepository.findAllByUserId(userId);
        LocalDate newStartDate = newEducation.getStartDate();
        LocalDate newEndDate = newEducation.getEndDate();

        return existingEducations.stream()
                .filter(edu -> excludeId == null || !edu.getId().equals(excludeId))
                .anyMatch(edu -> {
                    LocalDate existingStartDate = edu.getStartDate();
                    LocalDate existingEndDate = edu.getEndDate();

                    // If either education has no end date, consider it as ongoing
                    if (newEndDate == null || existingEndDate == null) {
                        return false;
                    }

                    // Check for overlap
                    return !(newEndDate.isBefore(existingStartDate) || newStartDate.isAfter(existingEndDate));
                });
    }

    private boolean isValidGradeValue(String gradeType, String gradeValue) {
        if (gradeValue == null || gradeValue.trim().isEmpty()) {
            return true; // Allow empty grade values
        }

        try {
            double grade = Double.parseDouble(gradeValue);
            if ("percentage".equals(gradeType)) {
                return grade >= 0 && grade <= 100;
            } else if ("cgpa".equals(gradeType)) {
                return grade >= 0 && grade <= 10;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateEducation(EducationDTO educationDTO, String userId, String excludeId) {
        List<String> errors = new ArrayList<>();

        // Validate dates
        if (educationDTO.getEndDate() != null &&
                educationDTO.getStartDate().isAfter(educationDTO.getEndDate())) {
            errors.add("End date must be after start date");
        }

        // Check for overlapping education periods
        if (hasOverlappingEducation(educationDTO, userId, excludeId)) {
            errors.add("Education period overlaps with existing education records");
        }

        // Validate grade value
        if (!isValidGradeValue(educationDTO.getGradeType(), educationDTO.getGradeValue())) {
            errors.add("Invalid grade value for the selected grade type");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    public ResponseEntity<?> getEducations(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        List<Education> educations = educationRepository.findAllByUserId(user.getId());
        List<EducationDTO> educationDTOs = new ArrayList<>();
        for (Education education : educations) {
            EducationDTO educationDTO = modelMapper.map(education, EducationDTO.class);
            educationDTOs.add(educationDTO);
        }

        return ResponseEntity.ok(educationDTOs);
    }

    public ResponseEntity<?> deleteEducation(String id, HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            Education education = educationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Education not found"));
            for (Documents document : education.getDocuments()) {
                String url = document.getFileUrl();
                fileStorageService.deleteFromCloudinary(url);
            }
            educationRepository.delete(education);

            return ResponseEntity.ok()
                    .body(Map.of("message", "Education deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to delete education: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllCertificates(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized User."));
        }
        try {
            List<Certificate> certificates = certificateRepository.findByUser(user);
            System.out.println("Found certificates: " + certificates.size()); // Debug log
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            System.err.println("Error fetching certificates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch certificates"));
        }
    }

    public ResponseEntity<?> addCertificate(HttpServletRequest request, String data, MultipartFile documents) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User."));
        }
        try {
            CertificateDTO certificateDTO = objectMapper.readValue(data, CertificateDTO.class);

            if ((documents == null || documents.isEmpty()) && (certificateDTO.getCertificateLink() == null
                    || certificateDTO.getCertificateLink().trim().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Either certificate file or certificate link is required."));
            }

            // Validate before any I/O
            Set<ConstraintViolation<CertificateDTO>> violations = validator.validate(certificateDTO);
            if (!violations.isEmpty()) {
                Map<String, String> errorMap = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage));
                return ResponseEntity.badRequest().body(Map.of("errors", errorMap));
            }

            try {
                // Create certificate without references
                Certificate certificate = Certificate.builder()
                        .title(certificateDTO.getTitle())
                        .organization(certificateDTO.getOrganization())
                        .organizationLink(certificateDTO.getOrganizationLink())
                        .certificateLink(certificateDTO.getCertificateLink())
                        .description(certificateDTO.getDescription())
                        .skillsAcquired(certificateDTO.getSkillsAcquired())
                        .dateIssued(certificateDTO.getDateIssued())
                        .dateExpire(certificateDTO.getDateExpire())
                        .user(user)
                        .build();

                // Set address directly if exists
                if (certificateDTO.getAddress() != null) {
                    Address address = Address.builder()
                            .street(certificateDTO.getAddress().getStreet())
                            .city(certificateDTO.getAddress().getCity())
                            .state(certificateDTO.getAddress().getState())
                            .country(certificateDTO.getAddress().getCountry())
                            .zipCode(certificateDTO.getAddress().getZipCode())
                            .build();
                    certificate.setAddress(address);
                }

                // Handle document upload and set directly
                if (documents != null && !documents.isEmpty()) {
                    String fileUrl = fileStorageService.uploadFile(documents, "employara/certification/");
                    Documents doc = Documents.builder()
                            .fileName(documents.getOriginalFilename())
                            .fileType(documents.getContentType())
                            .fileUrl(fileUrl)
                            .uploadDate(LocalDate.now())
                            .build();
                    certificate.setDocuments(doc);
                }

                Certificate savedCertificate = certificateRepository.save(certificate);

                return ResponseEntity.ok().body(Map.of(
                        "message", "Certificate added successfully.",
                        "certificateId", savedCertificate.getId()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Failed to process certificate: " + e.getMessage()));
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid certificate data format: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to add certificate: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> updateCertificate(String id, String data, MultipartFile documents,
            HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User."));
        }

        try {
            Certificate existingCertificate = certificateRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));

            if (!existingCertificate.getUser().getEmail().equals(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You don't have permission to update this certificate."));
            }

            // Parse the certificate data
            CertificateDTO certificateDTO = objectMapper.readValue(data, CertificateDTO.class);

            // Delete old document from Cloudinary if exists and new document is provided
            if (documents != null && !documents.isEmpty() && existingCertificate.getDocuments() != null) {
                String oldFileUrl = existingCertificate.getDocuments().getFileUrl();
                fileStorageService.deleteFromCloudinary(oldFileUrl);
            }

            // Update basic fields
            existingCertificate.setTitle(certificateDTO.getTitle());
            existingCertificate.setOrganization(certificateDTO.getOrganization());
            existingCertificate.setOrganizationLink(certificateDTO.getOrganizationLink());
            existingCertificate.setCertificateLink(certificateDTO.getCertificateLink());
            existingCertificate.setDescription(certificateDTO.getDescription());
            existingCertificate.setSkillsAcquired(certificateDTO.getSkillsAcquired());
            existingCertificate.setDateIssued(certificateDTO.getDateIssued());
            existingCertificate.setDateExpire(certificateDTO.getDateExpire());

            // Update address if provided
            if (certificateDTO.getAddress() != null) {
                Address address = Address.builder()
                        .street(certificateDTO.getAddress().getStreet())
                        .city(certificateDTO.getAddress().getCity())
                        .state(certificateDTO.getAddress().getState())
                        .country(certificateDTO.getAddress().getCountry())
                        .zipCode(certificateDTO.getAddress().getZipCode())
                        .build();
                existingCertificate.setAddress(address);
            }

            // Update document if new one is provided
            if (documents != null && !documents.isEmpty()) {
                String fileUrl = fileStorageService.uploadFile(documents, "employara/certification/");
                Documents doc = Documents.builder()
                        .fileName(documents.getOriginalFilename())
                        .fileType(documents.getContentType())
                        .fileUrl(fileUrl)
                        .uploadDate(LocalDate.now())
                        .build();
                existingCertificate.setDocuments(doc);
            }

            Certificate updatedCertificate = certificateRepository.save(existingCertificate);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Certificate updated successfully",
                            "certificateId", updatedCertificate.getId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to update certificate: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> deleteCertificate(String id, HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User."));
        }

        try {
            Certificate certificate = certificateRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));

            if (!certificate.getUser().getEmail().equals(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You don't have permission to delete this certificate."));
            }

            // Delete document from Cloudinary if exists
            if (certificate.getDocuments() != null) {
                fileStorageService.deleteFromCloudinary(certificate.getDocuments().getFileUrl());
            }

            certificateRepository.delete(certificate);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Certificate deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to delete certificate: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> updateProfile(
            String userData,
            MultipartFile profileImage,
            HttpServletRequest request,
            String deletedImageUrl) {

        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User");
        }

        try {
            ProfileDTO profileDTO = objectMapper.readValue(userData, ProfileDTO.class);
            Set<ConstraintViolation<ProfileDTO>> violations = validator.validate(profileDTO);

            if (!violations.isEmpty()) {
                Map<String, String> errorMap = violations.stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage));
                return ResponseEntity.badRequest().body(errorMap);
            }

            Profile profile = profileRepository.findByUserId(user.getId()).orElse(new Profile());

            if (deletedImageUrl != null && !deletedImageUrl.isEmpty()) {
                fileStorageService.deleteFromCloudinary(deletedImageUrl);
                profile.setProfileImage(new Documents());
            }

            // Upload new profile image if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                String fileUrl = fileStorageService.uploadFile(profileImage, "employara/user/profileImage");
                profileDTO.setProfileImage(DocumentsDTO.builder()
                        .fileName(profileImage.getOriginalFilename())
                        .fileType(profileImage.getContentType())
                        .fileUrl(fileUrl)
                        .uploadDate(LocalDate.now())
                        .build());
            }

            modelMapper.map(profileDTO, profile);

            profile.setUserId(user.getId());

            profileRepository.save(profile);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(profileDTO);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user data format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        User user = verifyUser.verify(request);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        System.out.println(user.getId());
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        System.out.println(profile);

        ProfileDTO profileDTO = modelMapper.map(profile, ProfileDTO.class);

        return ResponseEntity.ok(profileDTO);
    }

    public ResponseEntity<?> saveOrUpdateExperience(
            HttpServletRequest request,
            @Nullable MultipartFile file,
            String experienceJson) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            // Parse experience data from JSON string
            @SuppressWarnings("unchecked")
            Map<String, Object> experienceData = objectMapper.readValue(experienceJson, Map.class);

            // Validate required fields
            String jobTitle = (String) experienceData.get("jobTitle");
            String companyName = (String) experienceData.get("companyName");
            String startDate = (String) experienceData.get("startDate");

            if (jobTitle == null || jobTitle.trim().isEmpty() ||
                    companyName == null || companyName.trim().isEmpty() ||
                    startDate == null || startDate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Job title, company name, and start date are required"));
            } // Create or update experience entity
            String experienceId = (String) experienceData.get("id");
            Experience experienceEntity;

            if (experienceId != null && !experienceId.trim().isEmpty()) {
                // Update existing experience
                experienceEntity = experienceRepository.findById(experienceId)
                        .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));

                // Verify ownership
                if (!experienceEntity.getUserId().equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "You don't have permission to update this experience"));
                }
            } else {
                // Create new experience
                experienceEntity = new Experience();
                experienceEntity.setUserId(user.getId());
            }

            experienceEntity.setJobTitle(jobTitle);
            experienceEntity.setCompanyName(companyName);
            experienceEntity.setCompanyUrl((String) experienceData.get("companyUrl"));
            experienceEntity.setStartDate(LocalDate.parse(startDate));

            String endDate = (String) experienceData.get("endDate");
            if (endDate != null && !endDate.trim().isEmpty()) {
                experienceEntity.setEndDate(LocalDate.parse(endDate));
            }

            experienceEntity.setCertificateLink((String) experienceData.get("certificateLink"));

            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) experienceData.get("skills");
            experienceEntity.setSkills(skills);

            experienceEntity.setDescription((String) experienceData.get("description"));
            experienceEntity.setCompanyLocation((String) experienceData.get("companyLocation")); // Handle certificate
                                                                                                 // file upload if
                                                                                                 // provided
            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null ||
                        (!contentType.equals("application/pdf") &&
                                !contentType.equals("image/jpeg") &&
                                !contentType.equals("image/jpg") &&
                                !contentType.equals("image/png"))) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("message", "Invalid file type. Only PDF, JPG, JPEG, and PNG are allowed."));
                }

                try {
                    // Upload file to Cloudinary in the experience_certificates directory
                    String fileUrl = fileStorageService.uploadFile(file, "experience_certificates");

                    // Create Documents object with file metadata
                    Documents fileDoc = Documents.builder()
                            .fileName(file.getOriginalFilename())
                            .fileType(file.getContentType())
                            .fileUrl(fileUrl)
                            .uploadDate(LocalDate.now())
                            .build();

                    experienceEntity.setFile(fileDoc);
                } catch (Exception e) {
                    return ResponseEntity.internalServerError()
                            .body(Map.of("message", "Failed to upload certificate file: " + e.getMessage()));
                }
            }

            // Save experience
            Experience savedExperience = experienceRepository.save(experienceEntity);

            return ResponseEntity.ok(Map.of(
                    "message", "Experience saved successfully",
                    "experience", savedExperience));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid experience data format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to save experience: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllExperiences(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            List<Experience> experiences = experienceRepository.findByUserId(user.getId());
            return ResponseEntity.ok(experiences);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to fetch experiences: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> deleteExperience(String id, HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            Experience experience = experienceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));

            // Check if the user owns this experience
            if (!experience.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You don't have permission to delete this experience"));
            }

            // Delete certificate file if exists
            if (experience.getFile() != null) {
                fileStorageService.deleteFromCloudinary(experience.getFile().getFileUrl());
            }

            experienceRepository.delete(experience);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Experience deleted successfully"));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to delete experience: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllProjects(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            List<Project> projects = projectRepository.findByUserId(user.getId());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to fetch projects: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> saveOrUpdateProject(
            HttpServletRequest request,
            List<MultipartFile> screenshots,
            List<String> deletedScreenshots,
            String projectJson) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            // Parse project data from JSON string
            ProjectDTO projectDTO = objectMapper.readValue(projectJson, ProjectDTO.class); // Validate required fields
            if (projectDTO.getTitle() == null || projectDTO.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Project title is required"));
            }

            // Create or update project entity
            Project projectEntity;
            if (projectDTO.getId() != null && !projectDTO.getId().trim().isEmpty()) {
                // Update existing project
                projectEntity = projectRepository.findById(projectDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

                // Verify ownership
                if (!projectEntity.getUserId().equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "You don't have permission to update this project"));
                }
            } else {
                // Create new project
                projectEntity = new Project();
                projectEntity.setUserId(user.getId());
                projectEntity.setScreenshots(new ArrayList<>());
            }

            // Map DTO fields to entity
            projectEntity.setTitle(projectDTO.getTitle());
            projectEntity.setGithubUrl(projectDTO.getGithubUrl());
            projectEntity.setLiveUrl(projectDTO.getLiveUrl());

            projectEntity.setTechnologies(projectDTO.getTechnologies());
            projectEntity.setFeatures(projectDTO.getFeatures());
            projectEntity.setDescription(projectDTO.getDescription());

            // Handle deleted screenshots
            if (deletedScreenshots != null && !deletedScreenshots.isEmpty()) {
                for (String url : deletedScreenshots) {
                    // Remove from entity and delete from storage
                    projectEntity.getScreenshots().removeIf(doc -> doc.getFileUrl().equals(url));
                    fileStorageService.deleteFromCloudinary(url);
                }
            }

            // Handle new screenshots
            if (screenshots != null && !screenshots.isEmpty()) {
                for (MultipartFile screenshot : screenshots) {
                    String contentType = screenshot.getContentType();
                    if (contentType == null ||
                            (!contentType.equals("image/jpeg") &&
                                    !contentType.equals("image/jpg") &&
                                    !contentType.equals("image/png"))) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Invalid file type. Only JPG, JPEG, and PNG are allowed."));
                    }

                    try {
                        // Upload new screenshot to Cloudinary
                        String fileUrl = fileStorageService.uploadFile(screenshot, "project_screenshots");

                        // Create Documents object with file metadata
                        Documents screenshotDoc = Documents.builder()
                                .fileName(screenshot.getOriginalFilename())
                                .fileType(screenshot.getContentType())
                                .fileUrl(fileUrl)
                                .uploadDate(LocalDate.now())
                                .build();

                        projectEntity.getScreenshots().add(screenshotDoc);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                .body(Map.of("message", "Failed to upload screenshot: " + e.getMessage()));
                    }
                }
            }

            // Save project
            Project savedProject = projectRepository.save(projectEntity);

            return ResponseEntity.ok(Map.of(
                    "message", "Project saved successfully",
                    "project", savedProject));

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid project data format: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to save project: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> deleteProject(String id, HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized User"));
        }

        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

            // Check if the user owns this project
            if (!project.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You don't have permission to delete this project"));
            } // Delete all screenshots if they exist
            if (project.getScreenshots() != null && !project.getScreenshots().isEmpty()) {
                for (Documents screenshot : project.getScreenshots()) {
                    fileStorageService.deleteFromCloudinary(screenshot.getFileUrl());
                }
            }

            projectRepository.delete(project);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Project deleted successfully"));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to delete project: " + e.getMessage()));
        }
    }
}
