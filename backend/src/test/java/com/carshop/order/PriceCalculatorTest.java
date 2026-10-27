package com.carshop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PriceCalculatorTest {

  @Test
  void carUnitPriceSumsBaseTrimColorAndExtras() {
    BigDecimal total = PriceCalculator.carUnitPrice(
        new BigDecimal("24990.00"),
        new BigDecimal("2500.00"),
        new BigDecimal("450.00"),
        List.of(new BigDecimal("1200.00"), new BigDecimal("800.00")));

    assertThat(total).isEqualByComparingTo("29940.00");
  }

  @Test
  void partLineTotalMultipliesQuantity() {
    assertThat(PriceCalculator.partLineTotal(new BigDecimal("129.90"), 2))
        .isEqualByComparingTo("259.80");
  }

  @Test
  void partLineTotalRejectsNonPositiveQuantity() {
    assertThatThrownBy(() -> PriceCalculator.partLineTotal(new BigDecimal("10.00"), 0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void claimedTotalMismatchDetected() {
    BigDecimal server = new BigDecimal("100.00");
    assertThat(PriceCalculator.matchesClaimedTotal(server, null)).isTrue();
    assertThat(PriceCalculator.matchesClaimedTotal(server, new BigDecimal("100.00"))).isTrue();
    assertThat(PriceCalculator.matchesClaimedTotal(server, new BigDecimal("1.00"))).isFalse();
  }
}
