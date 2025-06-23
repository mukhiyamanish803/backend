package com.manish.employara.dto.response;

import com.manish.employara.models.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String email;

    public static UserResponseDTO createResponse(User user){
        return UserResponseDTO.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .build();
    }
}
