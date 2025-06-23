package com.manish.employara.dto.jobseeker;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceRequestDTO {

    private String userId;

    private String jobTitle;

    private String companyName;

    private String companyUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String certificateLink;

    private List<String> skills;

    private String description;

    private String companyLocation;
}
