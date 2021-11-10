package com.brailsoft.property.management.dialog;

import java.time.LocalDate;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class DateDialog extends Dialog<LocalDate> {

	private BooleanExpression invalidInput;

	private Label label1 = new Label("Item description:");
	private TextField description = new TextField();
	private Label label2 = new Label("Date completed:");
	private DatePicker eventComplete = new DatePicker();

	public DateDialog(MonitoredItem item) {
		setTitle("Record event complete");
		setHeaderText("Enter the date below to record when the event was complete");
		setResizable(true);

		description.setEditable(false);
		description.textProperty().set(item.getDescription());

		GridPane grid = new GridPane();
		grid.setHgap(10.0);
		grid.setVgap(10.0);
		grid.add(label1, 1, 1);
		grid.add(description, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(eventComplete, 2, 2);
		getDialogPane().setContent(grid);

		ButtonType buttonTypeOk = new ButtonType("Mark Complete", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);
		getDialogPane().lookupButton(buttonTypeOk).disableProperty().bind(invalidInputProperty());

		eventComplete.setValue(LocalDate.now());

		setResultConverter(new Callback<ButtonType, LocalDate>() {

			@Override
			public LocalDate call(ButtonType param) {
				if (param == buttonTypeOk) {
					return eventComplete.getValue();
				}
				return null;
			}
		});

	}

	private BooleanExpression invalidInputProperty() {
		if (invalidInput == null) {
			invalidInput = Bindings.createBooleanBinding(() -> !hasValue(eventComplete), eventComplete.valueProperty());
		}
		return invalidInput;
	}

	private boolean hasValue(DatePicker datePicker) {
		return datePicker.valueProperty().get() != null;
	}
}
