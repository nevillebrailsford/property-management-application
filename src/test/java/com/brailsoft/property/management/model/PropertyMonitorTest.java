package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.time.LocalDate;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.persistence.SaveData;
import com.brailsoft.property.management.persistence.StorageListener;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;

class PropertyMonitorTest {

	@TempDir
	File rootDirectory;
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);

	PropertyMonitor monitor;
	private LocalDate startTest;
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
	private MonitoredItem overdueItem;
	private MonitoredItem noticeDueItem;
	private InventoryItem testInventory;
	ApplicationPreferences applicationPreferences;
	private Object waitForIO = new Object();
	private StorageListener ioListener = (event) -> {
		synchronized (waitForIO) {
			waitForIO.notifyAll();
		}
	};

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
		startTest = LocalDate.now();
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, startTest, 1, Period.WEEKLY);
		testItem.setOwner(property1);
		testInventory = new InventoryItem("inventory1", "manufacturer1", "model1", "serialnumber1", "supplier1",
				LocalDate.now());
		testInventory.setOwner(property1);
		applicationPreferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		applicationPreferences.setDirectory(rootDirectory.getAbsolutePath());
		LocalStorage.getInstance(new File(applicationPreferences.getDirectory()));
		monitor = PropertyMonitor.getInstance();
		Thread.sleep(100);
		listener = new ListChangeListener<>() {

			@Override
			public void onChanged(Change<? extends Property> c) {
				System.out.print(c);
			}
		};
		SaveData.addStorageListener(ioListener);
	}

	@AfterEach
	void tearDown() throws Exception {
		PropertyMonitor.getInstance().clear();
		SaveData.removeStorageListener(ioListener);
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
	void testAddProperty() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
	}

	@Test
	void testReplaceProperty() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.replaceProperty(property1, property2);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
	}

	@Test
	void testRemoveProperty() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.removeProperty(property1);
			waitForIO.wait();
		}
		assertEquals(0, monitor.getProperties().size());
	}

	@Test
	void testGetProperties() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		assertEquals(property1, monitor.getProperties().get(0));
	}

	@Test
	void testGetPropertiesWithOverdueItems() throws InterruptedException {
		assertEquals(0, monitor.getPropertiesWithOverdueItems().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
		assertEquals(0, monitor.getPropertiesWithOverdueItems().size());
		overdueItem = new MonitoredItem("item2", Period.YEARLY, 1, startTest.minusYears(1).minusDays(1), 1,
				Period.WEEKLY);
		overdueItem.setOwner(property1);
		synchronized (waitForIO) {
			monitor.addItem(overdueItem);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getPropertiesWithOverdueItems().size());
	}

	@Test
	void testGetPropertiesWithOverdueNotices() throws InterruptedException {
		assertEquals(0, monitor.getPropertiesWithOverdueNotices().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
		assertEquals(0, monitor.getPropertiesWithOverdueNotices().size());
		noticeDueItem = new MonitoredItem("item2", Period.YEARLY, 1, startTest.minusYears(1), 1, Period.WEEKLY);
		noticeDueItem.setOwner(property1);
		synchronized (waitForIO) {
			monitor.addItem(noticeDueItem);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getPropertiesWithOverdueNotices().size());
		assertEquals(0, monitor.getPropertiesWithOverdueItems().size());
	}

	@Test
	void testGetItems() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getItemsFor(property1).size());
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getItemsFor(property1).size());
	}

	@Test
	void testGetInventory() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getItemsFor(property1).size());
		synchronized (waitForIO) {
			monitor.addItem(testInventory);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getInventoryFor(property1).size());
	}

	@Test
	void testRemoveItem() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getItemsFor(property1).size());
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getItemsFor(property1).size());
		synchronized (waitForIO) {
			monitor.removeItem(testItem);
			waitForIO.wait();
		}
		assertEquals(0, monitor.getItemsFor(property1).size());
	}

	@Test
	void testRemoveInventory() throws InterruptedException {
		assertEquals(0, monitor.getProperties().size());
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getProperties().size());
		assertEquals(0, monitor.getInventoryFor(property1).size());
		synchronized (waitForIO) {
			monitor.addItem(testInventory);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getInventoryFor(property1).size());
		synchronized (waitForIO) {
			monitor.removeItem(testInventory);
			waitForIO.wait();
		}
		assertEquals(0, monitor.getInventoryFor(property1).size());
	}

	@Test
	void testGetOverdueItemsForDate() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
		assertEquals(1, monitor.getOverdueItemsFor(testItem.getTimeForNextAction()).size());
	}

	@Test
	void testGetNotifiedItemsForDate() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		synchronized (waitForIO) {
			monitor.addItem(testItem);
			waitForIO.wait();
		}
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
	void testAddDUplicateProperty() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
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
			MonitoredItem missing = null;
			monitor.removeItem(missing);
		});
		assertEquals("PropertyMonitor: monitoredItem was null", exc.getMessage());
	}

	@Test
	void testRemoveNullInventory() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			InventoryItem missing = null;
			monitor.removeItem(missing);
		});
		assertEquals("PropertyMonitor: inventoryItem was null", exc.getMessage());
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
	void testRemoveInventoryWithUnknownProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(testInventory);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST was not known",
				exc.getMessage());
	}

	@Test
	void testRemoveUnknownItem() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(testItem);
		});
		assertEquals("Property: item item1 not found", exc.getMessage());
	}

	@Test
	void testRemoveUnknownInventory() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.removeItem(testInventory);
		});
		assertEquals("Property: item inventory1, manufacturer1, model1, serialnumber1 not found", exc.getMessage());
	}

	@Test
	void testRemoveUnknownProperty() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
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
	void testReplaceKnownProperty() throws InterruptedException {
		synchronized (waitForIO) {
			monitor.addProperty(property1);
			waitForIO.wait();
		}
		synchronized (waitForIO) {
			monitor.addProperty(property2);
			waitForIO.wait();
		}
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.replaceProperty(property1, property2);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9SU already exists",
				exc.getMessage());
	}

	@Test
	void testGetItemsForNullProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.getItemsFor(null);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testGetItemsForUnknownProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.getItemsFor(property1);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST not found",
				exc.getMessage());
	}

	@Test
	void testGetInventoryForNullProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.getInventoryFor(null);
		});
		assertEquals("PropertyMonitor: property was null", exc.getMessage());
	}

	@Test
	void testGetInventoryForUnknownProperty() {
		Exception exc = assertThrows(IllegalArgumentException.class, () -> {
			monitor.getInventoryFor(property1);
		});
		assertEquals("PropertyMonitor: property 99 The Street, The Town, The County CW3 9ST not found",
				exc.getMessage());
	}

}
