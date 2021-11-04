package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.MonitoredItemsTableView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class OverdueItemsController implements Initializable {

	@FXML
	VBox content;

	private MonitoredItemsTableView tableView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tableView = new MonitoredItemsTableView();
		List<Property> overdueProperties = PropertyMonitor.getInstance().getPropertiesWithOverdueItems();
		for (Property property : overdueProperties) {
			List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
			for (MonitoredItem item : allItems) {
				if (item.overdue()) {
					tableView.addItem(item);
				}
			}
		}
		content.getChildren().add(tableView);
	}

}
