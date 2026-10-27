package com.carshop.order;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

@SpringBootTest
@AutoConfigureMockMvc
class StaffCabinetIntegrationTest {

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () -> envOrDefault("IT_DATASOURCE_URL", "jdbc:oracle:thin:@localhost:1521/XEPDB1"));
    registry.add("spring.datasource.username", () -> envOrDefault("ORACLE_APP_USER", "carshop"));
    registry.add("spring.datasource.password", () -> envOrDefault("ORACLE_APP_PASSWORD", "LocalDevApp1"));
    registry.add(
        "app.jwt.secret",
        () -> envOrDefault("JWT_SECRET", "test-jwt-secret-at-least-32-characters!!"));
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void customerSeesOwnOrdersAndStaffAdvancesStatuses() throws Exception {
    String email = "cabinet." + UUID.randomUUID() + "@example.com";
    String password = "Customer1!";
    String customerToken = authToken(email, password, true);
    String staffToken = loginToken("staff@carshop.local", "StaffDemo1!");

    long orderId = createMinimalOrder(customerToken);

    mockMvc.perform(get("/api/orders/mine").header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == %d)]", orderId).exists());

    mockMvc.perform(get("/api/staff/orders").header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isForbidden());

    mockMvc.perform(get("/api/staff/orders").header("Authorization", "Bearer " + staffToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == %d)].status", orderId).value(hasItem("NEW")));

    patchStatus(staffToken, orderId, "SUBMITTED");
    patchStatus(staffToken, orderId, "IN_FULFILLMENT");
    patchStatus(staffToken, orderId, "COMPLETED");

    mockMvc.perform(get("/api/orders/mine").header("Authorization", "Bearer " + customerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == %d)].status", orderId).value(hasItem("COMPLETED")));

    mockMvc.perform(patch("/api/staff/orders/{id}/status", orderId)
            .header("Authorization", "Bearer " + staffToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"CANCELLED\"}"))
        .andExpect(status().isBadRequest());
  }

  private void patchStatus(String staffToken, long orderId, String status) throws Exception {
    mockMvc.perform(patch("/api/staff/orders/{id}/status", orderId)
            .header("Authorization", "Bearer " + staffToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"%s\"}".formatted(status)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(status));
  }

  private long createMinimalOrder(String token) throws Exception {
    JsonNode cars = objectMapper.readTree(
        mockMvc.perform(get("/api/cars")).andReturn().getResponse().getContentAsString());
    long carId = cars.get(0).get("id").asLong();
    JsonNode car = objectMapper.readTree(
        mockMvc.perform(get("/api/cars/{id}", carId)).andReturn().getResponse().getContentAsString());
    long trimId = car.get("trims").get(0).get("id").asLong();
    JsonNode colors = objectMapper.readTree(
        mockMvc.perform(get("/api/colors")).andReturn().getResponse().getContentAsString());
    long colorId = colors.get(0).get("id").asLong();

    String body = """
        {"car":{"carModelId":%d,"trimId":%d,"colorId":%d,"extraServiceIds":[]},"parts":[]}
        """.formatted(carId, trimId, colorId);

    MvcResult created = mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
  }

  private String authToken(String email, String password, boolean register) throws Exception {
    if (register) {
      mockMvc.perform(post("/api/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"email":"%s","password":"%s"}
                  """.formatted(email, password)))
          .andExpect(status().isCreated());
    }
    return loginToken(email, password);
  }

  private String loginToken(String email, String password) throws Exception {
    MvcResult login = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password)))
        .andExpect(status().isOk())
        .andReturn();
    return objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
  }

  private static String envOrDefault(String key, String defaultValue) {
    String value = System.getenv(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
