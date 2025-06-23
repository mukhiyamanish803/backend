package com.manish.employara.dto;

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
public class JobApplicationsResponseDTO {
    private String jobId;
    private Status status;
    private LocalDate createdAt;

    public JobApplicationsResponseDTO createResponse(String id){
        return JobApplicationsResponseDTO. builder()
        .jobId(id)
        .status(Status.ACTIVE)
        .createdAt(LocalDate.now())
        .build();
    }
}
