package com.carshop.catalog;

import java.math.BigDecimal;

public record CarSummaryResponse(
    Long id,
    String code,
    String name,
    String description,
    BigDecimal basePrice,
    String imageUrl
) {
  static CarSummaryResponse from(CarModel model) {
    return new CarSummaryResponse(
        model.getId(),
        model.getCode(),
        model.getName(),
        model.getDescription(),
        model.getBasePrice(),
        model.getImageUrl());
  }
}
