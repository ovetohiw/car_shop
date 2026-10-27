package com.carshop.order;

import com.carshop.catalog.ExtraService;
import com.carshop.order.PricedOrderDraft.PricedLine;
import com.carshop.user.UserAccount;
import com.carshop.user.UserAccountRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

  private final OrderPricingService orderPricingService;
  private final ShopOrderRepository shopOrderRepository;
  private final UserAccountRepository userAccountRepository;

  public OrderService(
      OrderPricingService orderPricingService,
      ShopOrderRepository shopOrderRepository,
      UserAccountRepository userAccountRepository) {
    this.orderPricingService = orderPricingService;
    this.shopOrderRepository = shopOrderRepository;
    this.userAccountRepository = userAccountRepository;
  }

  @Transactional(readOnly = true)
  public QuoteResponse quote(CreateOrderRequest request) {
    return orderPricingService.price(request).toQuoteResponse();
  }

  @Transactional
  public OrderResponse create(Long userId, CreateOrderRequest request) {
    UserAccount user = userAccountRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    PricedOrderDraft draft = orderPricingService.price(request);
    ShopOrder order = ShopOrder.createNew(user, draft.totalAmount());
    for (PricedLine line : draft.lines()) {
      order.addItem(toOrderItem(line));
    }
    ShopOrder saved = shopOrderRepository.save(order);
    return toResponse(saved, draft);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOwnedOrder(Long userId, Long orderId) {
    ShopOrder order = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    if (!order.getUser().getId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
    }
    return toResponse(order, null);
  }

  private static OrderItem toOrderItem(PricedLine line) {
    if (line.itemType() == OrderItemType.CAR) {
      OrderItem item = OrderItem.carLine(
          line.carModel(),
          line.trim(),
          line.color(),
          line.unitPrice(),
          line.description());
      for (ExtraService extra : line.extras()) {
        item.addExtra(extra, extra.getPrice());
      }
      return item;
    }
    return OrderItem.partLine(line.part(), line.quantity(), line.unitPrice(), line.lineTotal());
  }

  private static OrderResponse toResponse(ShopOrder order, PricedOrderDraft draft) {
    List<QuoteResponse.QuoteLineResponse> lines = new ArrayList<>();
    if (draft != null) {
      return new OrderResponse(
          order.getId(),
          order.getStatus(),
          order.getTotalAmount(),
          order.getCreatedAt(),
          draft.toQuoteResponse().lines());
    }
    for (OrderItem item : order.getItems()) {
      lines.add(new QuoteResponse.QuoteLineResponse(
          item.getItemType().name(),
          item.getDescriptionSnapshot(),
          item.getQuantity(),
          item.getUnitPrice(),
          item.getLineTotal()));
    }
    return new OrderResponse(
        order.getId(),
        order.getStatus(),
        order.getTotalAmount(),
        order.getCreatedAt(),
        lines);
  }
}
