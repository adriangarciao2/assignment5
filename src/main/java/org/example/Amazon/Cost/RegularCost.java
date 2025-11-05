package org.example.Amazon.Cost;

import java.util.List;
import org.example.Amazon.Item;

public class RegularCost implements PriceRule {
  @Override
  public double priceToAggregate(List<Item> cart) {

    double price = 0;
    for (Item item : cart) {
      price += item.getPricePerUnit() * item.getQuantity();
    }

    return price;
  }
}
