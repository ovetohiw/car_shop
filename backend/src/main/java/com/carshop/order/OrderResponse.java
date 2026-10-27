package com.carshop.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    OrderStatus status,
    BigDecimal totalAmount,
    LocalDateTime createdAt,
    List<QuoteResponse.QuoteLineResponse> lines
) {
}
