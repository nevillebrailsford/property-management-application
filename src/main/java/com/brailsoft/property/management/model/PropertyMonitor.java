package com.brailsoft.property.management.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		auditBeforeClearing();
		properties.clear();
	}

	public synchronized void addProperty(Property newProperty) {
		if (newProperty == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (properties.contains(newProperty)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + newProperty + " already exists");
		}
		properties.add(new Property(newProperty));
		updateStorage();
	}

	public synchronized void replaceProperty(Property oldProperty, Property newProperty) {
		if (oldProperty == null || newProperty == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(oldProperty)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + oldProperty + " was not known");
		}
		if (properties.contains(newProperty)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + newProperty + " already exists");
		}
		properties.remove(oldProperty);
		properties.add(newProperty);
		updateStorage();
	}

	public synchronized void removeProperty(Property oldProperty) {
		if (oldProperty == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(oldProperty)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + oldProperty + " was not known");
		}
		properties.remove(oldProperty);
		updateStorage();
	}

	public synchronized void addItem(MonitoredItem monitoredItem) {
		if (monitoredItem == null) {
			throw new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(property)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + property + " was not known");
		}
		findProperty(property).addItem(monitoredItem);
		updateStorage();
	}

	public synchronized void replaceItem(MonitoredItem monitoredItem) {
		if (monitoredItem == null) {
			throw new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(property)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + property + " was not known");
		}
		findProperty(property).replaceItem(monitoredItem);
		updateStorage();
	}

	public synchronized void removeItem(MonitoredItem monitoredItem) {
		if (monitoredItem == null) {
			throw new IllegalArgumentException("PropertyMonitor: monitoredItem was null");
		}
		Property property = monitoredItem.getOwner();
		if (property == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(property)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + property + " was not known");
		}
		findProperty(property).removeItem(monitoredItem);
		updateStorage();
	}

	private void updateStorage() {
		try {
			File rootDirectory = new File(applicationPreferences.getDirectory());
			LocalStorage.getInstance(rootDirectory).saveArchiveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized List<Property> getProperties() {
		List<Property> copyList = new ArrayList<>();
		properties.stream().forEach(property -> {
			copyList.add(new Property(property));
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueItems() {
		List<Property> copyList = new ArrayList<>();
		getProperties().stream().forEach(property -> {
			if (property.areItemsOverdue()) {
				copyList.add(property);
			}
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueNotices() {
		List<Property> copyList = new ArrayList<>();
		getProperties().stream().forEach(property -> {
			if (property.areNoticesOverdue()) {
				copyList.add(property);
			}
		});
		Collections.sort(copyList);
		return copyList;
	}

	public synchronized List<MonitoredItem> getItemsFor(Property property) {
		if (findProperty(property) == null) {
			throw new IllegalArgumentException("PropertyMonitor: property " + property + " not found");
		}
		List<MonitoredItem> copyList = new ArrayList<>();
		findProperty(property).getItems().stream().forEach(i -> {
			copyList.add(new MonitoredItem(i));
		});
		return copyList;
	}

	private synchronized Property findProperty(Property property) {
		Property found = null;
		for (Property p : properties) {
			if (p.equals(property)) {
				found = p;
				break;
			}
		}
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
		System.out.println(LocalDateTime.now() + " timer popped");
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
	}
}
