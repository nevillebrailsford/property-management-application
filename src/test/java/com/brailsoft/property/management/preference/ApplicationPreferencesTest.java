package com.brailsoft.property.management.preference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;

class ApplicationPreferencesTest {

	private ApplicationPreferences preferences;

	@BeforeEach
	void setUp() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME).clear();
		preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	}

	@AfterEach
	void tearDown() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME).clear();
	}

	@Test
	void testGetInstance() {
		assertNotNull(preferences);
		ApplicationPreferences ap = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
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
	void testGetLevel() throws Exception {
		preferences.setLevel(Level.WARNING);
		assertEquals(Level.WARNING, preferences.getLevel());
	}

	@Test
	void testSetLevel() throws Exception {
		preferences.setLevel(Level.WARNING);
		assertEquals(Level.WARNING, preferences.getLevel());
		preferences.setLevel(Level.SEVERE);
		assertEquals(Level.SEVERE, preferences.getLevel());
	}

	@Test
	void testNullNodeName() throws Exception {
		ApplicationPreferences.getInstance().clear();
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance(new String[] {});
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testNullNodeNameEntry() throws Exception {
		ApplicationPreferences.getInstance().clear();
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance(new String[] { null });
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testTooManyNodeNames() throws Exception {
		ApplicationPreferences.getInstance().clear();
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance("", "");
		});
		assertEquals("ApplicationPreferences: more than 1 nodeName was specified", exc.getMessage());
	}

	@Test
	void testEmptyNodeName() throws Exception {
		ApplicationPreferences.getInstance().clear();
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance("");
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testBlankNodeName() throws Exception {
		ApplicationPreferences.getInstance().clear();
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			ApplicationPreferences.getInstance("   ");
		});
		assertEquals("ApplicationPreferences: nodeName was null", exc.getMessage());
	}

	@Test
	void testGetNoLevel() {
		assertEquals(Level.WARNING, preferences.getLevel());
	}

	@Test
	void testSetNoLevel() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			preferences.setLevel(null);
		});
		assertEquals("ApplicationPreferences: level was null", exc.getMessage());
	}

}
