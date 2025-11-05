package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AmazonTest {

    @Test
    @DisplayName("specification-based")
    void calculates_final_price_from_all_rules_and_delegates_addToCart() {
        ShoppingCart cart = mock(ShoppingCart.class);
        List<Item> items = List.of(
                new Item(org.example.Amazon.Cost.ItemType.OTHER, "X", 1, 30.0),
                new Item(org.example.Amazon.Cost.ItemType.ELECTRONIC, "Mouse", 2, 15.0)
        );
        when(cart.getItems()).thenReturn(items);

        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);

        when(rule1.priceToAggregate(items)).thenReturn(10.0);
        when(rule2.priceToAggregate(items)).thenReturn(5.5);

        Amazon amazon = new Amazon(cart, List.of(rule1, rule2));

        double total = amazon.calculate();
        assertEquals(15.5, total, 1e-6);

        Item toAdd = new Item(org.example.Amazon.Cost.ItemType.OTHER, "Sticker", 3, 1.0);
        amazon.addToCart(toAdd);
        verify(cart).add(toAdd);
    }

    @Test
    @DisplayName("structural-based")
    void covers_empty_rules_multiple_rules_and_invocation_counts() {
        ShoppingCart cart = mock(ShoppingCart.class);
        when(cart.getItems()).thenReturn(List.of());

        // empty rules -> total 0
        Amazon empty = new Amazon(cart, List.of());
        assertEquals(0.0, empty.calculate());
        verify(cart, never()).getItems();

        // single rule -> called once, cart.getItems() called once
        PriceRule r1 = mock(PriceRule.class);
        when(r1.priceToAggregate(List.of())).thenReturn(7.0);
        Amazon one = new Amazon(cart, List.of(r1));
        assertEquals(7.0, one.calculate());
        verify(r1, times(1)).priceToAggregate(List.of());
        verify(cart, times(1)).getItems();

        // two rules -> both called, cart.getItems() called once per rule
        reset(cart, r1);
        when(cart.getItems()).thenReturn(List.of());
        PriceRule r2 = mock(PriceRule.class);
        when(r1.priceToAggregate(List.of())).thenReturn(2.0);
        when(r2.priceToAggregate(List.of())).thenReturn(3.0);

        Amazon two = new Amazon(cart, List.of(r1, r2));
        assertEquals(5.0, two.calculate());

        verify(r1, times(1)).priceToAggregate(List.of());
        verify(r2, times(1)).priceToAggregate(List.of());
        verify(cart, times(2)).getItems();
    }
}
