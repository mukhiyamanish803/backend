package com.manish.employara.models.jobseeker;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "savedjobs")
public class SavedJob {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private Set<String> jobId;
}
