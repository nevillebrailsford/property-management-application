package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.time.LocalDateTime;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constants.TestConstants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;

class PropertyMonitorTest {
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);

	PropertyMonitor monitor;
	private LocalDateTime startTest;
	private ListChangeListener<? super Property> listener;
	private static final PostCode postCode1 = new PostCode("CW3 9ST");
	private static final PostCode postCode2 = new PostCode("CW3 9SU");
	private static final String LINE1 = "99 The Street";
	private static final String LINE2 = "The Town";
	private static final String LINE3 = "The County";
	private static final String[] linesOfAddress = new String[] { LINE1, LINE2, LINE3 };
	private static final Address address1 = new Address(postCode1, linesOfAddress);
	private static final Property property1 = new Property(address1);
	private static final Address address2 = new Address(postCode2, linesOfAddress);
	private static final Property property2 = new Property(address2);
	private MonitoredItem testItem;
	ApplicationPreferences applicationPreferences;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try {
			Platform.startup(() -> {
			});
		} catch (IllegalStateException e) {
		}
		preferences.setLevel(Level.OFF);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		preferences.clear();
		Platform.exit();
	}

	@BeforeEach
	void setUp() throws Exception {
		startTest = LocalDateTime.now();
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, startTest, 1, Period.WEEKLY);
		testItem.setOwner(property1);
		applicationPreferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		applicationPreferences.setDirectory(TestConstants.TEST_DIRECTORY);
		LocalStorage.getInstance(new File(applicationPreferences.getDirectory()));
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
		applicationPreferences.clear();
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
	void testGetItems() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getItemsFor(property1).size());
		monitor.addItem(testItem);
		assertEquals(1, monitor.getItemsFor(property1).size());
	}

	@Test
	void testRemoveItem() {
		assertEquals(0, monitor.getProperties().size());
		monitor.addProperty(property1);
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getItemsFor(property1).size());
		monitor.addItem(testItem);
		assertEquals(1, monitor.getItemsFor(property1).size());
		monitor.removeItem(testItem);
		assertEquals(0, monitor.getItemsFor(property1).size());
	}

	@Test
	void testGetOverdueItemsForDate() {
		monitor.addProperty(property1);
		monitor.addItem(testItem);
		assertEquals(1, monitor.getOverdueItemsFor(testItem.getTimeForNextAction()).size());
	}

	@Test
	void testGetNotifiedItemsForDate() {
		monitor.addProperty(property1);
		monitor.addItem(testItem);
		assertEquals(1, monitor.getNotifiedItemsFor(testItem.getTimeForNextNotice()).size());
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
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST already exists",
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
	void testRemoveNullItem() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(null);
		});
		assertEquals("PropertyMonitor: monitoredItem was null", exc.getMessage());
	}

	@Test
	void testRemoveItemWithUnknownProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(testItem);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST was not known",
				exc.getMessage());
	}

	@Test
	void testRemoveUnknownItem() {
		monitor.addProperty(property1);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(testItem);
		});
		assertEquals("Property: item item1 not found", exc.getMessage());
	}

	@Test
	void testRemoveUnknownProperty() {
		monitor.addProperty(property1);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeProperty(property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9SU was not known",
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
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST was not known",
				exc.getMessage());
	}

	@Test
	void testReplaceKnownProperty() {
		monitor.addProperty(property1);
		monitor.addProperty(property2);
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(property1, property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9SU already exists",
				exc.getMessage());
	}

}
