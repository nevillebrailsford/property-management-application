package com.brailsoft.property.management.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.userinterface.NotifiedItemsTableView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class NotifiedItemsController implements Initializable {

	@FXML
	VBox content;

	private NotifiedItemsTableView tableView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tableView = new NotifiedItemsTableView();
		List<Property> notifiedProperties = PropertyMonitor.getInstance().getPropertiesWithOverdueNotices();
		for (Property property : notifiedProperties) {
			List<MonitoredItem> allItems = PropertyMonitor.getInstance().getItemsFor(property);
			for (MonitoredItem item : allItems) {
				if (item.noticeDue()) {
					tableView.addItem(item);
				}
			}
		}
		content.getChildren().add(tableView);
	}

}
