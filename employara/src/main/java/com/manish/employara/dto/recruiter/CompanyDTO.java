package com.manish.employara.dto.recruiter;

import java.util.Map;

import com.manish.employara.dto.DialCodeDTO;
import com.manish.employara.dto.DocumentsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    
    private String companyName;
    private String industryType;
    private String businessEmail;
    private String description;
    private String website;
    private String phoneNumber;

    private DialCodeDTO dialCode;

    private String address;

    private Map<String, String> socialMedia;

    private DocumentsDTO logo;
}
