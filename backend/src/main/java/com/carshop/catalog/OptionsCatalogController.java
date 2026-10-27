package com.carshop.catalog;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OptionsCatalogController {

  private final ColorOptionRepository colorOptionRepository;
  private final ExtraServiceRepository extraServiceRepository;

  public OptionsCatalogController(
      ColorOptionRepository colorOptionRepository,
      ExtraServiceRepository extraServiceRepository) {
    this.colorOptionRepository = colorOptionRepository;
    this.extraServiceRepository = extraServiceRepository;
  }

  @GetMapping("/colors")
  public List<ColorResponse> listColors() {
    return colorOptionRepository.findAll(Sort.by("name")).stream()
        .map(ColorResponse::from)
        .toList();
  }

  @GetMapping("/extra-services")
  public List<ExtraServiceResponse> listExtraServices() {
    return extraServiceRepository.findAll(Sort.by("name")).stream()
        .map(ExtraServiceResponse::from)
        .toList();
  }
}
