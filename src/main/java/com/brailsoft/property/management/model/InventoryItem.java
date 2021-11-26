package com.brailsoft.property.management.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.constant.DateFormats;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InventoryItem implements Comparable<InventoryItem> {
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DateFormats.dateFormatForUI);

	private StringProperty description = new SimpleStringProperty(this, "description", "");
	private StringProperty manufacturer = new SimpleStringProperty(this, "manufacturuer", "");
	private StringProperty model = new SimpleStringProperty(this, "model", "");
	private StringProperty serialNumber = new SimpleStringProperty(this, "serialNumber", "");
	private StringProperty supplier = new SimpleStringProperty(this, "supplier", "");
	private StringProperty purchaseDate = new SimpleStringProperty(this, "puchaseDate", null);
	private ObjectProperty<Property> owner = new SimpleObjectProperty<>(this, "owner", null);

	public InventoryItem(String description, String manufacturer, String model, String serialNumber, String supplier,
			LocalDate purchaseDateAsDate) {
		if (description == null || description.isBlank() || description.isEmpty()) {
			throw new IllegalArgumentException("InventoryItem: description was null");
		}
		String purchaseDate;
		if (purchaseDateAsDate == null) {
			purchaseDate = "Unknown";
		} else {
			purchaseDate = purchaseDateAsDate.format(dateFormatter);
		}
		initialize(description, manufacturer, model, serialNumber, supplier, purchaseDate);
	}

	public InventoryItem(InventoryItem that) {
		if (that == null) {
			throw new IllegalArgumentException("InventoryItem: item was null");
		}
		this.description.set(that.description.get());
		this.manufacturer.set(that.manufacturer.get());
		this.model.set(that.model.get());
		this.serialNumber.set(that.serialNumber.get());
		this.supplier.set(that.supplier.get());
		this.purchaseDate.set(that.purchaseDate.get());
		if (that.owner.get() != null) {
			this.owner.set(new Property(that.owner.get()));
		} else {
			this.owner.set(null);
		}
	}

	public InventoryItem(Element itemElement) {
		if (itemElement == null) {
			throw new IllegalArgumentException("InventoryItem: itemElement was null");
		}
		String description = itemElement.getElementsByTagName(Constants.DESCRIPTION).item(0).getTextContent();
		String manufacturer = itemElement.getElementsByTagName(Constants.MANUFACTURER).item(0).getTextContent();
		String model = itemElement.getElementsByTagName(Constants.MODEL).item(0).getTextContent();
		String serialNumber = itemElement.getElementsByTagName(Constants.SERIAL_NUMBER).item(0).getTextContent();
		String supplier = itemElement.getElementsByTagName(Constants.SUPPLIER).item(0).getTextContent();
		String purchaseDate = itemElement.getElementsByTagName(Constants.PURCHASE_DATE).item(0).getTextContent();

		initialize(description, manufacturer, model, serialNumber, supplier, purchaseDate);
	}

	public Element buildElement(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("InventoryItem: document was null");
		}
		Element result = document.createElement(Constants.INVENTORY);
		result.appendChild(ElementBuilder.build(Constants.DESCRIPTION, getDescription(), document));
		result.appendChild(ElementBuilder.build(Constants.MANUFACTURER, getManufacturer(), document));
		result.appendChild(ElementBuilder.build(Constants.MODEL, getModel(), document));
		result.appendChild(ElementBuilder.build(Constants.SERIAL_NUMBER, getSerialNumber(), document));
		result.appendChild(ElementBuilder.build(Constants.SUPPLIER, getSupplier(), document));
		result.appendChild(ElementBuilder.build(Constants.PURCHASE_DATE, getPurchaseDate(), document));
		return result;
	}

	private void initialize(String description, String manufacturer, String model, String serialNumber, String supplier,
			String purchaseDate) {
		this.description.set(description);
		this.manufacturer.set(manufacturer);
		this.model.set(model);
		this.serialNumber.set(serialNumber);
		this.supplier.set(supplier);
		this.purchaseDate.set(purchaseDate);
		this.owner.set(null);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public String getManufacturer() {
		return manufacturer.get();
	}

	public StringProperty manufacturerProperty() {
		return manufacturer;
	}

	public String getModel() {
		return model.get();
	}

	public StringProperty modelProperty() {
		return model;
	}

	public String getSerialNumber() {
		return serialNumber.get();
	}

	public StringProperty serialNumberProperty() {
		return serialNumber;
	}

	public String getSupplier() {
		return supplier.get();
	}

	public StringProperty supplierProperty() {
		return supplier;
	}

	public String getPurchaseDate() {
		return purchaseDate.get();
	}

	public StringProperty purchaseDateProperty() {
		return purchaseDate;
	}

	public Property getOwner() {
		return owner.get();
	}

	public void setOwner(Property property) {
		this.owner.set(new Property(property));
	}

	public ObjectProperty<Property> ownerProperty() {
		return owner;
	}

	@Override
	public int hashCode() {
		return Objects.hash(manufacturer.get(), model.get(), serialNumber.get());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryItem other = (InventoryItem) obj;
		return Objects.equals(manufacturer.get(), other.manufacturer.get())
				&& Objects.equals(model.get(), other.model.get())
				&& Objects.equals(serialNumber.get(), other.serialNumber.get());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (description.get() != null) {
			builder.append(description.get());
			builder.append(", ");
		}
		if (manufacturer.get() != null) {
			builder.append(manufacturer.get());
			builder.append(", ");
		}
		if (model.get() != null) {
			builder.append(model.get());
			builder.append(", ");
		}
		if (serialNumber.get() != null) {
			builder.append(serialNumber.get());
		}
		return builder.toString();
	}

	@Override
	public int compareTo(InventoryItem that) {
		int result = this.manufacturer.get().compareTo(that.manufacturer.get());
		if (result == 0) {
			result = this.model.get().compareTo(that.model.get());
			if (result == 0) {
				result = this.serialNumber.get().compareTo(that.serialNumber.get());
			}
		}
		return result;
	}

}
