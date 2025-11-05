package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.*;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class AmazonIT {

    Database db;
    ShoppingCartAdaptor cart;

    @BeforeEach
    void setUp() {
        db = new Database();
        db.resetDatabase();
        cart = new ShoppingCartAdaptor(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    @DisplayName("specification-based")
    void calculates_total_using_rules_over_real_persisted_cart() {
        // Build a real cart
        Amazon amazon = new Amazon(cart, List.of(
                // rule1: regular cost = sum(pricePerUnit * qty)
                mockRegularCostRule(),
                // rule2: delivery fee = flat 5 if at least one item
                mockFlatDeliveryRule(5.0)
        ));

        amazon.addToCart(new Item(ItemType.OTHER, "X", 1, 30.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Mouse", 2, 15.0));
        // regular = 30 + (2*15)=60; delivery=5; total=65
        double total = amazon.calculate();
        assertEquals(65.0, total, 1e-9);
    }

    @Test
    @DisplayName("structural-based")
    void handles_empty_rules_and_multiple_rules_call_patterns() {
        Amazon none = new Amazon(cart, List.of());
        assertEquals(0.0, none.calculate());

        PriceRule r1 = mock(PriceRule.class);
        when(r1.priceToAggregate(anyList())).thenReturn(7.0);
        Amazon one = new Amazon(cart, List.of(r1));
        assertEquals(7.0, one.calculate());
        verify(r1, times(1)).priceToAggregate(anyList());

        PriceRule a = mock(PriceRule.class);
        PriceRule b = mock(PriceRule.class);
        when(a.priceToAggregate(anyList())).thenReturn(2.0);
        when(b.priceToAggregate(anyList())).thenReturn(3.0);

        Amazon two = new Amazon(cart, List.of(a, b));
        assertEquals(5.0, two.calculate());
        verify(a, times(1)).priceToAggregate(anyList());
        verify(b, times(1)).priceToAggregate(anyList());
    }

    private PriceRule mockRegularCostRule() {
        PriceRule rule = mock(PriceRule.class);
        when(rule.priceToAggregate(anyList())).thenAnswer((Answer<Double>) inv -> {
            @SuppressWarnings("unchecked")
            List<Item> items = (List<Item>) inv.getArgument(0);
            return items.stream()
                    .mapToDouble(i -> i.getPricePerUnit() * i.getQuantity())
                    .sum();
        });
        return rule;
    }

    private PriceRule mockFlatDeliveryRule(double fee) {
        PriceRule rule = mock(PriceRule.class);
        when(rule.priceToAggregate(anyList())).thenAnswer((Answer<Double>) inv -> {
            @SuppressWarnings("unchecked")
            List<Item> items = (List<Item>) inv.getArgument(0);
            return items.isEmpty() ? 0.0 : fee;
        });
        return rule;
    }
}

