package com.manish.employara.models.jobseeker;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.models.Address;
import com.manish.employara.models.DialCode;
import com.manish.employara.models.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profile")
public class Profile {
    @Id
    private String id;

    private Documents profileImage;

    private String title;

    private String bio;

    private DialCode dialCode;

    private String phoneNumber;

    private Address address;

    private List<String> socialLinks;

    @Indexed(unique = true)
    private String userId;
    
}
