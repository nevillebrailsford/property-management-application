package com.brailsoft.property.management.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.brailsoft.property.management.audit.AuditObject;
import com.brailsoft.property.management.audit.AuditRecord;
import com.brailsoft.property.management.audit.AuditType;
import com.brailsoft.property.management.audit.AuditWriter;
import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.controller.StatusMonitor;
import com.brailsoft.property.management.launcher.PropertyManager;
import com.brailsoft.property.management.mail.EmailSender;
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

	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance();

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

	public synchronized void addInventoryListener(ListChangeListener<? super InventoryItem> inventoryListener,
			Property property) {
		if (inventoryListener == null) {
			throw new IllegalArgumentException("PropertyMonitor: inventoryListener was null");
		}
		findProperty(property).addInventoryListener(inventoryListener);
	}

	public synchronized void removeInventoryListener(ListChangeListener<? super InventoryItem> inventoryListener,
			Property property) {
		if (inventoryListener == null) {
			throw new IllegalArgumentException("PropertyMonitor: inventoryListener was null");
		}
		findProperty(property).removeInventoryListener(inventoryListener);
	}

	public void startTimer() {
		LOGGER.entering(CLASS_NAME, "startTimer");
		timer.start();
		LOGGER.exiting(CLASS_NAME, "startTimer");
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
		try {
			properties.add(new Property(newProperty));
			StatusMonitor.getInstance().update("Property " + newProperty + " added");
			updateStorage();
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addProperty", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addProperty");
		}
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
		try {
			properties.remove(oldProperty);
			properties.add(newProperty);
			updateStorage();
			StatusMonitor.getInstance().update("Property " + newProperty + " replaced");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "replaceProperty", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "replaceProperty");
		}
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
		try {
			properties.remove(oldProperty);
			updateStorage();
			StatusMonitor.getInstance().update("Property " + oldProperty + " deleted");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeProperty", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeProperty");
		}
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
		try {
			findProperty(property).addItem(monitoredItem);
			updateStorage();
			StatusMonitor.getInstance().update("Event " + monitoredItem + " added");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addItem", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addItem");
		}
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
		try {
			findProperty(property).replaceItem(monitoredItem);
			updateStorage();
			StatusMonitor.getInstance().update("Event " + monitoredItem + " updated");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "replaceItem", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "replaceItem");
		}
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
		try {
			findProperty(property).removeItem(monitoredItem);
			updateStorage();
			StatusMonitor.getInstance().update("Event " + monitoredItem + " deleted");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeItem", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeItem");
		}
	}

	public synchronized void addItem(InventoryItem inventoryItem) {
		LOGGER.entering(CLASS_NAME, "addItem", inventoryItem);
		if (inventoryItem == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: inventoryItem was null");
			LOGGER.throwing(CLASS_NAME, "addItem", exc);
			LOGGER.exiting(CLASS_NAME, "addItem");
			throw exc;
		}
		Property property = inventoryItem.getOwner();
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
		try {
			findProperty(property).addItem(inventoryItem);
			updateStorage();
			StatusMonitor.getInstance().update("Inventory Item " + inventoryItem + " added");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "addItem", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "addItem");
		}
	}

	public synchronized void removeItem(InventoryItem inventoryItem) {
		LOGGER.entering(CLASS_NAME, "removeItem", inventoryItem);
		if (inventoryItem == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: inventoryItem was null");
			LOGGER.throwing(CLASS_NAME, "removeItem", exc);
			LOGGER.exiting(CLASS_NAME, "removeItem");
			throw exc;
		}
		Property property = inventoryItem.getOwner();
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
		try {
			findProperty(property).removeItem(inventoryItem);
			updateStorage();
			StatusMonitor.getInstance().update("Inventory Item " + inventoryItem + " deleted");
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "removeItem", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "removeItem");
		}
	}

	private void updateStorage() {
		LOGGER.entering(CLASS_NAME, "updateStorage");
		try {
			File rootDirectory = new File(applicationPreferences.getDirectory());
			LocalStorage.getInstance(rootDirectory).storeData();
		} catch (IOException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
		}
		LOGGER.exiting(CLASS_NAME, "updateStorage");
	}

	public synchronized List<Property> getProperties() {
		LOGGER.entering(CLASS_NAME, "getProperties");
		List<Property> copyList = properties.stream().map(property -> new Property(property))
				.collect(Collectors.toList());
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getProperties", copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueItems() {
		LOGGER.entering(CLASS_NAME, "getPropertiesWithOverdueItems");
		List<Property> copyList = getProperties().stream().filter(property -> property.areItemsOverdue())
				.collect(Collectors.toList());
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getPropertiesWithOverdueItems", copyList);
		return copyList;
	}

	public synchronized List<Property> getPropertiesWithOverdueNotices() {
		LOGGER.entering(CLASS_NAME, "getPropertiesWithOverdueNotices");
		List<Property> copyList = getProperties().stream()
				.filter(property -> property.areNoticesOverdue() && !property.areItemsOverdue())
				.collect(Collectors.toList());
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getPropertiesWithOverdueNotices", copyList);
		return copyList;
	}

	public synchronized List<MonitoredItem> getItemsFor(Property property) {
		LOGGER.entering(CLASS_NAME, "getItemsFor", property);
		if (property == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "getItemsFor", exc);
			LOGGER.exiting(CLASS_NAME, "getItemsFor");
			throw exc;
		}
		if (findProperty(property) == null) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " not found");
			LOGGER.throwing(CLASS_NAME, "getItemsFor", exc);
			LOGGER.exiting(CLASS_NAME, "getItemsFor");
			throw exc;
		}
		List<MonitoredItem> copyList = findProperty(property).getItems().stream().map(item -> new MonitoredItem(item))
				.collect(Collectors.toList());
		Collections.sort(copyList);
		LOGGER.exiting(CLASS_NAME, "getItemsFor", copyList);
		return copyList;
	}

	public synchronized List<InventoryItem> getInventoryFor(Property property) {
		LOGGER.entering(CLASS_NAME, "getInventoryFor", property);
		if (property == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: property was null");
			LOGGER.throwing(CLASS_NAME, "getInventoryFor", exc);
			LOGGER.exiting(CLASS_NAME, "getInventoryFor");
			throw exc;
		}
		if (findProperty(property) == null) {
			IllegalArgumentException exc = new IllegalArgumentException(
					"PropertyMonitor: property " + property + " not found");
			LOGGER.throwing(CLASS_NAME, "getInventoryFor", exc);
			LOGGER.exiting(CLASS_NAME, "getInventoryFor");
			throw exc;
		}
		List<InventoryItem> copyList = findProperty(property).getInventory().stream()
				.map(item -> new InventoryItem(item)).sorted().collect(Collectors.toList());
		LOGGER.exiting(CLASS_NAME, "getInventoryFor", copyList);
		return copyList;
	}

	public synchronized List<MonitoredItem> getOverdueItemsFor(LocalDate date) {
		LOGGER.entering(CLASS_NAME, "getOverdueItemsFor", date);
		if (date == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: date was null");
			LOGGER.throwing(CLASS_NAME, "getOverdueItemsFor", exc);
			LOGGER.exiting(CLASS_NAME, "getOverdueItemsFor");
			throw exc;
		}
		List<MonitoredItem> overdueList = getAllItems().stream()
				.filter(item -> item.getTimeForNextAction().equals(date)).collect(Collectors.toList());
		Collections.sort(overdueList);
		LOGGER.exiting(CLASS_NAME, "getOverdueItemsFor", overdueList);
		return overdueList;
	}

	public synchronized List<MonitoredItem> getNotifiedItemsFor(LocalDate date) {
		LOGGER.entering(CLASS_NAME, "getNotifiedItemsFor", date);
		if (date == null) {
			IllegalArgumentException exc = new IllegalArgumentException("PropertyMonitor: date was null");
			LOGGER.throwing(CLASS_NAME, "getNotifiedItemsFor", exc);
			LOGGER.exiting(CLASS_NAME, "getNotifiedItemsFor");
			throw exc;
		}
		List<MonitoredItem> notifiedList = getAllItems().stream()
				.filter(item -> item.getTimeForNextNotice().equals(date)).collect(Collectors.toList());
		Collections.sort(notifiedList);
		LOGGER.exiting(CLASS_NAME, "getNotifiedItemsFor", notifiedList);
		return notifiedList;
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

	private List<MonitoredItem> getAllItems() {
		LOGGER.entering(CLASS_NAME, "getAllItems");
		List<MonitoredItem> allItems = getProperties().stream().flatMap(property -> property.getItems().stream())
				.collect(Collectors.toList());
		Collections.sort(allItems);
		LOGGER.exiting(CLASS_NAME, "getAllItems", allItems);
		return allItems;
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

	public void auditAddItem(InventoryItem inventoryItem) {
		Property property = inventoryItem.getOwner();
		AuditRecord record = new AuditRecord(AuditType.ADDED, AuditObject.INVENTORYITEM);
		record.setDescription(inventoryItem.toString() + " added to " + property.toString());
		auditWrite(record);
	}

	public void auditRemoveItem(InventoryItem inventoryItem) {
		Property property = inventoryItem.getOwner();
		AuditRecord record = new AuditRecord(AuditType.REMOVED, AuditObject.INVENTORYITEM);
		record.setDescription(inventoryItem.toString() + " removed from " + property.toString());
		auditWrite(record);
	}

	private void auditWrite(AuditRecord record) {
		AuditWriter.write(record);
	}

	private void timerPopped(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "timerPoppped", "timer popped at " + LocalDate.now());
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastTimer = applicationPreferences.lastTimer();
		if (lastTimer == null || lastTimer.plusWeeks(1).isBefore(now)) {
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
			sendEmailIfRequired();
			try {
				applicationPreferences.setLastTimer(now);
			} catch (Exception e) {
				LOGGER.warning("Caught exception: " + e.getMessage());
			}
		} else {
			LOGGER.fine("Time now = " + now + ", lastTimer = " + lastTimer);
		}
		LOGGER.exiting(CLASS_NAME, "timerPopped");
	}

	private void sendEmailIfRequired() {
		LOGGER.entering(CLASS_NAME, "sendEmailIfRequired");
		List<MonitoredItem> notifiedItems = new ArrayList<>();
		List<MonitoredItem> overdueItems = new ArrayList<>();
		if (applicationPreferences.isEmailNotification()) {
			for (Property property : getPropertiesWithOverdueNotices()) {
				for (MonitoredItem item : property.getOverdueNotices()) {
					if (item.getEmailSentOn() == null) {
						notifiedItems.add(item);
						updateEmailSentOn(item);
					} else {
						if (LocalDate.now().isAfter(item.getEmailSentOn().plusWeeks(1))) {
							notifiedItems.add(item);
							updateEmailSentOn(item);
						}
					}
				}
				for (MonitoredItem item : property.getOverdueItems()) {
					if (item.getEmailSentOn() == null) {
						overdueItems.add(item);
						updateEmailSentOn(item);
					} else {
						if (LocalDate.now().isAfter(item.getEmailSentOn().plusWeeks(1))) {
							overdueItems.add(item);
							updateEmailSentOn(item);
						}
					}
				}
			}
		}
		if (notifiedItems.size() > 0 || overdueItems.size() > 0) {
			try {
				sendEmail(notifiedItems, overdueItems);
			} catch (Exception e) {
				LOGGER.warning("Caught exception: " + e.getMessage());
			}
		}
		LOGGER.exiting(CLASS_NAME, "sendEmailIfRequired");
	}

	private void sendEmail(List<MonitoredItem> notifiedItems, List<MonitoredItem> overdueItems) throws Exception {
		LOGGER.entering(CLASS_NAME, "sendEmail");
		StringBuilder message = new StringBuilder();
		message.append("The following items need attention:\n\n");
		if (notifiedItems.size() > 0) {
			message.append("The following items are due soon:\n");
			for (MonitoredItem item : notifiedItems) {
				message.append(item.getOwner().toString()).append(" - ");
				message.append(item.toString());
				message.append("\n");
			}
			message.append("\n");
		}
		if (overdueItems.size() > 0) {
			message.append("The following items are overdue:\n");
			for (MonitoredItem item : overdueItems) {
				message.append(item.getOwner().toString()).append(" ");
				message.append(item.toString());
				message.append("\n");
			}
			message.append("\n");
		}
		StatusMonitor.getInstance().update("Email message being prepared");
		EmailSender worker = new EmailSender(message.toString());
		PropertyManager.executor().execute(worker);
		LOGGER.exiting(CLASS_NAME, "sendEmail");
	}

	private void updateEmailSentOn(MonitoredItem item) {
		LOGGER.entering(CLASS_NAME, "updateEmailSentOn", item);
		LocalDate now = LocalDate.now();
		MonitoredItem updatedItem = new MonitoredItem(item);
		updatedItem.setEmailSentOn(now);
		replaceItem(updatedItem);
		LOGGER.exiting(CLASS_NAME, "updateEmailSentOn", item);
	}
}
