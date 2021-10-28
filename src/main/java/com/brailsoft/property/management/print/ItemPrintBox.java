package com.brailsoft.property.management.print;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ItemPrintBox extends HBox {
	private static final double FONT_SIZE = 10.0;
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
		setOuterBorder();
	}

	private void makeLabels() {
		makeDescriptionLabel();
		createDivider();
		makeLastActionLabels();
		createDivider();
		makeNextActionLabels();
		createDivider();
		makeNextNoticeLabels();
	}

	private void createDivider() {
		getChildren().add(new Separator(Orientation.VERTICAL));
	}

	private void makeSpace() {
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
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

	private void setOuterBorder() {
		setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
	}
}
