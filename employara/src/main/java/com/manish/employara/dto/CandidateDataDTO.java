package com.manish.employara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDataDTO {
    private String firstName;
    private String LastName;
    private String email;
}
