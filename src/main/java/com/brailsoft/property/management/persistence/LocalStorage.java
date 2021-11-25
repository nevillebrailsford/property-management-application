package com.brailsoft.property.management.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

public class LocalStorage {
	private static final String CLASS_NAME = LocalStorage.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	public static final String DIRECTORY = "property.management";
	public static final String FILE_NAME = "property.dat";

	private static LocalStorage instance = null;

	private File directory = null;
	private boolean loadingData = false;

	public synchronized static LocalStorage getInstance(File rootDirectory) {
		if (rootDirectory == null) {
			throw new IllegalArgumentException("LocalStorage: rootDirectory was null");
		}
		if (instance == null) {
			instance = new LocalStorage();
			instance.updateDirectory(new File(rootDirectory, DIRECTORY));
		} else {
			instance.updateDirectory(new File(rootDirectory, DIRECTORY));
		}
		return instance;
	}

	private LocalStorage() {
	}

	private void updateDirectory(File directory) {
		LOGGER.entering(CLASS_NAME, "updateDirectory", directory);
		this.directory = directory;
		if (!directory.exists()) {
			directory.mkdirs();
		}
		LOGGER.exiting(CLASS_NAME, "updateDirectory");
	}

	public void loadArchivedData() throws IOException {
		LOGGER.entering(CLASS_NAME, "loadArchivedData");
		File archiveFile = new File(directory, FILE_NAME);
		if (!archiveFile.exists()) {
			IOException exc = new IOException(
					"LocalStorage: archiveFile " + archiveFile.getAbsolutePath() + " not found");
			LOGGER.throwing(CLASS_NAME, "loadArchivedData", exc);
			LOGGER.exiting(CLASS_NAME, "loadArchivedData");
			throw exc;
		}
		try (InputStream archive = new BufferedInputStream(new FileInputStream(archiveFile))) {
			loadingData = true;
			readDataFrom(archive);
		} catch (Exception e) {
			LOGGER.warning("caught exception: " + e.getMessage());
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "loadArchivedData", exc);
			throw exc;
		} finally {
			loadingData = false;
			LOGGER.exiting(CLASS_NAME, "loadArchivedData");

		}
	}

	public void saveArchiveData() throws IOException {
		LOGGER.entering(CLASS_NAME, "saveArchivedData");
		if (loadingData) {
			LOGGER.exiting(CLASS_NAME, "saveArchiveData", loadingData);
			return;
		}
		File archiveFile = new File(directory, FILE_NAME);
		try (OutputStream archive = new BufferedOutputStream(new FileOutputStream(archiveFile))) {
			writeDataTo(archive);
		} catch (Exception e) {
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "saveArchivedData", exc);
			LOGGER.exiting(CLASS_NAME, "saveArchivedData");
			throw exc;
		}
		LOGGER.exiting(CLASS_NAME, "saveArchivedData");
	}

	public File getDirectory() {
		return directory;
	}

	private void readDataFrom(InputStream archive) throws IOException {
		LOGGER.entering(CLASS_NAME, "readDataFrom");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(archive);
			document.getDocumentElement().normalize();
			readDataFrom(document);
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
	}

	private void writeDataTo(OutputStream archive) throws IOException {
		LOGGER.entering(CLASS_NAME, "writeDataTo");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			writeDataTo(document);
			writeXML(document, archive);
		} catch (ParserConfigurationException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IOException exc = new IOException(e.getMessage());
			LOGGER.throwing(CLASS_NAME, "readDataFrom", exc);
			LOGGER.exiting(CLASS_NAME, "readDataFrom");
			throw exc;
		}
		LOGGER.exiting(CLASS_NAME, "writeDataTo");
	}

	private void writeDataTo(Document document) {
		Element rootElement = document.createElement(Constants.PROPERTIES);
		document.appendChild(rootElement);
		PropertyMonitor.getInstance().getProperties().stream().forEach(property -> {
			Element propertyElement = buildElementFor(property, document);
			rootElement.appendChild(propertyElement);
		});
	}

	private Element buildElementFor(Property property, Document document) {
		Element propertyElement = property.buildElement(document);
		for (int index = 0; index < property.getItems().size(); index++) {
			propertyElement.appendChild(property.getItems().get(index).buildElement(document));
		}
		return propertyElement;
	}

	private void writeXML(Document doc, OutputStream output) throws IOException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException(e.getMessage());
		}
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

}
