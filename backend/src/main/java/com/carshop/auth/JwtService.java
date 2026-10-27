package com.carshop.auth;

import com.carshop.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_EMAIL = "email";

  private final JwtProperties properties;
  private final SecretKey secretKey;

  public JwtService(JwtProperties properties) {
    this.properties = properties;
    byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalStateException("JWT_SECRET must be at least 32 bytes");
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createToken(Long userId, String email, Role role) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(properties.getExpirationMinutes() * 60);
    return Jwts.builder()
        .issuer(properties.getIssuer())
        .subject(String.valueOf(userId))
        .claim(CLAIM_EMAIL, email)
        .claim(CLAIM_ROLE, role.name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(secretKey)
        .compact();
  }

  public long expirationSeconds() {
    return properties.getExpirationMinutes() * 60;
  }

  public AuthenticatedUser parseValidToken(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(secretKey)
          .requireIssuer(properties.getIssuer())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      Long userId = Long.valueOf(claims.getSubject());
      String email = claims.get(CLAIM_EMAIL, String.class);
      Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
      return new AuthenticatedUser(userId, email, role);
    } catch (JwtException | IllegalArgumentException ex) {
      throw new InvalidTokenException("Invalid or expired token", ex);
    }
  }

  public record AuthenticatedUser(Long userId, String email, Role role) {
  }
}
