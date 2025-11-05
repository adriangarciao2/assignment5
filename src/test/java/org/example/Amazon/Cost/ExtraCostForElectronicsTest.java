package org.example.Amazon.Cost;

import org.example.Amazon.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExtraCostForElectronicsTest {

    private final ExtraCostForElectronics rule = new ExtraCostForElectronics();

    @Test
    @DisplayName("specification-based")
    void adds_extra_cost_when_cart_has_at_least_one_electronic() {
        Item electronic = new Item(ItemType.ELECTRONIC, "Phone", 500, 1);
        Item book = new Item(ItemType.OTHER, "Book", 10, 1);

        // has electronic -> 7.50
        assertEquals(7.50, rule.priceToAggregate(List.of(electronic, book)));

        // no electronic -> 0
        assertEquals(0.0, rule.priceToAggregate(List.of(book)));

        // empty cart -> 0
        assertEquals(0.0, rule.priceToAggregate(List.of()));
    }

    @Test
    @DisplayName("structural-based")
    void covers_both_paths_of_anyMatch() {
        // path 1: anyMatch == true
        Item laptop = new Item(ItemType.ELECTRONIC,"Laptop", 999, 1);
        assertEquals(7.50, rule.priceToAggregate(List.of(laptop)), 0.0001);

        // path 2: anyMatch == false
        Item apple = new Item(ItemType.OTHER, "Apple", 1, 3);
        Item chair = new Item(ItemType.OTHER, "Chair", 20, 1);
        assertEquals(0.0, rule.priceToAggregate(List.of(apple, chair)), 0.0001);
    }
}

