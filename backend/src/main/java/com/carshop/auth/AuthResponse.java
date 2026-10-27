package com.carshop.auth;

import com.carshop.user.Role;

public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds,
    String email,
    Role role
) {
}
