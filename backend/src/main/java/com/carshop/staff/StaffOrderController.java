package com.carshop.staff;

import com.carshop.order.OrderService;
import com.carshop.order.OrderSummaryResponse;
import com.carshop.order.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderController {

  private final OrderService orderService;

  public StaffOrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public List<OrderSummaryResponse> listAll() {
    return orderService.listAllForStaff();
  }

  @PatchMapping("/{id}/status")
  public OrderSummaryResponse updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody UpdateOrderStatusRequest request) {
    return orderService.updateStatusForStaff(id, request.status());
  }
}
