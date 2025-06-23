package com.manish.employara.utils;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.manish.employara.models.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${app.security.jwt.secret}")
    private String secretKey;

    @Value("${app.security.jwt.expiration}")
    private int jwtExpiration;

    @Value("${app.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getToken(User user, int expirySeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirySeconds * 1000L);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(now)
                .expiration(expiry)
                .issuer(issuer)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractUserId(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("id", String.class); // Assuming ID is stored as String
        } catch (Exception e) {
            System.err.println("Failed to extract userId: " + e.getMessage());
            return null;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            // Additional validation
            if (claims.getPayload().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public ResponseCookie generateCookie(User user, String name, int expirySeconds) {
        String token = getToken(user, expirySeconds);
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(expirySeconds))
                .sameSite("strict")
                .build();
        return cookie;
    }

    public ResponseCookie generateExpiredCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("strict")
                .path("/")
                .maxAge(0) // instantly expire
                .build();
    }
}
