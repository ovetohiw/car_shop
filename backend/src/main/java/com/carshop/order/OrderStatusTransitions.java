package com.carshop.order;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class OrderStatusTransitions {

  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);

  static {
    ALLOWED.put(OrderStatus.NEW, EnumSet.of(OrderStatus.SUBMITTED, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.SUBMITTED, EnumSet.of(OrderStatus.IN_FULFILLMENT, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.IN_FULFILLMENT, EnumSet.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.COMPLETED, EnumSet.noneOf(OrderStatus.class));
    ALLOWED.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
  }

  private OrderStatusTransitions() {
  }

  public static boolean canTransition(OrderStatus from, OrderStatus to) {
    return ALLOWED.getOrDefault(from, Set.of()).contains(to);
  }
}
