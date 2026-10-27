package com.carshop.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "colors")
public class ColorOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 64)
  private String code;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(name = "hex_code", nullable = false, length = 7)
  private String hexCode;

  @Column(name = "price_delta", nullable = false, precision = 12, scale = 2)
  private BigDecimal priceDelta;

  protected ColorOption() {
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getHexCode() {
    return hexCode;
  }

  public BigDecimal getPriceDelta() {
    return priceDelta;
  }
}
