package org.example.Amazon.Cost;

import java.util.List;
import org.example.Amazon.Item;

public interface PriceRule {
  double priceToAggregate(List<Item> cart);
}
