package com.brailsoft.property.management.userinterface;

import java.time.LocalDateTime;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class WeekDaysColumn extends VBox {
	private Pane corner = new Pane();

	public WeekDaysColumn() {
		super();
		setSpacing(10);
		corner.setPrefHeight(22);
		getChildren().add(corner);
		LocalDateTime daysdate = LocalDateTime.now();
		for (int i = 0; i < 7; i++) {
			String day = daysdate.getDayOfWeek().name();
			if (i % 2 == 1) {
				getChildren().add(new Label(day.substring(0, 1) + day.substring(1, 3).toLowerCase()));
			}
			daysdate = daysdate.plusDays(1);
		}
	}
}
