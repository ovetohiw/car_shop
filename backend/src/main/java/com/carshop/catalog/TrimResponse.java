package com.carshop.catalog;

import java.math.BigDecimal;

public record TrimResponse(
    Long id,
    String code,
    String name,
    BigDecimal priceDelta
) {
  static TrimResponse from(Trim trim) {
    return new TrimResponse(
        trim.getId(),
        trim.getCode(),
        trim.getName(),
        trim.getPriceDelta());
  }
}
