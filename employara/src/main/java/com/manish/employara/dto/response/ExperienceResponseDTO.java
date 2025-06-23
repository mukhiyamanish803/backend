package com.manish.employara.dto.response;

import java.time.LocalDate;
import com.manish.employara.models.jobseeker.Experience;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceResponseDTO {
    private String jobTitle;
    private String companyName;
    private String companyUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String certificateLink;
    private String file;
    private String description;
    private String companyLocation;

    public static ExperienceResponseDTO createResponse(Experience experience){
        return ExperienceResponseDTO.builder()
        .jobTitle(experience.getJobTitle())
        .companyName(experience.getCompanyName())
        .companyUrl(experience.getCompanyUrl())
        .startDate(experience.getStartDate())
        .endDate(experience.getEndDate())
        .certificateLink(experience.getCertificateLink())
        .file(experience.getFile().getFileUrl())
        .description(experience.getDescription())
        .companyLocation(experience.getCompanyLocation())
        .build();
    }
}
