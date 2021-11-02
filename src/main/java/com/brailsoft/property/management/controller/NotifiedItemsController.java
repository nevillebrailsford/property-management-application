package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.NotifiedItemHBox;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NotifiedItemsController implements Initializable {

	@FXML
	VBox content;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		List<Property> notifiedProperties = PropertyMonitor.getInstance().getPropertiesWithOverdueNotices();
		if (notifiedProperties.size() == 0) {
			Label label = new Label("No notified items");
			content.getChildren().add(label);
		} else {
			for (Property property : notifiedProperties) {
				List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
				for (MonitoredItem item : allItems) {
					if (item.noticeDue()) {
						NotifiedItemHBox itemHBox = new NotifiedItemHBox(item);
						content.getChildren().add(itemHBox);
					}
				}
			}
		}

	}

}
