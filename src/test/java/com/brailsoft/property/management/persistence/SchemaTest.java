package com.brailsoft.property.management.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;

class SchemaTest {
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);

	File archiveFile;
	File invalidPostCode;
	File duplicateAddress;
	File invalidDate;
	File invalidPeriod;
	File missingAddress;
	File missingDescription;
	File propertyComplete;
	File propertyMultipleItems;
	File propertyMultipleInventory;
	File propertyEmpty;
	File noProperty;
	File multiProperty;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try {
			Platform.startup(() -> {
			});
		} catch (IllegalStateException e) {
		}
		preferences.setLevel(Level.OFF);
		PropertyManagerLogConfigurer.setUp();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		PropertyManagerLogConfigurer.shutdown();
		preferences.clear();
		Platform.exit();
	}

	@BeforeEach
	void setUp() throws Exception {
		String url = SchemaTest.class.getResource("property.dat").toExternalForm().substring(6);
		archiveFile = new File(url);
		url = SchemaTest.class.getResource("propertyComplete.dat").toExternalForm().substring(6);
		propertyComplete = new File(url);
		url = SchemaTest.class.getResource("propertyMultipleItems.dat").toExternalForm().substring(6);
		propertyMultipleItems = new File(url);
		url = SchemaTest.class.getResource("propertyMultipleInventory.dat").toExternalForm().substring(6);
		propertyMultipleInventory = new File(url);
		url = SchemaTest.class.getResource("propertyEmpty.dat").toExternalForm().substring(6);
		propertyEmpty = new File(url);
		url = SchemaTest.class.getResource("noProperty.dat").toExternalForm().substring(6);
		noProperty = new File(url);
		url = SchemaTest.class.getResource("multiProperty.dat").toExternalForm().substring(6);
		multiProperty = new File(url);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGoodLoad() throws IOException {
		assertTrue(archiveFile.exists());
		LoadData loader = new LoadData(archiveFile);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentIsValidStructure() throws IOException {
		LoadData loader = new LoadData(archiveFile);
		Document document = loader.loadStoredData();
		NodeList list = document.getElementsByTagName("property");
		assertEquals(1, list.getLength());
		Element propertyElement = (Element) list.item(0);
		NodeList addrList = propertyElement.getElementsByTagName("address");
		assertEquals(1, addrList.getLength());
		Element addressElement = (Element) addrList.item(0);
		assertEquals(1, addressElement.getElementsByTagName("postcode").getLength());
		assertEquals(3, addressElement.getElementsByTagName("line").getLength());
		NodeList itemList = propertyElement.getElementsByTagName("item");
		assertEquals(1, itemList.getLength());
		Element itemElement = (Element) itemList.item(0);
		assertEquals(1, itemElement.getElementsByTagName("description").getLength());
		assertEquals(1, itemElement.getElementsByTagName("periodfornextaction").getLength());
		assertEquals(1, itemElement.getElementsByTagName("noticeEvery").getLength());
		assertEquals(1, itemElement.getElementsByTagName("lastActioned").getLength());
		assertEquals(1, itemElement.getElementsByTagName("advanceNotice").getLength());
		assertEquals(1, itemElement.getElementsByTagName("periodForNextNotice").getLength());
		NodeList inventoryList = propertyElement.getElementsByTagName("inventory");
		assertEquals(0, inventoryList.getLength());
	}

	@Test
	void testDocumentWithItemAndInventory() throws IOException {
		assertTrue(propertyComplete.exists());
		LoadData loader = new LoadData(propertyComplete);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentWithMultipleItemsAndInventory() throws IOException {
		assertTrue(propertyMultipleItems.exists());
		LoadData loader = new LoadData(propertyMultipleItems);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentWithMultipleItemsAndMultipleInventory() throws IOException {
		assertTrue(propertyMultipleInventory.exists());
		LoadData loader = new LoadData(propertyMultipleInventory);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentWithNoItemsOrInventory() throws IOException {
		assertTrue(propertyEmpty.exists());
		LoadData loader = new LoadData(propertyEmpty);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentWithNoProperties() throws IOException {
		assertTrue(noProperty.exists());
		LoadData loader = new LoadData(noProperty);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
	}

	@Test
	void testDocumentWithTwoProperties() throws IOException {
		assertTrue(multiProperty.exists());
		LoadData loader = new LoadData(multiProperty);
		assertNotNull(loader);
		Document document = loader.loadStoredData();
		assertNotNull(document);
		NodeList list = document.getElementsByTagName("property");
		assertEquals(2, list.getLength());
	}

	@Test
	void testDocumentWithInvalidPostCode() {
		String url = SchemaTest.class.getResource("invalidPostCode.dat").toExternalForm().substring(6);
		invalidPostCode = new File(url);
		LoadData loader = new LoadData(invalidPostCode);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("'postcode' is not valid."));
	}

	@Test
	void testDocumentWithDuplicateAddress() {
		String url = SchemaTest.class.getResource("duplicateAddress.dat").toExternalForm().substring(6);
		duplicateAddress = new File(url);
		LoadData loader = new LoadData(duplicateAddress);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("'address'. One of '{item, inventory}' is expected."));
	}

	@Test
	void testDocumentWithInvalidDate() {
		String url = SchemaTest.class.getResource("invalidDate.dat").toExternalForm().substring(6);
		invalidDate = new File(url);
		LoadData loader = new LoadData(invalidDate);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("'lastActioned' is not valid."));
	}

	@Test
	void testDocumentWithInvalidPeriod() {
		String url = SchemaTest.class.getResource("invalidPeriod.dat").toExternalForm().substring(6);
		invalidPeriod = new File(url);
		LoadData loader = new LoadData(invalidPeriod);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("'periodForNextNotice' is not valid."));
	}

	@Test
	void testDocumentWithMissingAddress() {
		String url = SchemaTest.class.getResource("missingAddress.dat").toExternalForm().substring(6);
		missingAddress = new File(url);
		LoadData loader = new LoadData(missingAddress);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("One of '{address}' is expected."));
	}

	@Test
	void testDocumentWithMissingDescription() {
		String url = SchemaTest.class.getResource("missingDescription.dat").toExternalForm().substring(6);
		missingDescription = new File(url);
		LoadData loader = new LoadData(missingDescription);
		Exception exc = assertThrows(IOException.class, () -> {
			loader.loadStoredData();
		});
		assertTrue(exc.getMessage().endsWith("One of '{description}' is expected."));
	}
}
