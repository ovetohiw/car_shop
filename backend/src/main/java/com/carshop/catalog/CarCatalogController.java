package com.carshop.catalog;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars")
public class CarCatalogController {

  private final CatalogService catalogService;

  public CarCatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping
  public List<CarSummaryResponse> listCars() {
    return catalogService.listCars();
  }

  @GetMapping("/{id}")
  public CarDetailResponse getCar(@PathVariable Long id) {
    return catalogService.getCar(id);
  }
}
