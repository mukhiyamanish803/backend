package com.manish.employara.dto.response;

import java.util.List;

import com.manish.employara.models.jobseeker.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private String profileImageUrl;
    private String title;
    private String bio;
    private String dialCode;
    private String phoneNumber;
    private String address;
    private List<String> socialLinks;

    public static ProfileResponseDTO createResponse(Profile profile){
        return ProfileResponseDTO.builder()
        .profileImageUrl(profile.getProfileImage().getFileUrl())
        .title(profile.getTitle())
        .bio(profile.getBio())
        .dialCode(profile.getDialCode().getDialCode())
        .phoneNumber(profile.getPhoneNumber())
        .address(profile.getAddress().getStreet() + ", " + profile.getAddress().getCity() + ", " + profile.getAddress().getState()+ ", " + profile.getAddress().getCountry() + ", " + profile.getAddress().getZipCode())
        .socialLinks(profile.getSocialLinks())
        .build();
    }
}
