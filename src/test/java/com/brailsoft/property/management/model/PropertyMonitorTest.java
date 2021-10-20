package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;

class PropertyMonitorTest {

	PropertyMonitor monitor;
	private ListChangeListener<? super Property> listener;
	private static final PostCode postCode1 = new PostCode("CW3 9ST");
	private static final PostCode postCode2 = new PostCode("CW3 9SU");
	private static final String LINE1 = "99 The Street";
	private static final String LINE2 = "The Town";
	private static final String LINE3 = "The County";
	private static final String LINE4 = "Country";
	private static final String[] linesOfAddress = new String[] { LINE1, LINE2, LINE3, LINE4 };
	private static final Address address1 = new Address(postCode1, linesOfAddress);
	private static final Property property1 = new Property(address1);
	private static final Address address2 = new Address(postCode2, linesOfAddress);
	private static final Property property2 = new Property(address2);

	@BeforeEach
	void setUp() throws Exception {
		monitor = PropertyMonitor.getInstance();
		listener = new ListChangeListener<>() {

			@Override
			public void onChanged(Change<? extends Property> c) {
				System.out.print(c);
			}
		};
	}

	@AfterEach
	void tearDown() throws Exception {
		PropertyMonitor.getInstance().clear();
		PropertyMonitor.getInstance().removeListener(listener);
	}

	@Test
	void testGetInstance() {
		assertNotNull(PropertyMonitor.getInstance());
	}

	@Test
	void testAddListener() {
		monitor.addListener(listener);
	}

	@Test
	void testRemoveListener() {
		monitor.removeListener(listener);
	}

	@Test
	void testAddProperty() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
	}

	@Test
	void testReplaceProperty() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
		monitor.replaceProperty(property1, property2);
		assertEquals(1, monitor.getProperties().size());
	}

	@Test
	void testRemoveProperty() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
		monitor.removeProperty(property1);
		assertEquals(0, monitor.getProperties().size());
	}

	@Test
	void testGetProperties() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
		assertEquals(property1, monitor.getProperties().get(0));
	}

	@Test
	void testGetPropertiesWithOverdueItems() {
		assertEquals(0, monitor.getPropertiesWithOverdueItems().size());
	}

	@Test
	void testGetPropertiesWithOverdueNotices() {
		assertEquals(0, monitor.getPropertiesWithOverdueNotices().size());
	}

	@Test
	void testAddNullListener() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.addListener(null);
		});
		assertEquals("PropertyMonitor: listener was null", exc.getMessage());
	}

	@Test
	void testRemoveNullListener() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeListener(null);
		});
		assertEquals("PropertyMonitor: listener was null", exc.getMessage());
	}

	@Test
	void testAddNullProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.addProperty(null);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testAddDUplicateProperty() {
		monitor.addProperty(property1);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.addProperty(property1);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County, Country CW3 9ST already exists",
				exc.getMessage());
	}

	@Test
	void testRemoveNullProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeProperty(null);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testRemoveUnknownProperty() {
		monitor.addProperty(property1);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeProperty(property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County, Country CW3 9SU was not known",
				exc.getMessage());
	}

	@Test
	void testReplaceNullOldProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(null, property2);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testReplaceNullNewProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(property2, null);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testReplaceUnknownProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(property1, property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County, Country CW3 9ST was not known",
				exc.getMessage());
	}

	@Test
	void testReplaceKnownProperty() {
		monitor.addProperty(property1);
		monitor.addProperty(property2);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(property1, property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County, Country CW3 9SU already exists",
				exc.getMessage());
	}

}
