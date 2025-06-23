package com.manish.employara.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationsRequestDTO {

    @NotNull(message = "Job Id is required.")
    private String jobId;

    private String jobTitle;

    private String interest;

    private String coverLetter;

    private String companyId;
}
