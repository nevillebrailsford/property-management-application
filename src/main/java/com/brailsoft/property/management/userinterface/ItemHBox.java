package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.brailsoft.property.management.dialog.DateDialog;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class ItemHBox extends HBox {
	private MonitoredItem monitoredItem;
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	Image tick = new Image(getClass().getResourceAsStream("tick-16.png"));

	public ItemHBox(MonitoredItem monitoredtem) {
		super();
		this.monitoredItem = monitoredtem;
		this.setSpacing(5);
		createGUI();
	}

	public MonitoredItem getMonitoredItem() {
		return monitoredItem;
	}

	public void refresh(MonitoredItem monitoredItem) {
		this.getChildren().clear();
		createGUI();
	}

	private void createGUI() {
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
		Label label = new Label(monitoredItem.getDescription());
		label.setFont(new Font(15.0));
		getChildren().add(label);
		label = new Label("last action: " + monitoredItem.getLastActionPerformed().format(dateFormatter));
		label.setFont(new Font(15.0));
		label.setStyle("-fx-background-color: lightgreen;");
		getChildren().add(label);
		label = new Label("next action: " + monitoredItem.getTimeForNextAction().format(dateFormatter));
		label.setFont(new Font(15.0));
		if (this.monitoredItem.overdue()) {
			label.setStyle("-fx-background-color: red;");
		} else if (this.monitoredItem.noticeDue()) {
			label.setStyle("-fx-background-color: orange;");
		} else {
			label.setStyle("-fx-background-color: lightgreen;");
		}
		getChildren().add(label);
		label = new Label("next notice: " + monitoredItem.getTimeForNextNotice().format(dateFormatter));
		label.setFont(new Font(15.0));
		if (this.monitoredItem.noticeDue()) {
			label.setStyle("-fx-background-color: orange;");
		} else {
			label.setStyle("-fx-background-color: lightgreen;");
		}
		getChildren().add(label);
		pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
		ImageView imageView = new ImageView(tick);
		Button actionComplete = new Button("Done", imageView);
		actionComplete.setOnAction(event -> {
			recordActionComplete();
		});
		getChildren().add(actionComplete);
	}

	private void recordActionComplete() {
		Optional<LocalDate> result = new DateDialog().showAndWait();
		if (result.isPresent()) {
			monitoredItem.actionPerformed(result.get().atStartOfDay());
			PropertyMonitor.getInstance().replaceItem(monitoredItem);
		}
	}
}
