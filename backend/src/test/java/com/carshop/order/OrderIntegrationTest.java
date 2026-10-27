package com.carshop.order;

import static org.hamcrest.Matchers.notNullValue;
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

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

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
  void quoteAndCreateOrderPersistServerTotal() throws Exception {
    String email = "buyer." + UUID.randomUUID() + "@example.com";
    String password = "Customer1!";
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password)))
        .andExpect(status().isCreated());

    MvcResult login = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email":"%s","password":"%s"}
                """.formatted(email, password)))
        .andExpect(status().isOk())
        .andReturn();
    String token = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();

    JsonNode cars = objectMapper.readTree(
        mockMvc.perform(get("/api/cars")).andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString());
    long carId = cars.get(0).get("id").asLong();
    JsonNode car = objectMapper.readTree(
        mockMvc.perform(get("/api/cars/{id}", carId)).andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString());
    long trimId = car.get("trims").get(0).get("id").asLong();

    JsonNode colors = objectMapper.readTree(
        mockMvc.perform(get("/api/colors")).andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString());
    long colorId = colors.get(0).get("id").asLong();

    JsonNode extras = objectMapper.readTree(
        mockMvc.perform(get("/api/extra-services")).andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString());
    long extraId = extras.get(0).get("id").asLong();

    JsonNode parts = objectMapper.readTree(
        mockMvc.perform(get("/api/parts")).andExpect(status().isOk()).andReturn()
            .getResponse().getContentAsString());
    long partId = parts.get(0).get("id").asLong();

    String body = """
        {
          "car": {
            "carModelId": %d,
            "trimId": %d,
            "colorId": %d,
            "extraServiceIds": [%d]
          },
          "parts": [{"partId": %d, "quantity": 2}]
        }
        """.formatted(carId, trimId, colorId, extraId, partId);
    String mismatchedBody = """
        {
          "car": {
            "carModelId": %d,
            "trimId": %d,
            "colorId": %d,
            "extraServiceIds": [%d]
          },
          "parts": [{"partId": %d, "quantity": 2}],
          "claimedTotal": 1
        }
        """.formatted(carId, trimId, colorId, extraId, partId);

    MvcResult quote = mockMvc.perform(post("/api/orders/quote")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalAmount", notNullValue()))
        .andReturn();
    JsonNode quoteJson = objectMapper.readTree(quote.getResponse().getContentAsString());
    String serverTotal = quoteJson.get("totalAmount").asText();

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mismatchedBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Price mismatch"));

    MvcResult created = mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.totalAmount").value(serverTotal))
        .andReturn();

    long orderId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

    mockMvc.perform(get("/api/orders/{id}", orderId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId))
        .andExpect(jsonPath("$.totalAmount").value(serverTotal));
  }

  private static String envOrDefault(String key, String defaultValue) {
    String value = System.getenv(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
