package com.carshop.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
    Long id,
    OrderStatus status,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String customerEmail
) {
  static OrderSummaryResponse from(ShopOrder order) {
    return new OrderSummaryResponse(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getCreatedAt(),
        order.getUpdatedAt(),
        order.getUser().getEmail());
  }
}
