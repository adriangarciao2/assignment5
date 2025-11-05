package org.example.Barnes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class BarnesAndNobleTest {

  //    interface BookDatabase { Book findByISBN(String isbn); }
  //    interface BuyBookProcess { void buyBook(Book book, int quantity); }

  @Test
  @DisplayName("specification-based")
  void returns_null_for_null_order_and_computes_total_and_unavailable() {
    BookDatabase db = mock(BookDatabase.class);
    BuyBookProcess process = mock(BuyBookProcess.class);
    BarnesAndNoble bn = new BarnesAndNoble(db, process);

    assertNull(bn.getPriceForCart(null));

    Book b1 = new Book("A", 10, 5); // price 10, have 5
    Book b2 = new Book("B", 20, 1); // price 20, have 1

    when(db.findByISBN("A")).thenReturn(b1);
    when(db.findByISBN("B")).thenReturn(b2);

    Map<String, Integer> order =
        Map.of(
            "A", 3, // fully available
            "B", 4 // partially available: 1 available, 3 unavailable
            );

    PurchaseSummary ps = bn.getPriceForCart(order);

    // total: A -> 3*10 = 30; B -> 1*20 = 20; total = 50
    assertNotNull(ps);
    assertEquals(50, ps.getTotalPrice());
    assertEquals(1, ps.getUnavailable().size());
    assertEquals(3, ps.getUnavailable().get(b2)); // 4 requested - 1 available

    // ensure buyBook called with adjusted quantities
    verify(process).buyBook(b1, 3);
    verify(process).buyBook(b2, 1);
    verifyNoMoreInteractions(process);
  }

  @Test
  @DisplayName("structural-based")
  void covers_branches_enough_stock_and_insufficient_stock() {
    BookDatabase db = mock(BookDatabase.class);
    BuyBookProcess process = mock(BuyBookProcess.class);
    BarnesAndNoble bn = new BarnesAndNoble(db, process);

    Book enough = new Book("X", 15, 10);
    Book shorty = new Book("Y", 7, 2);

    when(db.findByISBN("X")).thenReturn(enough);
    when(db.findByISBN("Y")).thenReturn(shorty);

    Map<String, Integer> order =
        Map.of(
            "X", 4, // has enough
            "Y", 5 // not enough (only 2 available)
            );

    PurchaseSummary ps = bn.getPriceForCart(order);

    // price: X -> 4*15 = 60 ; Y -> 2*7 = 14 ; total = 74
    assertEquals(74, ps.getTotalPrice());
    assertEquals(1, ps.getUnavailable().size());
    assertEquals(3, ps.getUnavailable().get(shorty)); // 5 - 2 = 3 unavailable

    // verify calls with correct adjusted quantities
    verify(process).buyBook(enough, 4);
    verify(process).buyBook(shorty, 2);

    // capture arguments to ensure correct mapping
    ArgumentCaptor<Book> bookCap = ArgumentCaptor.forClass(Book.class);
    ArgumentCaptor<Integer> qtyCap = ArgumentCaptor.forClass(Integer.class);
    verify(process, times(2)).buyBook(bookCap.capture(), qtyCap.capture());
    assertTrue(bookCap.getAllValues().contains(enough));
    assertTrue(bookCap.getAllValues().contains(shorty));
    assertTrue(qtyCap.getAllValues().contains(4));
    assertTrue(qtyCap.getAllValues().contains(2));
  }
}
