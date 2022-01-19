package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class AddressTest {

	private static final PostCode POST_CODE = new PostCode("CW3 9ST");
	private static final String LINE1 = "99 The Street";
	private static final String LINE2 = "The Town";
	private static final String LINE3 = "The County";
	private static final int NUMBER_OF_LINES = 3;
	private String[] lines = new String[] { LINE1, LINE2, LINE3 };
	private static final String PRINTED_ADDRESS = "99 The Street, The Town, The County CW3 9ST";

	Address address = new Address(POST_CODE, lines);
	Document document;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	}

	@BeforeEach
	void setUp() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testAddressPostCodeStringArray() {
		new Address(POST_CODE, lines);
	}

	@Test
	void testAddressAddress() {
		new Address(address);
	}

	@Test
	void testAddressElement() {
		Element testElement = address.buildElement(document);
		assertNotNull(testElement);
		Address newAddress = new Address(testElement);
		assertNotNull(newAddress);
		assertNotNull(newAddress.getPostCode());
		assertEquals(3, newAddress.getLinesOfAddress().length);
		assertEquals(newAddress, address);
	}

	@Test
	void testGetPostCode() {
		assertEquals(POST_CODE, address.getPostCode());
	}

	@Test
	void testEqualsObject() {
		assertEquals(new Address(POST_CODE, lines), address);
	}

	@Test
	void testGetLinesOfCode() {
		assertEquals(NUMBER_OF_LINES, address.getLinesOfAddress().length);
		assertEquals(LINE1, address.getLinesOfAddress()[0]);
		assertEquals(LINE2, address.getLinesOfAddress()[1]);
		assertEquals(LINE3, address.getLinesOfAddress()[2]);
	}

	@Test
	void testToString() {
		assertEquals(PRINTED_ADDRESS, address.toString());
	}

	@Test
	void testBuildElement() {
		Element testElement = address.buildElement(document);
		assertNotNull(testElement);
		assertEquals(Constants.ADDRESS, testElement.getNodeName());
		assertEquals(4, testElement.getChildNodes().getLength());
		assertTrue(testElement.getNodeType() == Node.ELEMENT_NODE);
	}

	@Test
	void testNullLinesOfAddress() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Address(POST_CODE, null);
		});
	}

	@Test
	void testNullPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			PostCode missing = null;
			new Address(missing, lines);
		});
	}

	@Test
	void testEmptyLinesOfAddress() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Address(POST_CODE, new String[0]);
		});
	}

	@Test
	void testNullAddress() {
		assertThrows(IllegalArgumentException.class, () -> {
			Address missing = null;
			new Address(missing);
		});
	}

	@Test
	void testNullElement() {
		assertThrows(IllegalArgumentException.class, () -> {
			Element missing = null;
			new Address(missing);
		});
	}

	@Test
	void testNullDocument() {
		assertThrows(IllegalArgumentException.class, () -> {
			Document missing = null;
			address.buildElement(missing);
		});
	}

}
