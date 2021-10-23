package com.brailsoft.property.management.dialog;

import java.time.LocalDate;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Period;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class EventDialog extends Dialog<MonitoredItem> {

	public EventDialog() {
		setTitle("Add an Event");
		setHeaderText("Enter the details below to create a new event to be monitored.");
		setResizable(true);

		Label label1 = new Label("Description:");
		Label label2 = new Label("Period:");
		Label label3 = new Label("How many:");
		Label label4 = new Label("Date last actioned:");
		Label label5 = new Label("Notice period:");
		Label label6 = new Label("How many:");
		TextField description = new TextField();
		ChoiceBox<Period> actionPeriod = new ChoiceBox<>();
		actionPeriod.getItems().add(Period.YEARLY);
		actionPeriod.getItems().add(Period.MONTHLY);
		actionPeriod.getItems().add(Period.WEEKLY);
		TextField howMany = new TextField();
		DatePicker lastAction = new DatePicker();
		ChoiceBox<Period> noticePeriod = new ChoiceBox<>();
		noticePeriod.getItems().add(Period.YEARLY);
		noticePeriod.getItems().add(Period.MONTHLY);
		noticePeriod.getItems().add(Period.WEEKLY);
		TextField noticeHowMany = new TextField();

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(description, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(actionPeriod, 2, 2);
		grid.add(label3, 1, 3);
		grid.add(howMany, 2, 3);
		grid.add(label4, 1, 4);
		grid.add(lastAction, 2, 4);
		grid.add(label5, 1, 5);
		grid.add(noticePeriod, 2, 5);
		grid.add(label6, 1, 6);
		grid.add(noticeHowMany, 2, 6);
		getDialogPane().setContent(grid);

		ButtonType buttonTypeOk = new ButtonType("Add Event", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);

		setResultConverter(new Callback<ButtonType, MonitoredItem>() {

			@Override
			public MonitoredItem call(ButtonType param) {
				if (param == buttonTypeOk) {
					String desc = description.getText();
					Period per = actionPeriod.getValue();
					String howM = howMany.getText();
					LocalDate date = lastAction.getValue();
					Period evper = noticePeriod.getValue();
					String evhowM = noticeHowMany.getText();
					return new MonitoredItem(desc, per, Integer.parseInt(howM), date.atStartOfDay(),
							Integer.parseInt(evhowM), evper);
				}
				return null;
			}
		});
	}
}
