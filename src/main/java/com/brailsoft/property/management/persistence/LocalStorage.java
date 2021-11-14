package com.brailsoft.property.management.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
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
import com.brailsoft.property.management.model.Address;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Period;
import com.brailsoft.property.management.model.PostCode;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

public class LocalStorage {
	private static final String CLASS_NAME = LocalStorage.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	public static final String DIRECTORY = "property.management";
	public static final String FILE_NAME = "property.dat";
	private static final String PROPERTIES = "properties";
	private static final String PROPERTY = "property";
	private static final String ADDRESS = "address";
	private static final String POSTCODE = "postcode";
	private static final String LINE = "line";
	private static final String ITEM = "item";
	private static final String DESCRIPTION = "description";
	private static final String PERIOD_FOR_NEXT_ACTION = "periodfornextaction";
	private static final String NOTICE_EVERY = "noticeEvery";
	private static final String LAST_ACTIONED = "lastActioned";
	private static final String ADVANCE_NOTICE = "advanceNotice";
	private static final String PERIOD_FOR_NEXT_NOTICE = "periodForNextNotice";

	private static LocalStorage instance = null;

	private File directory = null;

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
			LOGGER.entering(CLASS_NAME, "loadArchivedData");
			throw exc;
		}
		try (InputStream archive = new BufferedInputStream(new FileInputStream(archiveFile))) {
			readDataFrom(archive);
		} catch (Exception e) {
			LOGGER.warning("caught exception: " + e.getMessage());
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "loadArchivedData", exc);
			LOGGER.entering(CLASS_NAME, "loadArchivedData");
			throw exc;
		}
		LOGGER.entering(CLASS_NAME, "loadArchivedData");
	}

	public void saveArchiveData() throws IOException {
		LOGGER.entering(CLASS_NAME, "saveArchivedData");
		File archiveFile = new File(directory, FILE_NAME);
		try (OutputStream archive = new BufferedOutputStream(new FileOutputStream(archiveFile))) {
			writeDataTo(archive);
		} catch (Exception e) {
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "saveArchivedData", exc);
			LOGGER.entering(CLASS_NAME, "saveArchivedData");
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
		Element rootElement = document.createElement(PROPERTIES);
		document.appendChild(rootElement);
		PropertyMonitor.getInstance().getProperties().stream().forEach(property -> {
			Element propertyElement = buildElementFor(property, document);
			rootElement.appendChild(propertyElement);
		});
	}

	private Element buildElementFor(Property property, Document document) {
		Element propertyElement = document.createElement(PROPERTY);
		propertyElement.appendChild(buildElementFor(property.getAddress(), document));
		for (int index = 0; index < property.getItems().size(); index++) {
			propertyElement.appendChild(buildElementFor(property.getItems().get(index), document));
		}
		return propertyElement;
	}

	private Element buildElementFor(Address address, Document document) {
		Element addressElement = document.createElement(ADDRESS);
		addressElement.appendChild(buildElement(POSTCODE, address.getPostCode().toString(), document));
		for (int index = 0; index < address.getLinesOfAddress().length; index++) {
			addressElement.appendChild(buildElement(LINE, address.getLinesOfAddress()[index], document));
		}
		return addressElement;
	}

	private Element buildElementFor(MonitoredItem item, Document document) {
		Element itemElement = document.createElement(ITEM);
		itemElement.appendChild(buildElement(DESCRIPTION, item.getDescription(), document));
		itemElement
				.appendChild(buildElement(PERIOD_FOR_NEXT_ACTION, item.getPeriodForNextAction().toString(), document));
		itemElement.appendChild(buildElement(NOTICE_EVERY, Integer.toString(item.getNoticeEvery()), document));
		itemElement.appendChild(buildElement(LAST_ACTIONED, item.getLastActionPerformed().toString(), document));
		itemElement.appendChild(buildElement(ADVANCE_NOTICE, Integer.toString(item.getAdvanceNotice()), document));
		itemElement
				.appendChild(buildElement(PERIOD_FOR_NEXT_NOTICE, item.getPeriodForNextNotice().toString(), document));
		return itemElement;
	}

	private Element buildElement(String tag, String text, Document document) {
		Element result = document.createElement(tag);
		result.setTextContent(text);
		return result;
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
		NodeList list = document.getElementsByTagName(PROPERTY);
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element propertyElement = (Element) node;
				Property property = readDataFrom(propertyElement);
				PropertyMonitor.getInstance().addProperty(property);
				updatePropertyWithMonitoredItems(property, propertyElement);
			}
		}
	}

	private Property readDataFrom(Element propertyElement) {
		Address address = buildAddressFrom((Element) propertyElement.getElementsByTagName(ADDRESS).item(0));
		Property property = new Property(address);
		return property;
	}

	private Address buildAddressFrom(Element addressElement) {
		String postcode = addressElement.getElementsByTagName(POSTCODE).item(0).getTextContent();
		NodeList list = addressElement.getElementsByTagName(LINE);
		String[] linesOfAddress = new String[list.getLength()];
		for (int index = 0; index < list.getLength(); index++) {
			linesOfAddress[index] = list.item(index).getTextContent();
		}
		return new Address(new PostCode(postcode), linesOfAddress);
	}

	private void updatePropertyWithMonitoredItems(Property property, Element propertyElement) {
		NodeList list = propertyElement.getElementsByTagName(ITEM);
		if (list == null || list.getLength() == 0) {
			return;
		}
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element itemElement = (Element) node;
				MonitoredItem monitoredItem = buildMonitoredItemFrom(itemElement);
				monitoredItem.setOwner(property);
				PropertyMonitor.getInstance().addItem(monitoredItem);
			}
		}
	}

	private MonitoredItem buildMonitoredItemFrom(Element itemElement) {
		String description = itemElement.getElementsByTagName(DESCRIPTION).item(0).getTextContent();
		String periodForNextAction = itemElement.getElementsByTagName(PERIOD_FOR_NEXT_ACTION).item(0).getTextContent();
		String noticeEvery = itemElement.getElementsByTagName(NOTICE_EVERY).item(0).getTextContent();
		String lastActioned = itemElement.getElementsByTagName(LAST_ACTIONED).item(0).getTextContent();
		String advanceNotice = itemElement.getElementsByTagName(ADVANCE_NOTICE).item(0).getTextContent();
		String periodForNextNotice = itemElement.getElementsByTagName(PERIOD_FOR_NEXT_NOTICE).item(0).getTextContent();
		LocalDate time = LocalDate.parse(lastActioned);
		return new MonitoredItem(description, Period.valueOf(periodForNextAction), Integer.parseInt(noticeEvery), time,
				Integer.parseInt(advanceNotice), Period.valueOf(periodForNextNotice));
	}
}
