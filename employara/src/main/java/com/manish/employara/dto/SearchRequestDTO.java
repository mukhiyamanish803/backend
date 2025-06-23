package com.manish.employara.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDTO {
    private String category;
    private String location;
    private String jobType;
    private String experienceLevel;
    private String keyword;
}
