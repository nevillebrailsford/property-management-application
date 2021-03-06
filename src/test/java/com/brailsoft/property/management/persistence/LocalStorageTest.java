package com.brailsoft.property.management.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.model.Address;
import com.brailsoft.property.management.model.InventoryItem;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Period;
import com.brailsoft.property.management.model.PostCode;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;

public class LocalStorageTest {

	private static final String POST_CODE = "CW3 9ST";
	private static final String PROPERTY_DAT = "property.dat";
	ApplicationPreferences applicationPreferences;
	@TempDir
	File rootDirectory;
	LocalStorage storage;
	private String line1 = "99 The Street";
	private String line2 = "The Town";
	private String line3 = "The County";
	private String[] lines = new String[] { line1, line2, line3 };
	private PostCode postcode = new PostCode(POST_CODE);
	private Address address = new Address(postcode, lines);
	private Property property = new Property(address);
	private MonitoredItem testItem;
	private InventoryItem testInventory;
	private LocalDate startTest;
	private Object waitForIO = new Object();
	private StorageListener ioListener = (event) -> {
		synchronized (waitForIO) {
			waitForIO.notifyAll();
		}
	};

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		try {
			Platform.startup(() -> {
			});
		} catch (IllegalStateException e) {
		}
		PropertyManagerLogConfigurer.changeLevel(Level.OFF);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Platform.exit();
	}

	@BeforeEach
	void setUp() throws Exception {
		applicationPreferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		applicationPreferences.setDirectory(rootDirectory.getAbsolutePath());
		rootDirectory = new File(applicationPreferences.getDirectory());
		storage = LocalStorage.getInstance(rootDirectory);
		PropertyMonitor.getInstance().clear();
		startTest = LocalDate.now();
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, startTest, 1, Period.WEEKLY);
		testInventory = new InventoryItem("description1", "manufacturer1", "model1", "serialnumber1", "supplier1",
				LocalDate.now());
		SaveData.addStorageListener(ioListener);
	}

	@AfterEach
	void tearDown() throws Exception {
		PropertyMonitor.getInstance().clear();
		applicationPreferences.clear();
	}

	@Test
	void testGetInstance() {
		assertNotNull(LocalStorage.getInstance(rootDirectory));
	}

	@Test
	void testLoadStoredData() throws IOException, InterruptedException {
		File parent = new File(getTestDirectory());
		assertEquals(0, PropertyMonitor.getInstance().getProperties().size());
		synchronized (waitForIO) {
			LocalStorage.getInstance(parent).loadStoredData();
			waitForIO.wait();
		}
		assertEquals(1, PropertyMonitor.getInstance().getProperties().size());
		Property property = PropertyMonitor.getInstance().getProperties().get(0);
		assertNotNull(property);
		Address address = property.getAddress();
		assertNotNull(address);
		PostCode postcode = address.getPostCode();
		assertNotNull(postcode);
		assertEquals(3, address.getLinesOfAddress().length);
		assertEquals(POST_CODE, postcode.toString());
		assertEquals(1, property.getItems().size());
		assertEquals(1, property.getInventory().size());
	}

	@Test
	void testStoreData() throws IOException, InterruptedException {
		property.addItem(testItem);
		property.addItem(testInventory);
		synchronized (waitForIO) {
			PropertyMonitor.getInstance().addProperty(property);
			waitForIO.wait();
		}
		assertTrue(fileExistsAndIsValid(new File(LocalStorage.getInstance(rootDirectory).getDirectory(), PROPERTY_DAT),
				27));
	}

	@Test
	void testGetDirectory() {
		LocalStorage ls = LocalStorage.getInstance(rootDirectory);
		assertEquals(ls.getDirectory().getAbsolutePath(),
				rootDirectory.getAbsolutePath() + File.separator + LocalStorage.DIRECTORY + File.separator + "model");
	}

	private boolean fileExistsAndIsValid(File file, int expectedCount) {
		int noOfRecords = 0;
		if (file.exists()) {
			try (BufferedReader inputFile = new BufferedReader(new FileReader(file))) {
				do {
					inputFile.readLine();
					noOfRecords++;
				} while (inputFile.ready());
			} catch (Exception e) {
				return false;
			}
		}
		return (noOfRecords == expectedCount);
	}

	private String getTestDirectory() {
		String fileName = getClass().getResource(PROPERTY_DAT).toExternalForm().substring(5);
		File file = new File(fileName);
		File directory = file.getParentFile();
		return directory.getAbsolutePath();
	}

}
