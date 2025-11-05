package org.example.Amazon.Cost;

import java.util.List;
import org.example.Amazon.Item;

public class ExtraCostForElectronics implements PriceRule {
  @Override
  public double priceToAggregate(List<Item> cart) {
    boolean hasAnElectronicDevice =
        cart.stream().anyMatch(it -> it.getType() == ItemType.ELECTRONIC);

    if (hasAnElectronicDevice) return 7.50;

    return 0;
  }
}
