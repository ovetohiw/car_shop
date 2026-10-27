package com.carshop.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "trims")
public class Trim {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "car_model_id", nullable = false)
  private CarModel carModel;

  @Column(nullable = false, length = 64)
  private String code;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(name = "price_delta", nullable = false, precision = 12, scale = 2)
  private BigDecimal priceDelta;

  protected Trim() {
  }

  public Long getId() {
    return id;
  }

  public CarModel getCarModel() {
    return carModel;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPriceDelta() {
    return priceDelta;
  }
}
