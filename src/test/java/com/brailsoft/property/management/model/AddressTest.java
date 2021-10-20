package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTest {

	private static final PostCode POST_CODE = new PostCode("CW3 9ST");
	private static final String LINE1 = "99 The Street";
	private static final String LINE2 = "The Town";
	private static final String LINE3 = "The County";
	private static final String LINE4 = "Country";
	private static final int NUMBER_OF_LINES = 4;
	private String[] lines = new String[] { LINE1, LINE2, LINE3, LINE4 };
	private static final String PRINTED_ADDRESS = "99 The Street, The Town, The County, Country CW3 9ST";

	Address address = new Address(POST_CODE, lines);

	@BeforeEach
	void setUp() throws Exception {
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
		assertEquals(LINE4, address.getLinesOfAddress()[3]);
	}

	@Test
	void testToString() {
		assertEquals(PRINTED_ADDRESS, address.toString());
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
			new Address(null, lines);
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
			new Address(null);
		});
	}

}
