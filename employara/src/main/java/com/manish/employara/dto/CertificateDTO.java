package com.manish.employara.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {

    @NotNull(message = "Title is required.")
    private String title;

    @NotNull(message = "Issuing Organization is required.")
    private String organization;

    private String organizationLink;

    private String certificateLink;

    private String description;
    
    private List<String> skillsAcquired;

    @PastOrPresent
    private LocalDate dateIssued;

    @Future
    private LocalDate dateExpire;

    private AddressDTO address;

    private DocumentsDTO documents;
}
