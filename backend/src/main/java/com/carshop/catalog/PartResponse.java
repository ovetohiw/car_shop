package com.carshop.catalog;

import java.math.BigDecimal;

public record PartResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    String imageUrl,
    Integer stockQty
) {
  static PartResponse from(Part part) {
    return new PartResponse(
        part.getId(),
        part.getSku(),
        part.getName(),
        part.getDescription(),
        part.getPrice(),
        part.getImageUrl(),
        part.getStockQty());
  }
}
