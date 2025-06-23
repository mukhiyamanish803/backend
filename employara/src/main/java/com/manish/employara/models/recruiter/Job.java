package com.manish.employara.models.recruiter;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.dto.BasicJobDetailsDTO;
import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs")
public class Job {
    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String companyId;

    private String companyName;

    private String companyAddress;

    private String companyLogoUrl;

    private String aboutCompany;

    private String website;

    @Indexed
    private String jobTitle;

    @Indexed
    private String jobType;

    @Indexed
    private String category;

    private String experienceLevel;

    private String salaryRange;

    private String location;

    private String jobDescription;

    private String responsibilities;

    private String requirements;

    private String niceToHave;

    private String educationRequirement;

    private String requiredSkills;

    private String benefitsAndPerks;

    private LocalDate deadline;

    @Indexed
    private Status status;

    @Builder.Default
    private int appliedCount = 0;

    private LocalDate createdAt;
    
    private LocalDate updatedAt;

    private LocalDate closedAt;


    public static BasicJobDetailsDTO getBasicDetails(Job job){
        return BasicJobDetailsDTO.builder()
        .id(job.getId())
        .appliedCount(job.getAppliedCount())
        .companyName(job.getCompanyName())
        .companyLogoUrl(job.getCompanyLogoUrl())
        .deadline(job.getDeadline())
        .closedAt(job.getClosedAt())
        .status(job.getStatus())
        .jobTitle(job.getJobTitle())
        .build();
    }

}
