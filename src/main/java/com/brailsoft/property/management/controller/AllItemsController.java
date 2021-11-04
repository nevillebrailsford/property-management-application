package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.AllItemsTableView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class AllItemsController implements Initializable {

	@FXML
	VBox content;

	private AllItemsTableView tableView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tableView = new AllItemsTableView();
		List<Property> properties = PropertyMonitor.getInstance().getProperties();
		for (Property property : properties) {
			List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
			for (MonitoredItem item : allItems) {
				tableView.addItem(item);
			}
		}
		content.getChildren().add(tableView);
	}

}
