package com.carshop.catalog;

import java.math.BigDecimal;
import java.util.List;

public record CarDetailResponse(
    Long id,
    String code,
    String name,
    String description,
    BigDecimal basePrice,
    String imageUrl,
    List<TrimResponse> trims
) {
}
