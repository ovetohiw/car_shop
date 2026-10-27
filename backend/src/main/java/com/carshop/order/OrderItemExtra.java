package com.carshop.order;

import com.carshop.catalog.ExtraService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_item_extras")
@IdClass(OrderItemExtra.OrderItemExtraId.class)
public class OrderItemExtra {

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "extra_service_id", nullable = false)
  private ExtraService extraService;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  protected OrderItemExtra() {
  }

  public OrderItem getOrderItem() {
    return orderItem;
  }

  public ExtraService getExtraService() {
    return extraService;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public static final class OrderItemExtraId implements Serializable {

    private Long orderItem;
    private Long extraService;

    public OrderItemExtraId() {
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof OrderItemExtraId that)) {
        return false;
      }
      return Objects.equals(orderItem, that.orderItem)
          && Objects.equals(extraService, that.extraService);
    }

    @Override
    public int hashCode() {
      return Objects.hash(orderItem, extraService);
    }
  }
}
