package com.carshop.order;

import java.math.BigDecimal;
import java.util.List;

public record QuoteResponse(
    BigDecimal totalAmount,
    List<QuoteLineResponse> lines
) {
  public record QuoteLineResponse(
      String itemType,
      String description,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal lineTotal
  ) {
  }
}
