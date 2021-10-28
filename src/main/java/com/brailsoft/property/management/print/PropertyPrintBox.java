package com.brailsoft.property.management.print;

import com.brailsoft.property.management.model.Address;
import com.brailsoft.property.management.model.Property;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class PropertyPrintBox extends HBox {
	private static final double FONT_SIZE = 15.0;
	private Address address;

	public PropertyPrintBox(Property property) {
		super();
		this.address = new Address(property.getAddress());
		this.setSpacing(5);
		createSpace();
		createAddressLabel();
		createSpace();
	}

	private void createAddressLabel() {
		Label addressLabel = new Label();
		addressLabel.textProperty().bind(address.fulladdressProperty());
		addressLabel.setFont(new Font(FONT_SIZE));
		getChildren().add(addressLabel);
	}

	private void createSpace() {
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
	}
}
