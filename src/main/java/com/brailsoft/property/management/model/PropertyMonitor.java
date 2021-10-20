package com.brailsoft.property.management.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class PropertyMonitor {
	private static PropertyMonitor instance = null;

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
		properties.addListener(listener);
	}

	public synchronized void removeListener(ListChangeListener<? super Property> listener) {
		properties.removeListener(listener);
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
	}

	public synchronized void removeProperty(Property oldProperty) {
		if (oldProperty == null) {
			throw new IllegalArgumentException("PropertyMonitor: property was null");
		}
		if (!properties.contains(oldProperty)) {
			throw new IllegalArgumentException("PropertyMonitor: property " + oldProperty + " was not known");
		}
		properties.remove(oldProperty);
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

}
