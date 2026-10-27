package com.carshop.auth;

import com.carshop.user.Role;
import com.carshop.user.UserAccount;
import com.carshop.user.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserAccountRepository userAccountRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email = normalizeEmail(request.email());
    if (userAccountRepository.findByEmailIgnoreCase(email).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    }

    UserAccount created = userAccountRepository.save(
        UserAccount.createCustomer(email, passwordEncoder.encode(request.password())));
    return toAuthResponse(created);
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    String email = normalizeEmail(request.email());
    UserAccount user = userAccountRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> unauthorized());

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw unauthorized();
    }
    return toAuthResponse(user);
  }

  private AuthResponse toAuthResponse(UserAccount user) {
    String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
    return new AuthResponse(
        token,
        "Bearer",
        jwtService.expirationSeconds(),
        user.getEmail(),
        user.getRole());
  }

  private static String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }

  private static ResponseStatusException unauthorized() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
  }
}
