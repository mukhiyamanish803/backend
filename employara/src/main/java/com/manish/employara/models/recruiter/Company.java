package com.manish.employara.models.recruiter;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.manish.employara.models.DialCode;
import com.manish.employara.models.Documents;
import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    private String id;

    private String companyName;
    private String industryType;
    private String businessEmail;
    private String description;
    private String website;
    private String phoneNumber;

    private DialCode dialCode;

    private String address;

    private Map<String, String> socialMedia;

    private Documents logo;

    @Indexed(unique = true)
    private String userId;

    private int hired;

    private long activeCandidate;

    private long interviewed;

    @Builder.Default
    private Status status = Status.PENDING;
}
