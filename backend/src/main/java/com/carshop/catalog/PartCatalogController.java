package com.carshop.catalog;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parts")
public class PartCatalogController {

  private final CatalogService catalogService;

  public PartCatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping
  public List<PartResponse> listParts() {
    return catalogService.listParts();
  }

  @GetMapping("/{id}")
  public PartResponse getPart(@PathVariable Long id) {
    return catalogService.getPart(id);
  }
}
