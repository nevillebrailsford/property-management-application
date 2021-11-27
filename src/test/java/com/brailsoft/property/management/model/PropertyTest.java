package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.brailsoft.property.management.constant.Constants;

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
	private InventoryItem testInventory;
	Document document;

	private ListChangeListener<? super MonitoredItem> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			change.next();
			assertTrue(change.wasAdded());
			assertEquals(1, change.getAddedSize());
			assertEquals(testItem, change.getAddedSubList().get(0));
		}
	};

	private ListChangeListener<? super InventoryItem> inventoryListener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends InventoryItem> change) {
			change.next();
			assertTrue(change.wasAdded());
			assertEquals(1, change.getAddedSize());
			assertEquals(testInventory, change.getAddedSubList().get(0));
		}
	};

	@BeforeEach
	void setUp() throws Exception {
		postCode = new PostCode("CW3 9SS");
		lowerPostCode = new PostCode("CW3 9SR");
		higherPostCode = new PostCode("CW3 9ST");
		address = new Address(postCode, linesOfAddress);
		testProperty = new Property(address);
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, LocalDate.now(), 1, Period.MONTHLY);
		testInventory = new InventoryItem("description1", "manufacturer1", "model1", "serialnumber1", "supplier1",
				LocalDate.now());
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	@AfterEach
	void tearDown() throws Exception {
		testProperty.removeListener(listener);
		testProperty.removeInventoryListener(inventoryListener);
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
	void testPropertyElement() {
		Element testElement = testProperty.buildElement(document);
		assertNotNull(testElement);
		Property property = new Property(testElement);
		assertNotNull(property);
		assertNotNull(property.getAddress());
		assertEquals(testProperty, property);
		assertEquals(postCode.toString(), property.getAddress().getPostCode().toString());
	}

	@Test
	void testBuildElement() {
		Element testElement = testProperty.buildElement(document);
		assertNotNull(testElement);
		assertEquals(Constants.PROPERTY, testElement.getNodeName());
		assertEquals(1, testElement.getChildNodes().getLength());
		Element element = (Element) testElement.getChildNodes().item(0);
		assertEquals(Constants.ADDRESS, element.getNodeName());
		assertEquals(4, element.getChildNodes().getLength());
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
	void testRemoveItem() {
		assertNotNull(testProperty.getItems());
		assertEquals(0, testProperty.getItems().size());
		testProperty.addItem(testItem);
		assertEquals(1, testProperty.getItems().size());
		assertEquals(testItem, testProperty.getItems().get(0));
		testProperty.removeItem(testItem);
		assertEquals(0, testProperty.getItems().size());
	}

	@Test
	void testAddInventoryItem() {
		assertNotNull(testProperty.getInventory());
		assertEquals(0, testProperty.getInventory().size());
		testProperty.addItem(testInventory);
		assertEquals(1, testProperty.getInventory().size());
		assertEquals(testInventory, testProperty.getInventory().get(0));
	}

	@Test
	void testRemoveInventory() {
		assertNotNull(testProperty.getInventory());
		assertEquals(0, testProperty.getInventory().size());
		testProperty.addItem(testInventory);
		assertEquals(1, testProperty.getInventory().size());
		assertEquals(testInventory, testProperty.getInventory().get(0));
		testProperty.removeItem(testInventory);
		assertEquals(0, testProperty.getInventory().size());
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
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDate.now(), 1, Period.WEEKLY));
		assertFalse(testProperty.areItemsOverdue());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1, LocalDate.now().minusYears(1).minusDays(1), 1,
				Period.WEEKLY));
		assertTrue(testProperty.areItemsOverdue());
	}

	@Test
	void testGetOverdueItems() {
		assertEquals(0, testProperty.getOverdueItems().size());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDate.now(), 1, Period.WEEKLY));
		assertEquals(0, testProperty.getOverdueItems().size());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1, LocalDate.now().minusYears(1).minusDays(1), 1,
				Period.WEEKLY));
		assertEquals(1, testProperty.getOverdueItems().size());
		assertEquals("item2", testProperty.getOverdueItems().get(0).getDescription());
	}

	@Test
	void testNoticesOverdue() {
		assertFalse(testProperty.areNoticesOverdue());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDate.now(), 1, Period.WEEKLY));
		assertFalse(testProperty.areNoticesOverdue());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDate.now().minusYears(1).minusWeeks(1).minusDays(1), 1, Period.WEEKLY));
		assertTrue(testProperty.areNoticesOverdue());
	}

	@Test
	void testGetOverdueNotices() {
		assertEquals(0, testProperty.getOverdueNotices().size());
		testProperty.addItem(new MonitoredItem("item1", Period.YEARLY, 1, LocalDate.now(), 1, Period.WEEKLY));
		assertEquals(0, testProperty.getOverdueNotices().size());
		testProperty.addItem(new MonitoredItem("item2", Period.YEARLY, 1,
				LocalDate.now().minusYears(1).minusWeeks(1).minusDays(1), 1, Period.WEEKLY));
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
	void testAddInventoryListener() {
		testProperty.addInventoryListener(inventoryListener);
		assertEquals(0, testProperty.getInventory().size());
		testProperty.addItem(testInventory);
		assertEquals(1, testProperty.getInventory().size());
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
	void testNullElement() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Element p = null;
			new Property(p);
		});
		assertEquals("Property: propertyElement was null", exc.getMessage());
	}

	@Test
	void testNullDocument() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Document p = null;
			testProperty.buildElement(p);
		});
		assertEquals("Property: document was null", exc.getMessage());
	}

	@Test
	void testAddNullItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			MonitoredItem missing = null;
			testProperty.addItem(missing);
		});
		assertEquals("Property: item was null", exc.getMessage());
	}

	@Test
	void testAddNullInventory() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			InventoryItem missing = null;
			testProperty.addItem(missing);
		});
		assertEquals("Property: item was null", exc.getMessage());
	}

	@Test
	void testRemoveNullItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			MonitoredItem missing = null;
			testProperty.removeItem(missing);
		});
		assertEquals("Property: item was null", exc.getMessage());
	}

	@Test
	void testRemoveNullInventory() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			InventoryItem missing = null;
			testProperty.removeItem(missing);
		});
		assertEquals("Property: item was null", exc.getMessage());
	}

	@Test
	void testRemoveMissingItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.removeItem(new MonitoredItem("item2", Period.YEARLY, 1,
					LocalDate.now().minusYears(1).minusWeeks(1).minusDays(1), 1, Period.WEEKLY));
		});
		assertEquals("Property: item item2 not found", exc.getMessage());
	}

	@Test
	void testRemoveMissingInventory() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.removeItem(testInventory);
		});
		assertEquals("Property: item description1, manufacturer1, model1, serialnumber1 not found", exc.getMessage());
	}

	@Test
	void testAddDuplicateItem() {
		testProperty.addItem(testItem);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.addItem(testItem);
		});
		assertEquals("Property: item item1 already exists", exc.getMessage());
	}

	@Test
	void testAddDuplicateInventory() {
		testProperty.addItem(testInventory);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.addItem(testInventory);
		});
		assertEquals("Property: item description1, manufacturer1, model1, serialnumber1 already exists",
				exc.getMessage());
	}

	@Test
	void testRemoveUnknownItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.removeItem(testItem);
		});
		assertEquals("Property: item item1 not found", exc.getMessage());
	}

	@Test
	void testRemoveUnknownInventory() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			testProperty.removeItem(testInventory);
		});
		assertEquals("Property: item description1, manufacturer1, model1, serialnumber1 not found", exc.getMessage());
	}
}
