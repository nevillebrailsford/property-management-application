package com.brailsoft.property.management.dialog;

import java.io.File;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.preference.ApplicationPreferences;
import com.brailsoft.property.management.preference.PreferencesData;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class PreferencesDialog extends Dialog<PreferencesData> {
	private static final String CLASS_NAME = PreferencesDialog.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private BooleanExpression invalidInput;

	private Label label1 = new Label("Root directory:");
	private TextField directory = new TextField();
	private Button selectDirectory = new Button("Select Directory");

	public PreferencesDialog() {
		LOGGER.entering(CLASS_NAME, "init");
		setTitle("Preferences");
		setHeaderText("Complete details below to set preferences.");
		setResizable(true);

		GridPane grid = new GridPane();
		grid.setHgap(10.0);
		grid.setVgap(10.0);
		grid.add(label1, 1, 1);
		grid.add(directory, 2, 1);
		grid.add(selectDirectory, 3, 1);
		getDialogPane().setContent(grid);

		selectDirectory.setOnAction((event) -> {
			LOGGER.entering(CLASS_NAME, "onAction", event);
			DirectoryChooser directoryChooser = new DirectoryChooser();
			String currentDirectory = ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory();
			LOGGER.fine("currentDirectory=" + currentDirectory);
			if (!(currentDirectory == null || currentDirectory.isBlank() || currentDirectory.isEmpty())) {
				directoryChooser.setInitialDirectory(new File(currentDirectory));
				directory.textProperty().set(currentDirectory);
			} else {
				directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				directory.textProperty().set(System.getProperty("user.home"));
			}
			File chosenDirectory = directoryChooser.showDialog(this.getOwner());
			LOGGER.fine("chosenDirectory=" + chosenDirectory);
			if (chosenDirectory != null) {
				directory.textProperty().set(chosenDirectory.getAbsolutePath());
			}
			LOGGER.exiting(CLASS_NAME, "onAction", chosenDirectory);
		});
		String currentDirectory = ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory();
		if (!(currentDirectory == null || currentDirectory.isBlank() || currentDirectory.isEmpty())) {
			directory.textProperty().set(currentDirectory);
		} else {
			directory.textProperty().set(System.getProperty("user.home"));
		}

		ButtonType buttonTypeOk = new ButtonType("Set Preferences", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.NO);
		getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
		getDialogPane().lookupButton(buttonTypeOk).disableProperty().bind(invalidInputProperty());

		setResultConverter(new Callback<ButtonType, PreferencesData>() {

			@Override
			public PreferencesData call(ButtonType param) {
				LOGGER.entering(CLASS_NAME, "call");
				if (param == buttonTypeOk) {
					PreferencesData data = new PreferencesData();
					data.setDirectory(directory.textProperty().get());
					LOGGER.exiting(CLASS_NAME, "call", data);
					return data;
				}
				LOGGER.exiting(CLASS_NAME, "call");
				return null;
			}
		});
		LOGGER.exiting(CLASS_NAME, "init");
	}

	private BooleanExpression invalidInputProperty() {
		if (invalidInput == null) {
			invalidInput = Bindings.createBooleanBinding(() -> isEmpty(directory), directory.textProperty());
		}
		return invalidInput;
	}

	private boolean isEmpty(TextField textField) {
		return textField.textProperty().get().isBlank() || textField.textProperty().get().isEmpty();
	}

}
