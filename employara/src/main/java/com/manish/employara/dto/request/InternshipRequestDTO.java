package com.manish.employara.dto.request;

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
public class InternshipRequestDTO {
    @NotNull(message = "Titile required.")
    private String title;

    @NotNull(message = "Category required.")
    private String category;

    @NotNull(message = "Description required.")
    private String description;

    private String responsibilities;
    private String requirements;

    @NotNull(message = "Duration required.")
    private String duration;

    private String stipend;

    @NotNull(message = "Mode required.")
    private String mode;
    
    private String location;

    private String benefits;

    @NotNull(message = "Deadline required.")
    private LocalDate deadline;
}
