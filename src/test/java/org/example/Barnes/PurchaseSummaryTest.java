package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseSummaryTest {

    @Test
    @DisplayName("specification-based")
    void sums_price_and_tracks_unavailable_items_readonly() {
        PurchaseSummary ps = new PurchaseSummary();

        Book b1 = new Book("A", 20, 0);
        Book b2 = new Book("B", 5, 10);

        ps.addToTotalPrice(20);          // +20
        ps.addToTotalPrice(5 * 3);       // +15
        ps.addUnavailable(b1, 2);
        ps.addUnavailable(b2, 1);

        assertEquals(35, ps.getTotalPrice());
        Map<Book,Integer> unavail = ps.getUnavailable();
        assertEquals(2, unavail.size());
        assertEquals(2, unavail.get(b1));
        assertEquals(1, unavail.get(b2));

        assertThrows(UnsupportedOperationException.class,
                () -> unavail.put(new Book("C", 1, 1), 1));
    }

    @Test
    @DisplayName("structural-based")
    void constructor_initializes_zero_and_empty_map() {
        PurchaseSummary ps = new PurchaseSummary();
        assertEquals(0, ps.getTotalPrice());
        assertTrue(ps.getUnavailable().isEmpty());
    }
}
