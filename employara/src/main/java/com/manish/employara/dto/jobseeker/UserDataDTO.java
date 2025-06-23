package com.manish.employara.dto.jobseeker;

import java.util.Map;

import com.manish.employara.dto.AddressDTO;
import com.manish.employara.dto.DialCodeDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDTO {

    private String firstName;
    private String lastName;

    private String title;
    private String profileImage;

    private String bio;

    private Map<String, String> socialLinks;

    @Valid
    private AddressDTO address;

    @Valid
    private DialCodeDTO dialCode;

    @Size(min = 7, max = 10)
    private String phoneNumber;
}
