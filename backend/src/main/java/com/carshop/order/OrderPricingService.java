package com.carshop.order;

import com.carshop.catalog.CarModel;
import com.carshop.catalog.CarModelRepository;
import com.carshop.catalog.ColorOption;
import com.carshop.catalog.ColorOptionRepository;
import com.carshop.catalog.ExtraService;
import com.carshop.catalog.ExtraServiceRepository;
import com.carshop.catalog.Part;
import com.carshop.catalog.PartRepository;
import com.carshop.catalog.Trim;
import com.carshop.catalog.TrimRepository;
import com.carshop.order.CreateOrderRequest.CarConfigurationRequest;
import com.carshop.order.CreateOrderRequest.PartLineRequest;
import com.carshop.order.PricedOrderDraft.PricedLine;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderPricingService {

  private final CarModelRepository carModelRepository;
  private final TrimRepository trimRepository;
  private final ColorOptionRepository colorOptionRepository;
  private final ExtraServiceRepository extraServiceRepository;
  private final PartRepository partRepository;

  public OrderPricingService(
      CarModelRepository carModelRepository,
      TrimRepository trimRepository,
      ColorOptionRepository colorOptionRepository,
      ExtraServiceRepository extraServiceRepository,
      PartRepository partRepository) {
    this.carModelRepository = carModelRepository;
    this.trimRepository = trimRepository;
    this.colorOptionRepository = colorOptionRepository;
    this.extraServiceRepository = extraServiceRepository;
    this.partRepository = partRepository;
  }

  @Transactional(readOnly = true)
  public PricedOrderDraft price(CreateOrderRequest request) {
    boolean hasCar = request.car() != null;
    boolean hasParts = request.parts() != null && !request.parts().isEmpty();
    if (!hasCar && !hasParts) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must include a car and/or parts");
    }

    List<PricedLine> lines = new ArrayList<>();
    List<BigDecimal> lineTotals = new ArrayList<>();

    if (hasCar) {
      PricedLine carLine = priceCar(request.car());
      lines.add(carLine);
      lineTotals.add(carLine.lineTotal());
    }

    if (hasParts) {
      for (PartLineRequest partLine : request.parts()) {
        PricedLine priced = pricePart(partLine);
        lines.add(priced);
        lineTotals.add(priced.lineTotal());
      }
    }

    BigDecimal total = PriceCalculator.orderTotal(lineTotals);
    if (!PriceCalculator.matchesClaimedTotal(total, request.claimedTotal())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price mismatch");
    }
    return new PricedOrderDraft(total, lines);
  }

  private PricedLine priceCar(CarConfigurationRequest carRequest) {
    CarModel model = carModelRepository.findById(carRequest.carModelId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown car model"));
    Trim trim = trimRepository.findById(carRequest.trimId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown trim"));
    if (!trim.getCarModel().getId().equals(model.getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trim does not belong to car model");
    }
    ColorOption color = colorOptionRepository.findById(carRequest.colorId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown color"));

    List<ExtraService> extras = resolveExtras(carRequest.extraServiceIds());
    List<BigDecimal> extraPrices = extras.stream().map(ExtraService::getPrice).toList();
    BigDecimal unitPrice = PriceCalculator.carUnitPrice(
        model.getBasePrice(),
        trim.getPriceDelta(),
        color.getPriceDelta(),
        extraPrices);

    String description = model.getName()
        + " / " + trim.getName()
        + " / " + color.getName()
        + (extras.isEmpty() ? "" : " + extras");

    return new PricedLine(
        OrderItemType.CAR,
        description,
        1,
        unitPrice,
        unitPrice,
        model,
        trim,
        color,
        extras,
        null);
  }

  private PricedLine pricePart(PartLineRequest partLine) {
    Part part = partRepository.findById(partLine.partId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown part"));
    BigDecimal lineTotal = PriceCalculator.partLineTotal(part.getPrice(), partLine.quantity());
    return new PricedLine(
        OrderItemType.PART,
        part.getName() + " x" + partLine.quantity(),
        partLine.quantity(),
        part.getPrice(),
        lineTotal,
        null,
        null,
        null,
        List.of(),
        part);
  }

  private List<ExtraService> resolveExtras(List<Long> extraServiceIds) {
    if (extraServiceIds == null || extraServiceIds.isEmpty()) {
      return List.of();
    }
    Set<Long> uniqueIds = new LinkedHashSet<>(extraServiceIds);
    List<ExtraService> extras = new ArrayList<>();
    for (Long id : uniqueIds) {
      ExtraService extra = extraServiceRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown extra service"));
      extras.add(extra);
    }
    return extras;
  }
}
