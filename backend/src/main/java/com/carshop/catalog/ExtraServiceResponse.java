package com.carshop.catalog;

import java.math.BigDecimal;

public record ExtraServiceResponse(
    Long id,
    String code,
    String name,
    BigDecimal price
) {
  static ExtraServiceResponse from(ExtraService extra) {
    return new ExtraServiceResponse(
        extra.getId(),
        extra.getCode(),
        extra.getName(),
        extra.getPrice());
  }
}
