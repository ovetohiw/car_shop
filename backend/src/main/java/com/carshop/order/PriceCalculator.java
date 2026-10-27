package com.carshop.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Pure pricing rules: totals are never taken from the client.
 */
public final class PriceCalculator {

  private PriceCalculator() {
  }

  public static BigDecimal carUnitPrice(
      BigDecimal basePrice,
      BigDecimal trimDelta,
      BigDecimal colorDelta,
      List<BigDecimal> extraPrices) {
    BigDecimal total = money(basePrice).add(money(trimDelta)).add(money(colorDelta));
    for (BigDecimal extra : extraPrices) {
      total = total.add(money(extra));
    }
    return total;
  }

  public static BigDecimal partLineTotal(BigDecimal unitPrice, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be positive");
    }
    return money(unitPrice).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal orderTotal(List<BigDecimal> lineTotals) {
    BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    for (BigDecimal line : lineTotals) {
      total = total.add(money(line));
    }
    return total;
  }

  public static boolean matchesClaimedTotal(BigDecimal serverTotal, BigDecimal claimedTotal) {
    if (claimedTotal == null) {
      return true;
    }
    return money(serverTotal).compareTo(money(claimedTotal)) == 0;
  }

  private static BigDecimal money(BigDecimal value) {
    return Objects.requireNonNullElse(value, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
  }
}
