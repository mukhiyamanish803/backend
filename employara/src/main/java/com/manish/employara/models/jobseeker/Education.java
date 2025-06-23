package com.manish.employara.models.jobseeker;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.models.Address;
import com.manish.employara.models.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "education")
public class Education {
    @Id
    private String id;

    private String instituteName;
    private String fieldOfStudy;
    private String degree;
    private String specialization;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String gradeType;
    private String gradeValue;
    private Address address;
    private List<String> skills;
    private List<Documents> documents;

    @Indexed
    private String userId;
}
