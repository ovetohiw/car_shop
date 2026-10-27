package com.carshop.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OrderStatusTransitionsTest {

  @Test
  void allowsHappyPathAndCancelBranches() {
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.NEW, OrderStatus.SUBMITTED)).isTrue();
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.SUBMITTED, OrderStatus.IN_FULFILLMENT))
        .isTrue();
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.IN_FULFILLMENT, OrderStatus.COMPLETED))
        .isTrue();
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.NEW, OrderStatus.CANCELLED)).isTrue();
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.COMPLETED, OrderStatus.CANCELLED))
        .isFalse();
    assertThat(OrderStatusTransitions.canTransition(OrderStatus.NEW, OrderStatus.COMPLETED)).isFalse();
  }
}
