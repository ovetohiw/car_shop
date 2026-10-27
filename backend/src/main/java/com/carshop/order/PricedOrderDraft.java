package com.carshop.order;

import com.carshop.catalog.CarModel;
import com.carshop.catalog.ColorOption;
import com.carshop.catalog.ExtraService;
import com.carshop.catalog.Part;
import com.carshop.catalog.Trim;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

record PricedOrderDraft(BigDecimal totalAmount, List<PricedLine> lines) {

  record PricedLine(
      OrderItemType itemType,
      String description,
      int quantity,
      BigDecimal unitPrice,
      BigDecimal lineTotal,
      CarModel carModel,
      Trim trim,
      ColorOption color,
      List<ExtraService> extras,
      Part part
  ) {
  }

  QuoteResponse toQuoteResponse() {
    List<QuoteResponse.QuoteLineResponse> quoteLines = new ArrayList<>();
    for (PricedLine line : lines) {
      quoteLines.add(new QuoteResponse.QuoteLineResponse(
          line.itemType().name(),
          line.description(),
          line.quantity(),
          line.unitPrice(),
          line.lineTotal()));
    }
    return new QuoteResponse(totalAmount, quoteLines);
  }
}
