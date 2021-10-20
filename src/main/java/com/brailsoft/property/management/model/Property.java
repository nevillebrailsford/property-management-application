package com.brailsoft.property.management.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Property implements Comparable<Property> {
	private Address address;
	private List<MonitoredItem> items = new ArrayList<>();

	public Property(Address address) {
		this.address = new Address(address);
	}

	public Property(Property that) {
		this.address = new Address(that.address);
		this.items = new ArrayList<>();
		that.items.stream().forEach(item -> {
			this.items.add(new MonitoredItem(item));
		});
	}

	public void addItem(MonitoredItem item) {
		if (item == null) {
			throw new IllegalArgumentException("Property: item was null");
		}
		if (items.contains(item)) {
			throw new IllegalArgumentException("Property: item " + item + " already exists");
		}
		items.add(item);
	}

	public List<MonitoredItem> getItems() {
		List<MonitoredItem> copyList = new ArrayList<>();
		items.stream().forEach(item -> {
			copyList.add(item);
		});
		Collections.sort(copyList);
		return copyList;
	}

	@Override
	public int compareTo(Property that) {
		return this.address.compareTo(that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Property other = (Property) obj;
		return Objects.equals(address, other.address);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (address != null) {
			builder.append(address);
		}
		return builder.toString();
	}

}
