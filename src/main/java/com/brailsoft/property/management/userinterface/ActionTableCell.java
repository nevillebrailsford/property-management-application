package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.brailsoft.property.management.constant.DateFormats;
import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.TableCell;

public class ActionTableCell extends TableCell<MonitoredItem, String> {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormats.dateFormatForUI);
	private String backgroundColor;

	public ActionTableCell(String backgroundColor) {
		super();
		this.backgroundColor = backgroundColor;
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		setGraphic(null);
		if (!empty) {
			setText(item);
			LocalDate givendate = LocalDate.parse(item, formatter);
			LocalDate currentdate = LocalDate.now();
			if (currentdate.isAfter(givendate)) {
				setStyle("-fx-background-color: " + backgroundColor + ";");
			}
		} else {
			setText(null);
			setStyle(null);
		}
	}
}
