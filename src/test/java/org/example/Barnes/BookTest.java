package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    @DisplayName("specification-based")
    void equality_is_by_ISBN_only_and_hashcode_matches() {
        Book b1 = new Book("ISBN-1", 10, 2);
        Book b2 = new Book("ISBN-1", 99, 999);
        Book b3 = new Book("ISBN-2", 10, 2);

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        assertNotEquals(b1, b3);
        assertNotEquals(b1, null);
        assertNotEquals(b1, "not a book");
    }

    @Test
    @DisplayName("structural-based")
    void getters_return_constructor_values() {
        Book b = new Book("X", 42, 7);
        assertEquals(42, b.getPrice());
        assertEquals(7, b.getQuantity());
    }
}

