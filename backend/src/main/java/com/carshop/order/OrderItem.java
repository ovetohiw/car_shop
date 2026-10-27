package com.carshop.order;

import com.carshop.catalog.CarModel;
import com.carshop.catalog.ColorOption;
import com.carshop.catalog.Part;
import com.carshop.catalog.Trim;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private ShopOrder order;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_type", nullable = false, length = 16)
  private OrderItemType itemType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "car_model_id")
  private CarModel carModel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trim_id")
  private Trim trim;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "color_id")
  private ColorOption color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "part_id")
  private Part part;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal lineTotal;

  @Column(name = "description_snapshot", nullable = false, length = 1000)
  private String descriptionSnapshot;

  protected OrderItem() {
  }

  public Long getId() {
    return id;
  }

  public ShopOrder getOrder() {
    return order;
  }

  public OrderItemType getItemType() {
    return itemType;
  }

  public CarModel getCarModel() {
    return carModel;
  }

  public Trim getTrim() {
    return trim;
  }

  public ColorOption getColor() {
    return color;
  }

  public Part getPart() {
    return part;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }

  public String getDescriptionSnapshot() {
    return descriptionSnapshot;
  }
}
