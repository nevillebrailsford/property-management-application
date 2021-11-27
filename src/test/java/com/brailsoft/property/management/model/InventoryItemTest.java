package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.brailsoft.property.management.constant.Constants;

class InventoryItemTest {

	private Address address;
	private PostCode postCode;
	private String[] linesOfAddress = new String[] { "98 the street", "the town", "the county" };
	private Property testProperty;
	private InventoryItem testItem;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
	Document document;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		postCode = new PostCode("CW3 9SS");
		address = new Address(postCode, linesOfAddress);
		testProperty = new Property(address);
		LocalDate date = LocalDate.now();
		testItem = new InventoryItem("item1", "manufacturer1", "model1", "serialnumber1", "supplier1", date);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testInventoryItemStringStringStringStringStringLocalDate() {
		assertNotNull(testItem);
	}

	@Test
	void testInventoryItemInventoryItem() {
		InventoryItem item = new InventoryItem(testItem);
		assertNotNull(item);
		assertEquals(testItem, item);
		assertEquals(testItem.getDescription(), item.getDescription());
		assertEquals(testItem.getManufacturer(), item.getManufacturer());
		assertEquals(testItem.getModel(), item.getModel());
		assertEquals(testItem.getSerialNumber(), item.getSerialNumber());
		assertEquals(testItem.getSupplier(), item.getSupplier());
		assertEquals(testItem.getPurchaseDate(), item.getPurchaseDate());
	}

	@Test
	void testInventoryItemElement() {
		Element testElement = testItem.buildElement(document);
		assertNotNull(testElement);
		InventoryItem item = new InventoryItem(testElement);
		assertNotNull(item);
		assertEquals(testItem, item);
		assertEquals(testItem.getDescription(), item.getDescription());
		assertEquals(testItem.getManufacturer(), item.getManufacturer());
		assertEquals(testItem.getModel(), item.getModel());
		assertEquals(testItem.getSerialNumber(), item.getSerialNumber());
		assertEquals(testItem.getSupplier(), item.getSupplier());
		assertEquals(testItem.getPurchaseDate(), item.getPurchaseDate());
	}

	@Test
	void testBuildElement() {
		Element testElement = testItem.buildElement(document);
		assertNotNull(testElement);
		assertEquals(Constants.INVENTORY, testElement.getNodeName());
		assertTrue(Node.ELEMENT_NODE == testElement.getNodeType());
		assertEquals(6, testElement.getChildNodes().getLength());
	}

	@Test
	void testGetDescription() {
		assertEquals("item1", testItem.getDescription());
	}

	@Test
	void testGetManufacturer() {
		assertEquals("manufacturer1", testItem.getManufacturer());
	}

	@Test
	void testGetModel() {
		assertEquals("model1", testItem.getModel());
	}

	@Test
	void testGetSerialNumber() {
		assertEquals("serialnumber1", testItem.getSerialNumber());
	}

	@Test
	void testGetSupplier() {
		assertEquals("supplier1", testItem.getSupplier());
	}

	@Test
	void testGetPurchaseDate() {
		assertEquals(LocalDate.now().format(formatter), testItem.getPurchaseDate());
	}

	@Test
	void testGetOwner() {
		assertNull(testItem.getOwner());
	}

	@Test
	void testSetOwner() {
		assertNull(testItem.getOwner());
		testItem.setOwner(testProperty);
		assertNotNull(testItem.getOwner());
		assertEquals(testProperty, testItem.getOwner());
	}

	@Test
	void testEqualsObject() {
		InventoryItem item = new InventoryItem(testItem);
		assertTrue(item.equals(testItem));
	}

	@Test
	void testToString() {
		assertEquals("item1, manufacturer1, model1, serialnumber1", testItem.toString());
	}

	@Test
	void testCompareTo() {
		InventoryItem minItem = new InventoryItem("item1", "manufacturer1", "model1", "serialnumber1", "supplier1",
				LocalDate.now());
		InventoryItem minItem2 = new InventoryItem("item1", "manufacturer1", "model1", "serialnumber2", "supplier1",
				LocalDate.now());
		InventoryItem minItem3 = new InventoryItem("item1", "manufacturer1", "model2", "serialnumber2", "supplier1",
				LocalDate.now());
		InventoryItem midItem = new InventoryItem("item2", "manufacturer2", "model2", "serialnumber2", "supplier1",
				LocalDate.now());
		InventoryItem maxItem = new InventoryItem("item3", "manufacturer3", "model3", "serialnumber3", "supplier1",
				LocalDate.now());
		assertTrue(minItem.compareTo(midItem) < 0);
		assertTrue(minItem.compareTo(maxItem) < 0);
		assertTrue(midItem.compareTo(maxItem) < 0);
		assertTrue(minItem2.compareTo(midItem) < 0);
		assertTrue(minItem3.compareTo(midItem) < 0);
		assertTrue(minItem.compareTo(testItem) == 0);
	}

	@Test
	void testNullDescription() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			new InventoryItem(null, "manufacturer1", "model1", "serialnumber1", "supplier1", LocalDate.now());
		});
		assertEquals("InventoryItem: description was null", exc.getMessage());
	}

	@Test
	void testEmptyDescription() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			new InventoryItem("", "manufacturer1", "model1", "serialnumber1", "supplier1", LocalDate.now());
		});
		assertEquals("InventoryItem: description was null", exc.getMessage());
	}

	@Test
	void testBlankDescription() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			new InventoryItem(" ", "manufacturer1", "model1", "serialnumber1", "supplier1", LocalDate.now());
		});
		assertEquals("InventoryItem: description was null", exc.getMessage());
	}

	@Test
	void testNullItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			InventoryItem missing = null;
			new InventoryItem(missing);
		});
		assertEquals("InventoryItem: item was null", exc.getMessage());
	}

	@Test
	void testNullElement() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Element missing = null;
			new InventoryItem(missing);
		});
		assertEquals("InventoryItem: itemElement was null", exc.getMessage());
	}

	@Test
	void testNullDocument() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			Document missing = null;
			testItem.buildElement(missing);
		});
		assertEquals("InventoryItem: document was null", exc.getMessage());
	}

}
