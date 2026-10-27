package com.carshop.order;

import com.carshop.user.UserAccount;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class ShopOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private OrderStatus status;

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  protected ShopOrder() {
  }

  public static ShopOrder createNew(UserAccount user, BigDecimal totalAmount) {
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    ShopOrder order = new ShopOrder();
    order.user = user;
    order.status = OrderStatus.NEW;
    order.totalAmount = totalAmount;
    order.createdAt = now;
    order.updatedAt = now;
    return order;
  }

  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void transitionTo(OrderStatus nextStatus) {
    if (!OrderStatusTransitions.canTransition(status, nextStatus)) {
      throw new IllegalStateException(
          "Cannot transition from " + status + " to " + nextStatus);
    }
    this.status = nextStatus;
    this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
  }

  public Long getId() {
    return id;
  }

  public UserAccount getUser() {
    return user;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<OrderItem> getItems() {
    return items;
  }
}
