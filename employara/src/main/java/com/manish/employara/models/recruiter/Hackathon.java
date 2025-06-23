package com.manish.employara.models.recruiter;

import org.springframework.data.annotation.Id;

import com.manish.employara.models.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon {
    @Id
    private String id;
    private String recruiterId;
    private Documents bannerImage;
    private String details;
    private int registered;
}
