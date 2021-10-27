package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
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
	Image tick = new Image(getClass().getResourceAsStream("tick-16.png"));
	Label lastActionLabel;
	Label nextActionLabel;
	Label nextNoticeLabel;

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
		unbind();
		this.monitoredItem = monitoredItem;
		bind();
		setStyles();
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

	private void unbind() {
		lastActionLabel.textProperty().unbind();
		nextActionLabel.textProperty().unbind();
		nextNoticeLabel.textProperty().unbind();
	}

	private void createGUI() {
		makeSpace();
		makeLabels();
		makeSpace();
		makeActionCompleteButton();
		bind();
		setStyles();
	}

	private void makeLabels() {
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

	private void makeActionCompleteButton() {
		ImageView imageView = new ImageView(tick);
		Button actionComplete = new Button("Done", imageView);
		actionComplete.setOnAction(event -> {
			recordActionComplete();
		});
		getChildren().add(actionComplete);
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
		if (this.monitoredItem.noticeDue()) {
			nextNoticeLabel.getStyleClass().add("overdue-notice-style");
		} else {
			nextNoticeLabel.getStyleClass().add("within-date-style");
		}
	}

	private void setNextActionStyle() {
		if (this.monitoredItem.overdue()) {
			nextActionLabel.getStyleClass().add("overdue-item-style");
		} else if (this.monitoredItem.noticeDue()) {
			nextActionLabel.getStyleClass().add("overdue-notice-style");
		} else {
			nextActionLabel.getStyleClass().add("within-date-style");
		}
	}

	private void recordActionComplete() {
		Optional<LocalDate> result = new DateDialog().showAndWait();
		if (result.isPresent()) {
			monitoredItem.actionPerformed(result.get().atStartOfDay());
			PropertyMonitor.getInstance().replaceItem(monitoredItem);
		}
	}
}
