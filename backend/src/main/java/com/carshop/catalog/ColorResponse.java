package com.carshop.catalog;

import java.math.BigDecimal;

public record ColorResponse(
    Long id,
    String code,
    String name,
    String hexCode,
    BigDecimal priceDelta
) {
  static ColorResponse from(ColorOption color) {
    return new ColorResponse(
        color.getId(),
        color.getCode(),
        color.getName(),
        color.getHexCode(),
        color.getPriceDelta());
  }
}
