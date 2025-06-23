package com.manish.employara;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.manish.employara.models.Role;
import com.manish.employara.models.User;
import com.manish.employara.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataLoader {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @PostConstruct
    public void loadUser() {
        if (!userRepository.existsByEmail("test@example.com")) {
            User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.JOBSEEKER)
                .build();
            userRepository.save(user);
        }
    }
}
