package com.brailsoft.property.management.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class PropertyMonitor {
	private static PropertyMonitor instance = null;
	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);

	private final ObservableList<Property> properties;

	public synchronized static PropertyMonitor getInstance() {
		if (instance == null) {
			instance = new PropertyMonitor();
		}
		return instance;
	}

	private PropertyMonitor() {
		properties = FXCollections.observableArrayList();
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

}
