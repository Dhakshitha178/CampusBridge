package com.campusbridge.placement.security;

import com.campusbridge.placement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Default 256-bit key for HMAC-SHA256
    private final String secretString = "CampusBridgeSecureSecretKey2026NationalPlacementDriveVirtualCoordinationPlatform";
    private final long expirationMs = 86400000L; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user, Long profileId, Long entityRefId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("fullName", user.getFullName());
        claims.put("profileId", profileId);
        claims.put("entityRefId", entityRefId);
        if (user.getCollege() != null) {
            claims.put("collegeId", user.getCollege().getId());
            claims.put("collegeName", user.getCollege().getName());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Object val = extractAllClaims(token).get("userId");
        return val != null ? Long.valueOf(val.toString()) : null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String extractedUser = extractUsername(token);
        return (extractedUser.equals(username) && !isTokenExpired(token));
    }
}
