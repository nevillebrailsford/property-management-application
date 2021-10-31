package com.brailsoft.property.management.preference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constants.TestConstants;

class ApplicationPreferencesTest {

	private ApplicationPreferences preferences = ApplicationPreferences.getInstance(TestConstants.TEST_NODE_NAME);

	@BeforeEach
	void setUp() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.TEST_NODE_NAME).clear();
	}

	@AfterEach
	void tearDown() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.TEST_NODE_NAME).clear();
	}

	@Test
	void testGetInstance() {
		assertNotNull(preferences);
		ApplicationPreferences ap = ApplicationPreferences.getInstance(TestConstants.TEST_NODE_NAME);
		assertNotNull(ap);
		assertTrue(preferences == ap);
	}

	@Test
	void testGetDirectory() throws Exception {
		assertEquals("", preferences.getDirectory());
		preferences.setDirectory(TestConstants.TEST_DIRECTORY);
		assertEquals(TestConstants.TEST_DIRECTORY, preferences.getDirectory());
	}

	@Test
	void testSetDirectory() throws Exception {
		assertEquals("", preferences.getDirectory());
		preferences.setDirectory(TestConstants.TEST_DIRECTORY);
		assertEquals(TestConstants.TEST_DIRECTORY, preferences.getDirectory());
	}

	@Test
	void testClear() throws Exception {
		preferences.setDirectory(TestConstants.TEST_DIRECTORY);
		assertEquals(TestConstants.TEST_DIRECTORY, preferences.getDirectory());
		preferences.clear();
		assertEquals("", preferences.getDirectory());
	}

	@Test
	void testNullNodeName() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance(null);
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testEmptyNodeName() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance("");
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testBlankNodeName() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance("   ");
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}
}
