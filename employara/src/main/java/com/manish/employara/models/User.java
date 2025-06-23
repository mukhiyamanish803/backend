package com.manish.employara.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private String password;
    private Role role;
    private Status status;

    private String experience;

    @Builder.Default
    private String createdAt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
}
