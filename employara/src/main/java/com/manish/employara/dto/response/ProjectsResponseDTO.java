package com.manish.employara.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.manish.employara.models.Documents;
import com.manish.employara.models.jobseeker.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsResponseDTO {
    private String title;
    private String githubUrl;
    private String liveUrl;
    private List<String> screenshots;

    private List<String> technologies;
    private List<String> features;
    private String description;

    public static ProjectsResponseDTO createResponse(Project project){
        List<String> url = new ArrayList<>();
        for(Documents doc : project.getScreenshots()){
            url.add(doc.getFileUrl());
        }
        return ProjectsResponseDTO.builder()
        .title(project.getTitle())
        .githubUrl(project.getGithubUrl())
        .liveUrl(project.getLiveUrl())
        .screenshots(url)
        .features(project.getFeatures())
        .technologies(project.getTechnologies())
        .description(project.getDescription())
        .build();
    }
}
