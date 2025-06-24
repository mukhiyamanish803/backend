package com.manish.employara.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Country name is required.")
    private String countryName;

    @NotBlank(message = "Country code is required.")
    private String countryCode;

    @NotBlank(message = "Dial Code is required.")
    @Pattern(regexp = "^\\+\\d{1,4}$", message = "Dial Code must start with + followed by 1 to 4 digits.")
    private String dialCode;
}
