package com.manish.employara.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {
    private String id;
    @NotNull(message = "Institute name cannot be blank.")
    private String instituteName;
    private String fieldOfStudy;
    private String degree;
    private String specialization;

    @Past(message = "Start date must be a past date.")
    @NotNull(message = "Start Date is required.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String description;
    private String gradeType;
    private String gradeValue;
    private AddressDTO address;
    private List<String> skills;
    private List<DocumentsDTO> documents;
}
