package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;

class PropertyTest {

	private Address address;
	private PostCode postCode;
	private PostCode lowerPostCode;
	private PostCode higherPostCode;
	private String[] linesOfAddress = new String[] { "98 the street", "the town", "the county" };
	private String[] lowerLinesOfAddress = new String[] { "97 the street", "the town", "the county" };
	private String[] higherLinesOfAddress = new String[] { "99 the street", "the town", "the county" };
	private Property testProperty;
	private MonitoredItem testItem;

	private ListChangeListener<? super MonitoredItem> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			change.next();
			assertTrue(change.wasAdded());
			assertEquals(1, change.getAddedSize());
			assertEquals(testItem, change.getAddedSubList().get(0));
		}
	};

	@BeforeEach
	void setUp() throws Exception {
		postCode = new PostCode("CW3 9SS");
		lowerPostCode = new PostCode("CW3 9SR");
		higherPostCode = new PostCode("CW3 9ST");
		address = new Address(postCode, linesOfAddress);
		testProperty = new Property(address);
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, LocalDateTime.now(), 1, Period.MONTHLY);
	}

	@AfterEach
	void tearDown() throws Exception {
		testProperty.removeListener(listener);
		testProperty.clear();
	}

	@Test
	void testPropertyAddress() {
		Property property = new Property(address);
		assertNotNull(property);
	}

	@Test
	void testPropertyProperty() {
		Property property = new Property(testProperty);
		assertNotNull(property.getAddress());
		assertEquals(testProperty, property);
		assertEquals(postCode.toString(), property.getAddress().getPostCode().toString());
	}

	@Test
	void testAddItem() {
		assertNotNull(testProperty.getItems());
		assertEquals(0, testProperty.getItems().size());
		testProperty.addItem(testItem);
		assertEquals(1, testProperty.getItems().size());
	}

	@Test
	void testGetItems() {
		assertNotNull(testProperty.getItems());
		assertEquals(0, testProperty.getItems().size());
		testProperty.addItem(testItem);
		assertEquals(1, testProperty.getItems().size());
		assertEquals(testItem, testProperty.getItems().get(0));
	}

	@Test
	void testCompareTo() {
		assertTrue(testProperty.compareTo(testProperty) == 0);
		assertTrue(testProperty.compareTo(new Property(new Address(postCode, linesOfAddress))) == 0);
		assertTrue(testProperty.compareTo(new Property(new Address(lowerPostCode, linesOfAddress))) > 0);
		assertTrue(testProperty.compareTo(new Property(new Address(higherPostCode, linesOfAddress))) < 0);
		assertTrue(testProperty.compareTo(new Property(new Address(postCode, lowerLinesOfAddress))) > 0);
		assertTrue(testProperty.compareTo(new Property(new Address(postCode, higherLinesOfAddress))) < 0);
	}

	@Test
	void testEqualsObject() {
		assertTrue(testProperty.equals(testProperty));
	}

	@Test
	void testToString() {
		assertEquals("98 the street, the town, the county CW3 9SS", testProperty.toString());
	}

	@Test
	void testItemsOverdue() {
		assertFalse(testProperty.areItemsOverdue());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDateTime.now(), 1, Period.WEEKLY));
		assertFalse(testProperty.areItemsOverdue());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDateTime.now().minusYears(1).minusMinutes(1), 1, Period.WEEKLY));
		assertTrue(testProperty.areItemsOverdue());
	}

	@Test
	void testGetOverdueItems() {
		assertEquals(0, testProperty.getOverdueItems().size());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDateTime.now(), 1, Period.WEEKLY));
		assertEquals(0, testProperty.getOverdueItems().size());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDateTime.now().minusYears(1).minusMinutes(1), 1, Period.WEEKLY));
		assertEquals(1, testProperty.getOverdueItems().size());
		assertEquals("item2", testProperty.getOverdueItems().get(0).getDescription());
	}

	@Test
	void testNoticesOverdue() {
		assertFalse(testProperty.areNoticesOverdue());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDateTime.now(), 1, Period.WEEKLY));
		assertFalse(testProperty.areNoticesOverdue());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDateTime.now().minusYears(1).minusWeeks(1).minusMinutes(1), 1, Period.WEEKLY));
		assertTrue(testProperty.areNoticesOverdue());
	}

	@Test
	void testGetOverdueNotices() {
		assertEquals(0, testProperty.getOverdueNotices().size());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDateTime.now(), 1, Period.WEEKLY));
		assertEquals(0, testProperty.getOverdueNotices().size());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDateTime.now().minusYears(1).minusWeeks(1).minusMinutes(1), 1, Period.WEEKLY));
		assertEquals(1, testProperty.getOverdueNotices().size());
		assertEquals("item2", testProperty.getOverdueNotices().get(0).getDescription());
	}

	@Test
	void testAddListener() {
		testProperty.addListener(listener);
		assertEquals(0, testProperty.getItems().size());
		testProperty.addItem(testItem);
		assertEquals(1, testProperty.getItems().size());
	}

	@Test
	void testNullAddress() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Address a = null;
			new Property(a);
		});
		assertEquals("Property: address was null", exc.getMessage());
	}

	@Test
	void testNullProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Property p = null;
			new Property(p);
		});
		assertEquals("Property: property was null", exc.getMessage());
	}

	@Test
	void testAddNullItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.addItem(null);
		});
		assertEquals("Property: item was null", exc.getMessage());
	}

	@Test

	void testAddDuplicateItem() {
		testProperty.addItem(testItem);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.addItem(testItem);
		});
		assertEquals("Property: item item1 already exists", exc.getMessage());
	}
}
