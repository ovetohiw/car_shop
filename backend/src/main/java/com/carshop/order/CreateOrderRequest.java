package com.carshop.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
    @Valid CarConfigurationRequest car,
    @Valid @Size(max = 50) List<PartLineRequest> parts,
    /**
     * Optional client-side total. If present and different from the server total, the request is rejected.
     */
    @DecimalMin("0.00") BigDecimal claimedTotal
) {
  public record CarConfigurationRequest(
      @NotNull Long carModelId,
      @NotNull Long trimId,
      @NotNull Long colorId,
      @Size(max = 20) List<Long> extraServiceIds
  ) {
  }

  public record PartLineRequest(
      @NotNull Long partId,
      @NotNull @Positive Integer quantity
  ) {
  }
}
