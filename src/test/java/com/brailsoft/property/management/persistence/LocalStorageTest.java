package com.brailsoft.property.management.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.model.Address;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Period;
import com.brailsoft.property.management.model.PostCode;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

public class LocalStorageTest {

	private static final String POST_CODE = "CW3 9ST";
	private static final String TEST_DIRECTORY = "property.test";
	private static final String PROPERTY_DAT = "property.dat";
	public static File directory = new File(System.getProperty("user.home"), TEST_DIRECTORY);
	LocalStorage storage = LocalStorage.getInstance(directory);
	private String line1 = "99 The Street";
	private String line2 = "The Town";
	private String line3 = "The County";
	private String[] lines = new String[] { line1, line2, line3 };
	private PostCode postcode = new PostCode(POST_CODE);
	private Address address = new Address(postcode, lines);
	private Property property = new Property(address);
	private MonitoredItem testItem;
	private LocalDateTime startTest;

	@BeforeEach
	void setUp() throws Exception {
		LocalStorage.getInstance(directory);
		PropertyMonitor.getInstance().clear();
		startTest = LocalDateTime.now();
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, startTest, 1, Period.WEEKLY);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.deleteIfExists(Paths.get(directory.getAbsolutePath(), PROPERTY_DAT));
		Files.deleteIfExists(Paths.get(directory.getAbsolutePath()));
		PropertyMonitor.getInstance().clear();
	}

	@Test
	void testGetInstance() {
		assertNotNull(LocalStorage.getInstance(directory));
	}

	@Test
	void testLoadArchivedData() throws IOException {
		File parent = new File(getTestDirectory());
		assertEquals(0, PropertyMonitor.getInstance().getProperties().size());
		LocalStorage.getInstance(parent).loadArchivedData();
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
	}

	@Test
	void testSaveArchiveData() throws IOException {
		property.addItem(testItem);
		PropertyMonitor.getInstance().addProperty(property);
//		LocalStorage.getInstance(directory).saveArchiveData();
//		assertTrue(fileExistsAndIsValid(new File(directory, PROPERTY_DAT), 19));
	}

	@Test
	void testGetDirectory() {
		LocalStorage ls = LocalStorage.getInstance(directory);
		assertEquals(ls.getDirectory().getAbsolutePath(), directory.getAbsolutePath());
		File parent = new File(getTestDirectory());
		LocalStorage.getInstance(parent);
		assertEquals(ls.getDirectory().getAbsolutePath(), parent.getAbsolutePath());
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
