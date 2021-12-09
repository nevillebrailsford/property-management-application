package com.brailsoft.property.management.persistence;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.event.ActionEvent;

public class SaveData extends DataHandler implements Runnable {

	private static final String CLASS_NAME = SaveData.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	public SaveData(File archiveFile) {
		super(archiveFile);
		LOGGER.entering(CLASS_NAME, "init", archiveFile.getAbsolutePath());
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void run() {
		LOGGER.entering(CLASS_NAME, "run");
		StorageLock.readLock().lock();
		boolean loading = LoadingState.isLoading();
		if (loading) {
			LOGGER.exiting(CLASS_NAME, "run", loading);
			StorageLock.readLock().unlock();
			return;
		}
		StorageLock.readLock().unlock();
		StorageLock.writeLock().lock();
		try {
			saveArchiveData();
		} catch (IOException e) {
			LOGGER.warning("caught exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			StorageLock.writeLock().unlock();
			tellListeners(new ActionEvent());
			LOGGER.exiting(CLASS_NAME, "run");
		}
	}

	private void saveArchiveData() throws IOException {
		LOGGER.entering(CLASS_NAME, "saveArchivedData");
		try (OutputStream archive = new BufferedOutputStream(new FileOutputStream(archiveFile))) {
			writeDataTo(archive);
		} catch (Exception e) {
			e.printStackTrace();
			IOException exc = new IOException("LocalStorage: Exception occurred - " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "saveArchivedData", exc);
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "saveArchivedData");
		}
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
			throw exc;
		} finally {
			LOGGER.exiting(CLASS_NAME, "writeDataTo");
		}
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
		for (int index = 0; index < property.getInventory().size(); index++) {
			propertyElement.appendChild(property.getInventory().get(index).buildElement(document));
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

}
