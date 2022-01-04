package com.brailsoft.property.management.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.controller.StatusMonitor;
import com.brailsoft.property.management.model.InventoryItem;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.event.ActionEvent;

public class LoadData extends DataHandler implements Runnable {
	private static final String CLASS_NAME = LoadData.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	public LoadData(File archiveFile) {
		super(archiveFile);
		LOGGER.entering(CLASS_NAME, "init", archiveFile.getAbsolutePath());
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void run() {
		LOGGER.entering(CLASS_NAME, "run");
		StorageLock.readLock().lock();
		LoadingState.startLoading();
		try {
			Document document = loadArchivedData();
			readDataFrom(document);
		} catch (IOException e) {
			LOGGER.warning("caught exception: " + e.getMessage());
		} finally {
			LoadingState.stopLoading();
			StorageLock.readLock().unlock();
			tellListeners(new ActionEvent());
			StatusMonitor.getInstance().update("Initial load of data has completed");
			LOGGER.exiting(CLASS_NAME, "run");
		}
	}

	Document loadArchivedData() throws IOException {
		Document document = null;
		LOGGER.entering(CLASS_NAME, "loadArchivedData");
		try (InputStream archive = new BufferedInputStream(new FileInputStream(archiveFile))) {
			document = readDataFrom(archive);
		} catch (Exception e) {
			LOGGER.warning("caught exception: " + e.getMessage());
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "loadArchivedData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "loadArchivedData");
		}
		return document;
	}

	private Document readDataFrom(InputStream archive) throws IOException {
		Document document = null;
		LOGGER.entering(CLASS_NAME, "readDataFrom");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		try {
			XMLErrorHandler handler = new XMLErrorHandler();
			URL url = LoadData.class.getResource("property.xsd");
			documentBuilderFactory
					.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(url.toExternalForm()) }));
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(handler);
			document = documentBuilder.parse(archive);
			handler.failFast();
			document.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IOException exc = new IOException(e.getMessage());
			LOGGER.throwing(CLASS_NAME, "readDataFrom", exc);
			LOGGER.exiting(CLASS_NAME, "readDataFrom");
			throw exc;
		} catch (SAXException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IOException exc = new IOException(e.getMessage());
			LOGGER.throwing(CLASS_NAME, "readDataFrom", exc);
			LOGGER.exiting(CLASS_NAME, "readDataFrom");
			throw exc;
		}
		LOGGER.exiting(CLASS_NAME, "readDataFrom");
		return document;
	}

	private void readDataFrom(Document document) throws IOException {
		NodeList list = document.getElementsByTagName(Constants.PROPERTY);
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element propertyElement = (Element) node;
				Property property = new Property(propertyElement);
				PropertyMonitor.getInstance().addProperty(property);
				updatePropertyWithMonitoredItems(property, propertyElement);
				updatePropertyWithInventoryItems(property, propertyElement);
			}
		}
	}

	private void updatePropertyWithMonitoredItems(Property property, Element propertyElement) {
		NodeList list = propertyElement.getElementsByTagName(Constants.ITEM);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element itemElement = (Element) node;
				MonitoredItem monitoredItem = new MonitoredItem(itemElement);
				monitoredItem.setOwner(property);
				PropertyMonitor.getInstance().addItem(monitoredItem);
			}
		}
	}

	private void updatePropertyWithInventoryItems(Property property, Element propertyElement) {
		NodeList list = propertyElement.getElementsByTagName(Constants.INVENTORY);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element itemElement = (Element) node;
				InventoryItem inventoryItem = new InventoryItem(itemElement);
				inventoryItem.setOwner(property);
				PropertyMonitor.getInstance().addItem(inventoryItem);
			}
		}
	}

}
