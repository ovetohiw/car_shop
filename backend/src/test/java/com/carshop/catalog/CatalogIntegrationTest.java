package com.carshop.catalog;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Requires Oracle from docker compose on localhost:1521 with Flyway seeds.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogIntegrationTest {

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
  void publicCatalogReturnsSeededCarsAndParts() throws Exception {
    MvcResult carsResult = mockMvc.perform(get("/api/cars"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
        .andExpect(jsonPath("$[0].imageUrl").isNotEmpty())
        .andReturn();

    long carId = objectMapper.readTree(carsResult.getResponse().getContentAsString()).get(0).get("id").asLong();

    mockMvc.perform(get("/api/cars/{id}", carId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.trims", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$.imageUrl").isNotEmpty());

    MvcResult partsResult = mockMvc.perform(get("/api/parts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
        .andExpect(jsonPath("$[0].imageUrl").isNotEmpty())
        .andReturn();

    JsonNode firstPart = objectMapper.readTree(partsResult.getResponse().getContentAsString()).get(0);
    mockMvc.perform(get("/api/parts/{id}", firstPart.get("id").asLong()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sku").value(firstPart.get("sku").asText()));
  }

  @Test
  void missingCarReturnsNotFound() throws Exception {
    mockMvc.perform(get("/api/cars/{id}", 999999L))
        .andExpect(status().isNotFound());
  }

  private static String envOrDefault(String key, String defaultValue) {
    String value = System.getenv(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
