package com.carshop.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.carshop.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    JwtProperties properties = new JwtProperties();
    properties.setSecret("test-jwt-secret-at-least-32-characters!!");
    properties.setIssuer("car-shop-test");
    properties.setExpirationMinutes(60);
    jwtService = new JwtService(properties);
  }

  @Test
  void createAndParseRoundTrip() {
    String token = jwtService.createToken(42L, "user@example.com", Role.CUSTOMER);

    JwtService.AuthenticatedUser user = jwtService.parseValidToken(token);

    assertThat(user.userId()).isEqualTo(42L);
    assertThat(user.email()).isEqualTo("user@example.com");
    assertThat(user.role()).isEqualTo(Role.CUSTOMER);
  }

  @Test
  void rejectsTamperedToken() {
    String token = jwtService.createToken(1L, "a@b.c", Role.STAFF);
    String tampered = token.substring(0, token.length() - 4) + "xxxx";

    assertThatThrownBy(() -> jwtService.parseValidToken(tampered))
        .isInstanceOf(InvalidTokenException.class);
  }
}
