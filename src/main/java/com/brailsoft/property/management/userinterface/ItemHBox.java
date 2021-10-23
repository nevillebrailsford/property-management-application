package com.brailsoft.property.management.userinterface;

import java.time.format.DateTimeFormatter;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ItemHBox extends HBox {
	private MonitoredItem monitoredItem;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public ItemHBox(MonitoredItem monitoredtem) {
		super();
		this.monitoredItem = monitoredtem;
		this.setSpacing(5);
		Label label = new Label(monitoredItem.getDescription());
		label.setFont(new Font(15.0));
		getChildren().add(label);
		label = new Label("last action: " + monitoredItem.getLastActionPerformed().format(dateFormatter));
		label.setFont(new Font(15.0));
		getChildren().add(label);
		label = new Label("next action: " + monitoredItem.getTimeForNextAction().format(dateFormatter));
		label.setFont(new Font(15.0));
		getChildren().add(label);
		label = new Label("next notice: " + monitoredItem.getTimeForNextNotice().format(dateFormatter));
		label.setFont(new Font(15.0));
		getChildren().add(label);
	}

	public MonitoredItem getMonitoredItem() {
		return monitoredItem;
	}
}
