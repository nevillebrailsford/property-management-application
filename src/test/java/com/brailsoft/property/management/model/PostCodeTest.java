package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostCodeTest {

	private static final String LOWER_POST_CODE = "CW3 9SR";
	private static final String SAMPLE_POST_CODE = "CW3 9SS";
	private static final String HIGHER_POST_CODE = "CW3 9ST";
	private static final String FOUR_CHARACTER_POST_CODE = "WC2H 7LT";

	PostCode postcodeToBeTested = new PostCode(SAMPLE_POST_CODE);

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testValidPostCode() {
		new PostCode(SAMPLE_POST_CODE);
	}

	@Test
	void testGetValue() {
		assertEquals(SAMPLE_POST_CODE, postcodeToBeTested.getValue());
	}

	@Test
	void testEqualsObject() {
		assertEquals(postcodeToBeTested, new PostCode(SAMPLE_POST_CODE));
		assertNotEquals(postcodeToBeTested, new PostCode(LOWER_POST_CODE));
		assertNotEquals(postcodeToBeTested, new PostCode(HIGHER_POST_CODE));
	}

	@Test
	void testToString() {
		assertEquals(SAMPLE_POST_CODE, postcodeToBeTested.toString());
	}

	@Test
	void testCompareTo() {
		assertTrue(postcodeToBeTested.compareTo(new PostCode(LOWER_POST_CODE)) > 0);
		assertTrue(postcodeToBeTested.compareTo(new PostCode(SAMPLE_POST_CODE)) == 0);
		assertTrue(postcodeToBeTested.compareTo(new PostCode(HIGHER_POST_CODE)) < 0);
	}

	@Test
	void testCloneSortCode() {
		assertEquals(SAMPLE_POST_CODE, new PostCode(postcodeToBeTested).getValue());
	}

	@Test
	void testFourCharacterPostCode() {
		assertEquals(FOUR_CHARACTER_POST_CODE, new PostCode(FOUR_CHARACTER_POST_CODE).getValue());
	}

	@Test
	void testMissingPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("");
		});
	}

	@Test
	void testBlankPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode(" ");
		});
	}

	@Test
	void testIllegalSymbolPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("CW3-9ST");
		});
	}

	@Test
	void testNonDigitPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("CW3 AST");
		});
	}

	@Test
	void testMissingLstValuePostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("CW3");
		});
	}

	@Test
	void testTooManyDigitsPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("CW3 99ST");
		});
	}

	@Test
	void testTooManyValuesPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			new PostCode("CW3 9ST 9ST");
		});
	}

	@Test
	void testNullPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			PostCode missing = null;
			new PostCode(missing);
		});
	}

	@Test
	void testNullStringPostCode() {
		assertThrows(IllegalArgumentException.class, () -> {
			String missing = null;
			new PostCode(missing);
		});
	}

}
