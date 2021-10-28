package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.OverdueItemHBox;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OverdueItemsController implements Initializable {

	@FXML
	VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		List<Property> overdueProperties = PropertyMonitor.getInstance().getPropertiesWithOverdueItems();
		if (overdueProperties.size() == 0) {
			Label label = new Label("No overdue items");
			content.getChildren().add(label);
		} else {
			for (Property property : overdueProperties) {
				List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
				for (MonitoredItem item : allItems) {
					if (item.overdue()) {
						OverdueItemHBox itemHBox = new OverdueItemHBox(item);
						content.getChildren().add(itemHBox);
					}
				}
			}
		}

	}

}
