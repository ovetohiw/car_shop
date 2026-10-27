package com.carshop.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "users")
public class UserAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private Role role;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  protected UserAccount() {
  }

  public static UserAccount createCustomer(String email, String passwordHash) {
    UserAccount user = new UserAccount();
    user.email = email;
    user.passwordHash = passwordHash;
    user.role = Role.CUSTOMER;
    user.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    return user;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
