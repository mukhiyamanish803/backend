// backend\employara\src\main\java\com\manish\employara\controller\AuthController.java
package com.manish.employara.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.employara.dto.AuthResponseDTO;
import com.manish.employara.dto.LoginRequestDTO;
import com.manish.employara.dto.RegisterRequestDTO;
import com.manish.employara.models.User;
import com.manish.employara.models.jobseeker.Profile;
import com.manish.employara.repository.UserRepository;
import com.manish.employara.repository.jobseeker.ProfileRepository;
import com.manish.employara.service.AuthService;
import com.manish.employara.utils.JwtUtil;
import com.manish.employara.utils.VerifyUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final VerifyUser verifyUser;

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        System.out.println(request);
        AuthResponseDTO response = authService.register(request);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No token found"));
        }

        try {
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            String email = jwtUtil.getEmailFromJwtToken(token);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
            String url = null;
            if (profile != null && profile.getProfileImage() != null) {
                url = profile.getProfileImage().getFileUrl();
            }

            return ResponseEntity.ok(AuthResponseDTO.createResponse(user, url, "User Details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token validation failed", "message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Create expired cookies by setting maxAge to 0
        ResponseCookie accessExpiredCookie = jwtUtil.generateExpiredCookie("accessToken");
        ResponseCookie refreshExpiredCookie = jwtUtil.generateExpiredCookie("refreshToken");

        response.addHeader(HttpHeaders.SET_COOKIE, accessExpiredCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshExpiredCookie.toString());

        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }

    // this is only for Web Socket Connection.

    @PostMapping("/token-for-websocket")
    public ResponseEntity<?> getWebSocketToken(HttpServletRequest request) {
        User user = verifyUser.verify(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please Login."));
        }

        String accessToken = jwtUtil.generateCookie(user, "accessToken", 5 * 60).getValue();

        return ResponseEntity.ok(accessToken);
    }
}
