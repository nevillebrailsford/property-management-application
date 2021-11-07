package com.brailsoft.property.management.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.brailsoft.property.management.audit.AuditObject;
import com.brailsoft.property.management.audit.AuditRecord;
import com.brailsoft.property.management.audit.AuditType;
import com.brailsoft.property.management.audit.AuditWriter;
import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;
import com.brailsoft.property.management.timer.Timer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PropertyMonitor {
	private static final String CLASS_NAME = PropertyMonitor.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private static PropertyMonitor instance = null;

	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);

	private Timer timer;

	private final ObservableList<Property> properties;

	public synchronized static PropertyMonitor getInstance() {
		if (instance == null) {
			instance = new PropertyMonitor();
			instance.timer.addListener((event) -> {
				instance.timerPopped(event);
			});
		}
		return instance;
	}

	private PropertyMonitor() {
		properties = FXCollections.observableArrayList();
		timer = Timer.getInstance();
	}

	public synchronized void addListener(ListChangeListener<? super Property> listener) {
		if (listener == null) {
			throw new IllegalArgumentException("PropertyMonitor: listener was null");
		}
		properties.addListener(listener);
	}

	public synchronized void removeListener(ListChangeListener<? super Property> listener) {
		if (listener == null) {
			throw new IllegalArgumentException("PropertyMonitor: listener was null");
		}
		properties.removeListener(listener);
	}

	public synchronized void addListener(ListChangeListener<? super MonitoredItem> listener, Property property) {
		if (listener == null) {
			throw new IllegalArgumentException("PropertyMonitor: listener was null");
		}
		findProperty(property).addListener(listener);
	}

	public synchronized void removeListener(ListChangeListener<? super MonitoredItem> listener, Property property) {
		if (listener == null) {
			throw new IllegalArgumentException("PropertyMonitor: listener was null");
		}
		findProperty(property).removeListener(listener);
	}

	public synchronized void clear() {
		LOGGER.entering(CLASS_NAME, "clear");
		auditBeforeClearing();
		properties.clear();
		LOGGER.exiting(CLASS_NAME, "clear");
	}

	public synchronized void addProperty(Property newProperty) {
		LOGGER.entering(CLASS_NAME, "addProperty", newProperty);
		if (newProperty == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "addProperty", exc);
			LOGGER.exiting(CLASS_NAME, "addProperty");
			throw exc;
		}
		if (properties.contains(newProperty)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + newProperty + " already exists");
			LOGGER.throwing(CLASS_NAME, "addProperty", exc);
			LOGGER.exiting(CLASS_NAME, "addProperty");
			throw exc;
		}
		properties.add(new Property(newProperty));
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "addProperty");
	}

	public synchronized void replaceProperty(Property oldProperty, Property newProperty) {
		LOGGER.entering(CLASS_NAME, "replaceProperty", new Object[] { oldProperty, newProperty });
		if (oldProperty == null || newProperty == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "replaceProperty", exc);
			LOGGER.exiting(CLASS_NAME, "replaceProperty");
			throw exc;
		}
		if (!properties.contains(oldProperty)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + oldProperty + " was not known");
			LOGGER.throwing(CLASS_NAME, "replaceProperty", exc);
			LOGGER.exiting(CLASS_NAME, "replaceProperty");
			throw exc;
		}
		if (properties.contains(newProperty)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + newProperty + " already exists");
			LOGGER.throwing(CLASS_NAME, "replaceProperty", exc);
			LOGGER.exiting(CLASS_NAME, "replaceProperty");
			throw exc;
		}
		properties.remove(oldProperty);
		properties.add(newProperty);
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "replaceProperty");
	}

	public synchronized void removeProperty(Property oldProperty) {
		LOGGER.entering(CLASS_NAME, "removeProperty", oldProperty);
		if (oldProperty == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "removeProperty", exc);
			LOGGER.exiting(CLASS_NAME, "removeProperty");
			throw exc;
		}
		if (!properties.contains(oldProperty)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + oldProperty + " was not known");
			LOGGER.throwing(CLASS_NAME, "removeProperty", exc);
			LOGGER.exiting(CLASS_NAME, "removeProperty");
			throw exc;
		}
		properties.remove(oldProperty);
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "removeProperty");
	}

	public synchronized void addItem(MonitoredItem monitoredItem) {
		LOGGER.entering(CLASS_NAME, "addItem", monitoredItem);
		if (monitoredItem == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
			LOGGER.throwing(CLASS_NAME, "addItem", exc);
			LOGGER.exiting(CLASS_NAME, "addItem");
			throw exc;
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "addItem", exc);
			LOGGER.exiting(CLASS_NAME, "addItem");
			throw exc;
		}
		if (!properties.contains(property)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " was not known");
			LOGGER.throwing(CLASS_NAME, "addItem", exc);
			LOGGER.exiting(CLASS_NAME, "addItem");
			throw exc;
		}
		findProperty(property).addItem(monitoredItem);
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "addItem");
	}

	public synchronized void replaceItem(MonitoredItem monitoredItem) {
		LOGGER.entering(CLASS_NAME, "replaceItem", monitoredItem);
		if (monitoredItem == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
			LOGGER.throwing(CLASS_NAME, "replaceItem", exc);
			LOGGER.exiting(CLASS_NAME, "replaceItem");
			throw exc;
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "replaceItem", exc);
			LOGGER.exiting(CLASS_NAME, "replaceItem");
			throw exc;
		}
		if (!properties.contains(property)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " was not known");
			LOGGER.throwing(CLASS_NAME, "replaceItem", exc);
			LOGGER.exiting(CLASS_NAME, "replaceItem");
			throw exc;
		}
		findProperty(property).replaceItem(monitoredItem);
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "replaceItem");
	}

	public synchronized void removeItem(MonitoredItem monitoredItem) {
		LOGGER.entering(CLASS_NAME, "removeItem", monitoredItem);
		if (monitoredItem == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
			LOGGER.throwing(CLASS_NAME, "removeItem", exc);
			LOGGER.exiting(CLASS_NAME, "removeItem");
			throw exc;
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "removeItem", exc);
			LOGGER.exiting(CLASS_NAME, "removeItem");
			throw exc;
		}
		if (!properties.contains(property)) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " was not known");
			LOGGER.throwing(CLASS_NAME, "removeItem", exc);
			LOGGER.exiting(CLASS_NAME, "removeItem");
			throw exc;
		}
		findProperty(property).removeItem(monitoredItem);
		updateStorage();
		LOGGER.exiting(CLASS_NAME, "removeItem");
	}

	private void updateStorage() {
		LOGGER.entering(CLASS_NAME, "updateStorage");
		try {
			File rootDirectory = new File(applicationPreferences.getDirectory());
			LocalStorage.getInstance(rootDirectory).saveArchiveData();
		} catch (IOException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
		}
		LOGGER.exiting(CLASS_NAME, "updateStorage");
	}

	public synchronized List<Property> getProperties() {
		LOGGER.entering(CLASS_NAME, "getProperties");
		List<Property> copyList = new ArrayList<>();
		properties.stream().forEach(property -> {
			copyList.add(new Property(property));
		});
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getProperties", copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueItems() {
		LOGGER.entering(CLASS_NAME, "getPropertiesWithOverdueItems");
		List<Property> copyList = new ArrayList<>();
		getProperties().stream().forEach(property -> {
			if (property.areItemsOverdue()) {
				copyList.add(property);
			}
		});
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getPropertiesWithOverdueItems", copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueNotices() {
		LOGGER.entering(CLASS_NAME, "getPropertiesWithOverdueNotices");
		List<Property> copyList = new ArrayList<>();
		getProperties().stream().forEach(property -> {
			if (property.areNoticesOverdue()) {
				copyList.add(property);
			}
		});
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getPropertiesWithOverdueNotices", copyList);
		return copyList;
	}

	public synchronized List<MonitoredItem> getItemsFor(Property property) {
		LOGGER.entering(CLASS_NAME, "getItemsFor", property);
		if (findProperty(property) == null) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " not found");
			LOGGER.throwing(CLASS_NAME, "getItemsFor", exc);
			LOGGER.exiting(CLASS_NAME, "getItemsFor");
			throw exc;
		}
		List<MonitoredItem> copyList = new ArrayList<>();
		findProperty(property).getItems().stream().forEach(i -> {
			copyList.add(new MonitoredItem(i));
		});
		LOGGER.exiting(CLASS_NAME, "getItemsFor", copyList);
		return copyList;
	}

	private synchronized Property findProperty(Property property) {
		LOGGER.entering(CLASS_NAME, "findProperty", property);
		Property found = null;
		for (Property p : properties) {
			if (p.equals(property)) {
				found = p;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findProperty", found);
		return found;
	}

	private void auditBeforeClearing() {
		for (Property p : properties) {
			AuditRecord record = new AuditRecord(AuditType.REMOVED, AuditObject.PROPERTY);
			record.setDescription(p.toString());
			auditWrite(record);
		}
	}

	public void auditAddProperty(Property property) {
		AuditRecord record = new AuditRecord(AuditType.ADDED, AuditObject.PROPERTY);
		record.setDescription(property.toString());
		auditWrite(record);
	}

	public void auditRemoveProperty(Property property) {
		AuditRecord record = new AuditRecord(AuditType.REMOVED, AuditObject.PROPERTY);
		record.setDescription(property.toString());
		auditWrite(record);
	}

	public void auditAddItem(MonitoredItem monitoredItem) {
		Property property = monitoredItem.getOwner();
		AuditRecord record = new AuditRecord(AuditType.ADDED, AuditObject.MONITOREDITEM);
		record.setDescription(monitoredItem.toString() + " added to " + property.toString());
		auditWrite(record);
	}

	public void auditReplaceItem(MonitoredItem monitoredItem) {
		Property property = monitoredItem.getOwner();
		AuditRecord record = new AuditRecord(AuditType.REPLACED, AuditObject.MONITOREDITEM);
		record.setDescription(monitoredItem.toString() + " replaced in " + property.toString());
		auditWrite(record);
	}

	public void auditRemoveItem(MonitoredItem monitoredItem) {
		Property property = monitoredItem.getOwner();
		AuditRecord record = new AuditRecord(AuditType.REMOVED, AuditObject.MONITOREDITEM);
		record.setDescription(monitoredItem.toString() + " removed from " + property.toString());
		auditWrite(record);
	}

	private void auditWrite(AuditRecord record) {
		AuditWriter.write(record);
	}

	private void timerPopped(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "timerPoppped", "timer popped at " + LocalDateTime.now());
		if (getPropertiesWithOverdueNotices().size() > 0) {
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Notified Items");
			alert.setHeaderText("Notified items have been found");
			StringBuilder context = new StringBuilder();
			context.append("The Following properties have overdue items").append("\n");
			for (Property property : getPropertiesWithOverdueNotices()) {
				context.append(property.getAddress().toString()).append("\n");
			}
			alert.setContentText(context.toString());
			Platform.runLater(() -> {
				alert.showAndWait();
			});
		}
		if (getPropertiesWithOverdueItems().size() > 0) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Overdue Items");
			alert.setHeaderText("Overdue items have been found");
			StringBuilder context = new StringBuilder();
			context.append("The Following properties have overdue items").append("\n");
			for (Property property : getPropertiesWithOverdueItems()) {
				context.append(property.getAddress().toString()).append("\n");
			}
			alert.setContentText(context.toString());
			Platform.runLater(() -> {
				alert.showAndWait();
			});
		}
		LOGGER.exiting(CLASS_NAME, "timerPopped");
	}
}
