package com.carshop.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Requires Oracle from docker compose on localhost:1521 (APP_USER schema + Flyway seeds).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () -> envOrDefault("IT_DATASOURCE_URL", "jdbc:oracle:thin:@localhost:1521/XEPDB1"));
    registry.add(
        "spring.datasource.username",
        () -> envOrDefault("ORACLE_APP_USER", "carshop"));
    registry.add(
        "spring.datasource.password",
        () -> envOrDefault("ORACLE_APP_PASSWORD", "LocalDevApp1"));
    registry.add(
        "app.jwt.secret",
        () -> envOrDefault("JWT_SECRET", "test-jwt-secret-at-least-32-characters!!"));
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void registerLoginAndEnforceStaffAccess() throws Exception {
    String email = "customer." + UUID.randomUUID() + "@example.com";
    String password = "Customer1!";

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.role").value("CUSTOMER"));

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andReturn();

    String customerToken = readToken(loginResult);

    mockMvc.perform(get("/api/staff/ping"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/api/staff/ping")
            .header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isForbidden());

    MvcResult staffLogin = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"staff@carshop.local","password":"StaffDemo1!"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("STAFF"))
        .andReturn();

    String staffToken = readToken(staffLogin);

    mockMvc.perform(get("/api/staff/ping")
            .header("Authorization", "Bearer " + staffToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.scope").value("staff"));
  }

  @Test
  void loginRejectsBadPassword() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"staff@carshop.local","password":"wrong-password"}
                """))
        .andExpect(status().isUnauthorized());
  }

  private String readToken(MvcResult result) throws Exception {
    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return body.get("accessToken").asText();
  }

  private static String envOrDefault(String key, String defaultValue) {
    String value = System.getenv(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
