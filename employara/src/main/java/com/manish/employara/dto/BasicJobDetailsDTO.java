package com.manish.employara.dto;

import java.time.LocalDate;

import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicJobDetailsDTO {
    private String id;
    private int appliedCount;
    private String companyName;
    private String companyLogoUrl;
    private LocalDate deadline;
    private LocalDate closedAt;
    private Status status;
    private String jobTitle;
}
