package com.brailsoft.property.management.userinterface;

import com.brailsoft.property.management.model.Address;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class AddressHBox extends HBox {
	private Address address;

	public AddressHBox(Address address) {
		super();
		this.address = new Address(address);
		this.setSpacing(5);
		String[] linesOfAddress = address.getLinesOfAddress();
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
		for (int index = 0; index < linesOfAddress.length; index++) {
			Label lineLabel = new Label(linesOfAddress[index]);
			lineLabel.setFont(new Font(25.0));
			getChildren().add(lineLabel);
		}
		Label postCodeLabel = new Label(address.getPostCode().toString());
		postCodeLabel.setFont(new Font(25.0));
		getChildren().add(postCodeLabel);
		pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
	}

	public Address getAddress() {
		return new Address(address);
	}
}
