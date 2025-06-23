package com.manish.employara.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.manish.employara.dto.AuthResponseDTO;
import com.manish.employara.dto.LoginRequestDTO;
import com.manish.employara.dto.RegisterRequestDTO;
import com.manish.employara.exception.DuplicateUserException;
import com.manish.employara.models.User;
import com.manish.employara.repository.UserRepository;
import com.manish.employara.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("User with email " + request.getEmail() + " already exists.");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        return AuthResponseDTO.createResponse(user, null, "User Created Successfully");
    }

    public ResponseEntity<?> login(LoginRequestDTO request, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            ResponseCookie accessCookie = jwtUtil.generateCookie(user, ACCESS_TOKEN_COOKIE, 15 * 60);

            ResponseCookie refreshCookie = jwtUtil.generateCookie(user, REFRESH_TOKEN_COOKIE, 24 * 60 * 60);

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok().body("logged in Successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No refresh token found"));
            }

            String refreshToken = null;
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token not found"));
            }

            if (!jwtUtil.validateJwtToken(refreshToken)) {
                // Clear invalid cookies
                ResponseCookie accessExpiredCookie = jwtUtil.generateExpiredCookie(ACCESS_TOKEN_COOKIE);
                ResponseCookie refreshExpiredCookie = jwtUtil.generateExpiredCookie(REFRESH_TOKEN_COOKIE);
                response.addHeader(HttpHeaders.SET_COOKIE, accessExpiredCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshExpiredCookie.toString());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }

            String email = jwtUtil.getEmailFromJwtToken(refreshToken);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Generate new access token
            ResponseCookie newAccessCookie = jwtUtil.generateCookie(user, ACCESS_TOKEN_COOKIE, 15 * 60);
            // Generate new refresh token
            ResponseCookie newRefreshCookie = jwtUtil.generateCookie(user, REFRESH_TOKEN_COOKIE, 24 * 60 * 60);

            response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());


            AuthResponseDTO userResponse = AuthResponseDTO.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .build();

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Tokens refreshed successfully",
                            "user", userResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing refresh token",
                            "message", e.getMessage()));
        }
    }
}
