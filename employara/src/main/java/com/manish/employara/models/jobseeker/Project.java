package com.manish.employara.models.jobseeker;

import java.util.List;

import org.springframework.data.annotation.Id;
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
@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    private String userId;
    private String title;
    private String githubUrl;
    private String liveUrl;
    private List<Documents> screenshots;

    private List<String> technologies;
    private List<String> features;
    private String description;
}
