package org.example.Amazon.Cost;

import org.example.Amazon.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegularCostTest {

    private final RegularCost rule = new RegularCost();

    @Test
    @DisplayName("specification-based")
    void sums_pricePerUnit_times_quantity_for_every_item() {
        Item apples = new Item(ItemType.OTHER, "Apples", 2, 3);     // 6
        Item book = new Item(ItemType.OTHER, "Book", 10, 1);       // 10
        Item cable = new Item(ItemType.OTHER, "Cable", 5, 2);     // 10

        double total = rule.priceToAggregate(List.of(apples, book, cable));

        assertEquals(26.0, total);
    }

    @Test
    @DisplayName("structural-based")
    void iterates_over_all_items_and_handles_empty_cart() {
        // empty cart -> 0
        assertEquals(0.0, rule.priceToAggregate(List.of()));

        // single item
        Item single = new Item(ItemType.OTHER, "Single", 4, 2); // 8
        assertEquals(8, rule.priceToAggregate(List.of(single)));

        // multiple items with different quantities
        Item a = new Item(ItemType.OTHER, "A", 1, 5);  // 5
        Item b = new Item(ItemType.OTHER, "B", 7, 1);  // 7
        Item c = new Item(ItemType.ELECTRONIC, "C", 100, 0); // 0
        double total = rule.priceToAggregate(List.of(a, b, c));
        assertEquals(12.0, total);
    }
}

