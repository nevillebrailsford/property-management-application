package com.brailsoft.property.management.userinterface;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class AllItemHBox extends HBox {
	private MonitoredItem monitoredItem;
	Label addressLabel;
	Label lastActionLabel;
	Label nextActionLabel;
	Label nextNoticeLabel;

	public AllItemHBox(MonitoredItem monitoredtem) {
		super();
		this.monitoredItem = monitoredtem;
		this.setSpacing(5);
		createGUI();
	}

	public MonitoredItem getMonitoredItem() {
		return monitoredItem;
	}

	private void setStyles() {
		setNextActionStyle();
		setNextNoticeStyle();
	}

	private void bind() {
		lastActionLabel.textProperty().bind(monitoredItem.lastActionProperty());
		nextActionLabel.textProperty().bind(monitoredItem.nextActionProperty());
		nextNoticeLabel.textProperty().bind(monitoredItem.nextNoticeProperty());
	}

	private void createGUI() {
		makeSpace();
		makeLabels();
		makeSpace();
		bind();
		setStyles();
	}

	private void makeLabels() {
		makeAddressLabel();
		makeDescriptionLabel();
		makeLastActionLabels();
		makeNextActionLabels();
		makeNextNoticeLabels();
	}

	private void makeSpace() {
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
	}

	private void makeAddressLabel() {
		Label label = new Label(monitoredItem.getOwner().getAddress().getPostCode().toString());
		label.setFont(new Font(15.0));
		getChildren().add(label);
	}

	private void makeDescriptionLabel() {
		Label label = new Label(monitoredItem.getDescription());
		label.setFont(new Font(15.0));
		getChildren().add(label);
	}

	private void makeNextNoticeLabels() {
		Label label;
		label = new Label("next notice: ");
		label.setFont(new Font(15.0));
		getChildren().add(label);
		nextNoticeLabel = new Label();
		nextNoticeLabel.setFont(new Font(15.0));
		getChildren().add(nextNoticeLabel);
	}

	private void makeNextActionLabels() {
		Label label;
		label = new Label("next action: ");
		label.setFont(new Font(15.0));
		getChildren().add(label);
		nextActionLabel = new Label();
		nextActionLabel.setFont(new Font(15.0));
		getChildren().add(nextActionLabel);
	}

	private void makeLastActionLabels() {
		Label label;
		label = new Label("last action: ");
		label.setFont(new Font(15.0));
		getChildren().add(label);
		lastActionLabel = new Label();
		lastActionLabel.setFont(new Font(15.0));
		lastActionLabel.getStyleClass().add("within-date-style");
		getChildren().add(lastActionLabel);
	}

	private void setNextNoticeStyle() {
		nextNoticeLabel.getStyleClass().add("within-date-style");
	}

	private void setNextActionStyle() {
		nextActionLabel.getStyleClass().add("within-date-style");
	}

}
