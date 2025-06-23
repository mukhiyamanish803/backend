package com.manish.employara.models.recruiter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manish.employara.models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "candidates")
public class Candidate {
    @Id
    private String userId;

    private String appliedFor;
    
    private String email;

    private String firstName;

    private String lastName;

    private String experience;

    private Status status;
    
}
