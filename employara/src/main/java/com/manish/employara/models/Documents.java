package com.manish.employara.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Documents {
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDate uploadDate;
}
