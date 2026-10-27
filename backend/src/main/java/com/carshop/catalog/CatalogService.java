package com.carshop.catalog;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogService {

  static final int CATALOG_PAGE_SIZE = 100;

  private final CarModelRepository carModelRepository;
  private final TrimRepository trimRepository;
  private final PartRepository partRepository;

  public CatalogService(
      CarModelRepository carModelRepository,
      TrimRepository trimRepository,
      PartRepository partRepository) {
    this.carModelRepository = carModelRepository;
    this.trimRepository = trimRepository;
    this.partRepository = partRepository;
  }

  @Transactional(readOnly = true)
  public List<CarSummaryResponse> listCars() {
    return carModelRepository
        .findAll(PageRequest.of(0, CATALOG_PAGE_SIZE, Sort.by("name")))
        .stream()
        .map(CarSummaryResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public CarDetailResponse getCar(Long id) {
    CarModel model = carModelRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    List<TrimResponse> trims = trimRepository.findByCarModelId(model.getId()).stream()
        .map(TrimResponse::from)
        .toList();
    return new CarDetailResponse(
        model.getId(),
        model.getCode(),
        model.getName(),
        model.getDescription(),
        model.getBasePrice(),
        model.getImageUrl(),
        trims);
  }

  @Transactional(readOnly = true)
  public List<PartResponse> listParts() {
    return partRepository
        .findAll(PageRequest.of(0, CATALOG_PAGE_SIZE, Sort.by("name")))
        .stream()
        .map(PartResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public PartResponse getPart(Long id) {
    return partRepository.findById(id)
        .map(PartResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found"));
  }
}
