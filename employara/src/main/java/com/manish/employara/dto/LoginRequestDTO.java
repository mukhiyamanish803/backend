package com.manish.employara.dto;

import com.manish.employara.models.Role;

import jakarta.validation.constraints.Email;
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
public class LoginRequestDTO {

    @NotNull(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    private String email;

    @NotNull(message = "Password is required.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!_]).{8,}$",
        message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit."
    )
    private String password;
    private Role role;
}
