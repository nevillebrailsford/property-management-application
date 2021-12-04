package com.brailsoft.property.management.print;

import javafx.geometry.Orientation;
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

public abstract class PrintBox extends HBox {

	protected static final double FONT_SIZE = 10.0;

	protected void createGUI() {
		makeSpace();
		makeLabels();
		makeSpace();
		bind();
		setStyles();
		setOuterBorder();
	}

	protected void createDivider() {
		getChildren().add(new Separator(Orientation.VERTICAL));
	}

	protected void makeSpace() {
		Pane pane = new Pane();
		HBox.setHgrow(pane, Priority.ALWAYS);
		getChildren().add(pane);
	}

	protected abstract void makeLabels();

	protected abstract void bind();

	protected abstract void setStyles();

	protected void setOuterBorder() {
		setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
	}

}
