package com.carshop.order;

import com.carshop.auth.UserAccountDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/quote")
  public QuoteResponse quote(@Valid @RequestBody CreateOrderRequest request) {
    return orderService.quote(request);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse create(
      @AuthenticationPrincipal UserAccountDetails principal,
      @Valid @RequestBody CreateOrderRequest request) {
    return orderService.create(principal.getUserId(), request);
  }

  @GetMapping("/{id}")
  public OrderResponse getOwned(
      @AuthenticationPrincipal UserAccountDetails principal,
      @PathVariable Long id) {
    return orderService.getOwnedOrder(principal.getUserId(), id);
  }
}
