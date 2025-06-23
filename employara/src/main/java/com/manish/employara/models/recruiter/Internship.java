package com.manish.employara.models.recruiter;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Internship {
    @Id
    private String id;
    private String recruiterId;
    private String title;
    private String companyName;
    private String companyLogoUrl;
    private String companyWebsite;
    private String companyDescription;
    private String companyAddress;

    @Indexed
    private String category;
    private String description;
    private String responsibilities;
    private String requirements;

    @Indexed
    private String duration;
    private String stipend;

    @Indexed
    private String mode;

    @Indexed
    private String location;
    private String benefits;

    @Indexed
    private LocalDate deadline;

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
    private LocalDate updatedAt;

    @Builder.Default
    private Status status = Status.ACTIVE;
}
