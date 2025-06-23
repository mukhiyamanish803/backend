package com.manish.employara.dto;

import java.util.List;

import com.manish.employara.models.Documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private String id;
    private String title;
    private String githubUrl;
    private String liveUrl;
    private List<Documents> screenshots;

    private List<String> technologies;
    private List<String> features;
    private String description;
}
