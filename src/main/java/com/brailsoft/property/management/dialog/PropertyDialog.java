package com.brailsoft.property.management.dialog;

import com.brailsoft.property.management.model.Address;
import com.brailsoft.property.management.model.PostCode;
import com.brailsoft.property.management.model.Property;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class PropertyDialog extends Dialog<Property> {

	public PropertyDialog() {
		setTitle("Add a Property");
		setHeaderText("Enter the details below to create a new property.");
		setResizable(true);

		Label label1 = new Label("Street address:");
		Label label2 = new Label("Town:");
		Label label3 = new Label("County:");
		Label label4 = new Label("Post Code:");
		TextField street = new TextField();
		TextField town = new TextField();
		TextField county = new TextField();
		TextField postcode = new TextField();

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(street, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(town, 2, 2);
		grid.add(label3, 1, 3);
		grid.add(county, 2, 3);
		grid.add(label4, 1, 5);
		grid.add(postcode, 2, 5);
		getDialogPane().setContent(grid);

		ButtonType buttonTypeOk = new ButtonType("Add Property", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);

		setResultConverter(new Callback<ButtonType, Property>() {

			@Override
			public Property call(ButtonType param) {
				if (param == buttonTypeOk) {
					String line1 = street.getText();
					String line2 = town.getText();
					String line3 = county.getText();
					String pc = postcode.getText().toUpperCase();
					PostCode thePostCode = new PostCode(pc);
					String[] linesOfAddress = new String[] { line1, line2, line3 };
					Address address = new Address(thePostCode, linesOfAddress);
					return new Property(address);
				}
				return null;
			}
		});
	}
}
