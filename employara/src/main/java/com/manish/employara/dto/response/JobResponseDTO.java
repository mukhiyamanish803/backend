package com.manish.employara.dto.response;

import java.time.LocalDate;
import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponseDTO {

    private String id;

    private String companyId;

    private String companyName;

    private String companyAddress;

    private String companyLogoUrl;

    private String aboutCompany;

    private String website;

    private String jobTitle;

    private String jobType;

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

    private Status status;

    private int appliedCount;

}
