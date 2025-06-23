package com.manish.employara.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.manish.employara.models.jobseeker.Certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private String title;
    private String organization;
    private String organizationLink;
    private String certificateLink;
    private String description;
    private List<String> skillsAcquired;
    private LocalDate dateIssued;
    private LocalDate dateExpire;
    private String address;
    private String documents;

    public static CertificateResponseDTO createResponse(Certificate certificate){
        return CertificateResponseDTO.builder()
        .title(certificate.getTitle())
        .organization(certificate.getOrganization())
        .organizationLink(certificate.getOrganizationLink())
        .certificateLink(certificate.getCertificateLink())
        .description(certificate.getDescription())
        .skillsAcquired(certificate.getSkillsAcquired())
        .dateIssued(certificate.getDateIssued())
        .dateExpire(certificate.getDateExpire())
        .address(certificate.getAddress().getStreet() + ", " + certificate.getAddress().getCity() + ", " + certificate.getAddress().getState() + ", " + certificate.getAddress().getCountry() + ", " + certificate.getAddress().getZipCode())
        .documents(certificate.getDocuments().getFileUrl())
        .build();
    }
}
