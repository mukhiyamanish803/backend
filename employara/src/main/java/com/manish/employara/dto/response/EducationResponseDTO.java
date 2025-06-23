package com.manish.employara.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.manish.employara.models.Documents;
import com.manish.employara.models.jobseeker.Education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationResponseDTO {
    private String instituteName;
    private String fieldOfStudy;
    private String degree;
    private String specialization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String gradeType;
    private String gradeValue;
    private String address;
    private List<String> documents;

    public static EducationResponseDTO createResponse(Education education){
        List<String> doc = new ArrayList<>();

        for(Documents docu : education.getDocuments()){
            doc.add(docu.getFileUrl());
        }

        return EducationResponseDTO.builder()
        .instituteName(education.getInstituteName())
        .fieldOfStudy(education.getFieldOfStudy())
        .degree(education.getDegree())
        .specialization(education.getSpecialization())
        .startDate(education.getStartDate())
        .endDate(education.getEndDate())
        .description(education.getDescription())
        .gradeType(education.getGradeType())
        .gradeValue(education.getGradeValue())
        .address(education.getAddress().getStreet() + ", " + education.getAddress().getCity() + ", " + education.getAddress().getState() + ", " + education.getAddress().getCountry() + ", " + education.getAddress().getZipCode())
        .documents(doc)
        .build();
    }
}
