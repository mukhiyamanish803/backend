package com.manish.employara.dto.jobseeker;

import java.util.List;

import com.manish.employara.dto.AddressDTO;
import com.manish.employara.dto.DialCodeDTO;
import com.manish.employara.dto.DocumentsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    
    private DocumentsDTO profileImage;

    private String title;

    private String bio;

    private DialCodeDTO dialCode;

    private String phoneNumber;

    private AddressDTO address;

    private List<String> socialLinks;
}
