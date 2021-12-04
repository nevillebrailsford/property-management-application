package com.brailsoft.property.management.print;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class ItemPrintBox extends PrintBox {
	private MonitoredItem monitoredItem;
	Label lastActionLabel;
	Label nextActionLabel;
	Label nextNoticeLabel;

	public ItemPrintBox(MonitoredItem monitoredtem) {
		super();
		this.monitoredItem = monitoredtem;
		this.setSpacing(5);
		createGUI();
	}

	@Override
	protected void setStyles() {
		setNextActionStyle();
		setNextNoticeStyle();
	}

	@Override
	protected void bind() {
		lastActionLabel.textProperty().bind(monitoredItem.lastActionProperty());
		nextActionLabel.textProperty().bind(monitoredItem.nextActionProperty());
		nextNoticeLabel.textProperty().bind(monitoredItem.nextNoticeProperty());
	}

	@Override
	protected void makeLabels() {
		makeDescriptionLabel();
		createDivider();
		makeLastActionLabels();
		createDivider();
		makeNextActionLabels();
		createDivider();
		makeNextNoticeLabels();
	}

	private void makeDescriptionLabel() {
		Label label = new Label(monitoredItem.getDescription());
		label.setFont(new Font(FONT_SIZE));
		getChildren().add(label);
	}

	private void makeNextNoticeLabels() {
		Label label;
		label = new Label("next notice: ");
		label.setFont(new Font(FONT_SIZE));
		getChildren().add(label);
		nextNoticeLabel = new Label();
		getChildren().add(nextNoticeLabel);
	}

	private void makeNextActionLabels() {
		Label label;
		label = new Label("next action: ");
		label.setFont(new Font(FONT_SIZE));
		getChildren().add(label);
		nextActionLabel = new Label();
		getChildren().add(nextActionLabel);
	}

	private void makeLastActionLabels() {
		Label label;
		label = new Label("last action: ");
		label.setFont(new Font(FONT_SIZE));
		getChildren().add(label);
		lastActionLabel = new Label();
		lastActionLabel.setFont(new Font(FONT_SIZE));
		getChildren().add(lastActionLabel);
	}

	private void setNextNoticeStyle() {
		if (this.monitoredItem.noticeDue()) {
			nextNoticeLabel.setFont(new Font(FONT_SIZE));
		} else {
			nextNoticeLabel.setFont(new Font(FONT_SIZE));
		}
	}

	private void setNextActionStyle() {
		if (this.monitoredItem.overdue() || this.monitoredItem.noticeDue()) {
			nextActionLabel.setFont(new Font(FONT_SIZE));
		} else {
			nextActionLabel.setFont(new Font(FONT_SIZE));
		}
	}
}
