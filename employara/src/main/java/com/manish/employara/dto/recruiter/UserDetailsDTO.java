package com.manish.employara.dto.recruiter;

import java.util.List;

import com.manish.employara.models.Address;
import com.manish.employara.models.DialCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String title;
    private String bio;
    private DialCode dialCode;
    private String phoneNumber;
    private List<String> socialLinks;
    private Address address;
}
