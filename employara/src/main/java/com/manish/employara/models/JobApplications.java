package com.manish.employara.models;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "jobApplications")
@CompoundIndexes({
    @CompoundIndex(name = "applicant_job_unique", def = "{'applicantId': 1, 'jobId': 1}", unique = true)
})
public class JobApplications {
    @Id
    private String id;

    @Indexed
    private String applicantId;

    private String applicantFirstName;

    private String applicantLastName;

    private String applicantEmail;

    @Indexed
    private String jobId;

    private String jobTitle;

    private String interest;

    private String coverLetter;

    private String companyId;

    private String companyName;

    private Status status;

    private LocalDate createdAt;

    private LocalDate updatedAt;

}
