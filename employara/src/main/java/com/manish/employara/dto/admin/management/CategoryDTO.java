package com.manish.employara.dto.admin.management;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotNull(message = "Category cannot be empty.")
    @Pattern(
        regexp = "^[A-Za-z/ ]+$",
        message = "Category can contain only letters and spaces."
    )
    private String category;
}
