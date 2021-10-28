package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.AllItemHBox;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AllItemsController implements Initializable {

	@FXML
	VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		List<Property> properties = PropertyMonitor.getInstance().getProperties();
		if (properties.size() == 0) {
			Label label = new Label("No items found");
			content.getChildren().add(label);
		} else {
			for (Property property : properties) {
				List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
				for (MonitoredItem item : allItems) {
					AllItemHBox itemHBox = new AllItemHBox(item);
					content.getChildren().add(itemHBox);
				}
			}
		}

	}

}
