package com.manish.employara.models.jobseeker;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.models.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "experiences")
public class Experience {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String jobTitle;

    private String companyName;

    private String companyUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String certificateLink;
    private Documents file;

    private List<String> skills;

    private String description;

    private String companyLocation;
}
