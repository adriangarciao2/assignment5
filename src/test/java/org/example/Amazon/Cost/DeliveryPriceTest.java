package org.example.Amazon.Cost;

import org.example.Amazon.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryPriceTest {

    private final DeliveryPrice rule = new DeliveryPrice();

    @Test
    @DisplayName("specification-based")
    void deliveryCost_follows_documented_ranges() {
        // empty cart -> 0
        assertEquals(0.0, rule.priceToAggregate(List.of()));

        // 1..3 items -> 5
        assertEquals(5.0, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem()
        )), 0.0001);

        // 4..10 items -> 12.5
        assertEquals(12.5, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem(), fakeItem(), fakeItem()
        )), 0.0001);

        // >10 items -> 20
        assertEquals(20.0, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem(), fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem()
        )), 0.0001);
    }

    @Test
    @DisplayName("structural-based")
    void deliveryCost_covers_all_branches_explicitly() {
        // branch: totalItems == 0
        assertEquals(0.0, rule.priceToAggregate(List.of()));

        // branch: 1 <= totalItems <= 3
        assertEquals(5.0, rule.priceToAggregate(List.of(fakeItem())));
        assertEquals(5.0, rule.priceToAggregate(List.of(fakeItem(), fakeItem(), fakeItem())));

        // branch: 4 <= totalItems <= 10
        assertEquals(12.5, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem(), fakeItem(), fakeItem()
        )), 0.0001);
        assertEquals(12.5, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem(), fakeItem()
        )), 0.0001);

        // branch: else (>10)
        assertEquals(20.0, rule.priceToAggregate(List.of(
                fakeItem(), fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem(), fakeItem(), fakeItem(), fakeItem(), fakeItem(),
                fakeItem()
        )), 0.0001);
    }

    private Item fakeItem() {
        return new Item(ItemType.OTHER, "x", 1, 1);
    }
}
