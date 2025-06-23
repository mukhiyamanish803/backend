package com.manish.employara.dto.recruiter;

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
public class CandidateDTO {
    @NotNull(message = "User id is missing.")
    private String userId;

    private String appliedFor;
    
    private String email;

    private String firstName;

    private String lastName;

    private String experience;

    private Status status;
}
