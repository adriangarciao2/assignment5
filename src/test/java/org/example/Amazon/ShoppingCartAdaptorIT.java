package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartAdaptorIT {

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
    void persists_and_reads_items_roundtrip() {
        Item b = new Item(ItemType.OTHER, "X", 1, 30.0);
        Item e = new Item(ItemType.ELECTRONIC, "Mouse", 2, 15.0);

        cart.add(b);
        cart.add(e);

        List<Item> items = cart.getItems();
        assertEquals(2, items.size());


        assertTrue(items.stream().anyMatch(i ->
                i.getType() == ItemType.OTHER &&
                        i.getName().equals("X") &&
                        i.getQuantity() == 1 &&
                        Math.abs(i.getPricePerUnit() - 30.0) < 1e-9));

        assertTrue(items.stream().anyMatch(i ->
                i.getType() == ItemType.ELECTRONIC &&
                        i.getName().equals("Mouse") &&
                        i.getQuantity() == 2 &&
                        Math.abs(i.getPricePerUnit() - 15.0) < 1e-9));
    }

    @Test
    @DisplayName("structural-based")
    void handles_multiple_inserts_and_reset_database() {
        cart.add(new Item(ItemType.OTHER, "A", 1, 10.0));
        cart.add(new Item(ItemType.OTHER, "B", 3, 2.5));
        cart.add(new Item(ItemType.OTHER, "C", 0, 9.99));

        assertEquals(3, cart.getItems().size());

        db.resetDatabase();  // clears table
        assertEquals(0, cart.getItems().size());
    }
}
