package com.manish.employara.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsDTO {
    @NotNull(message = "File url is missing.")
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDate uploadDate;
}
