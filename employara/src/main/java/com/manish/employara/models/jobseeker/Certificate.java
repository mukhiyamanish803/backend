package com.manish.employara.models.jobseeker;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.models.Address;
import com.manish.employara.models.Documents;
import com.manish.employara.models.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "certificate")
public class Certificate {
    @Id
    private String id;

    @NotNull(message = "Title is required.")
    private String title;

    @NotNull(message = "Issuing Organization is required.")
    private String organization;

    private String organizationLink;

    private String certificateLink;

    private String description;

    private List<String> skillsAcquired;

    @Past
    private LocalDate dateIssued;

    private LocalDate dateExpire;

    private Address address; // Address of organization.

    @DBRef
    private User user;

    private Documents documents;

}
