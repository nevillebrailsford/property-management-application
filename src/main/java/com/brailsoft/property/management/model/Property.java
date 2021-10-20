package com.brailsoft.property.management.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Property implements Comparable<Property> {
	private Address address;
	private List<MonitoredItem> items = new ArrayList<>();

	public Property(Address address) {
		if (address == null) {
			throw new IllegalArgumentException("Property: address was null");
		}
		this.address = new Address(address);
	}

	public Property(Property that) {
		if (that == null) {
			throw new IllegalArgumentException("Property: property was null");
		}
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
		items.add(new MonitoredItem(item));
	}

	public List<MonitoredItem> getItems() {
		List<MonitoredItem> copyList = new ArrayList<>();
		items.stream().forEach(item -> {
			copyList.add(item);
		});
		Collections.sort(copyList);
		return copyList;
	}

	public Address getAddress() {
		return new Address(address);
	}

	public boolean areItemsOverdue() {
		List<MonitoredItem> list = items.stream().filter(item -> item.overdue()).collect(Collectors.toList());
		return list.size() > 0;
	}

	public List<MonitoredItem> getOverdueItems() {
		List<MonitoredItem> copyList = new ArrayList<>();
		items.stream().forEach(item -> {
			if (item.overdue()) {
				copyList.add(new MonitoredItem(item));
			}
		});
		Collections.sort(copyList);
		return copyList;
	}

	public boolean areNoticesOverdue() {
		List<MonitoredItem> list = items.stream().filter(item -> item.noticeDue()).collect(Collectors.toList());
		return list.size() > 0;
	}

	public List<MonitoredItem> getOverdueNotices() {
		List<MonitoredItem> copyList = new ArrayList<>();
		items.stream().forEach(item -> {
			if (item.noticeDue()) {
				copyList.add(new MonitoredItem(item));
			}
		});
		Collections.sort(copyList);
		return copyList;
	}

	public void clear() {
		items.clear();
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
