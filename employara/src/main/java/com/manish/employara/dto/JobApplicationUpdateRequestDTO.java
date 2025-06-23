package com.manish.employara.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.manish.employara.models.Status;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationUpdateRequestDTO {
    

    @NotNull(message = "Applicant id is missing.")
    private String appplicantId;

    @NotNull(message = "Job id is missing.")
    private String jobId;

    @NotNull(message = "Status is missing.")
    private Status status;
    
    private LocalDate date;
    private LocalTime time;
}
