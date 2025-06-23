package com.manish.employara.dto;

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
public class DialCodeDTO {
    @NotNull(message = "Country name is required.")
    private String countryName;

    private String countryCode;

    @NotNull(message = "Dial Code is required.")
    @Pattern(regexp = "^\\+\\d{1,4}$", message = "Dial Code must start with + followed by 1 to 4 digits.")
    private String dialCode;
}
