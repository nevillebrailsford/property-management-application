package com.brailsoft.property.management.dialog;

import java.io.File;

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

	private BooleanExpression invalidInput;

	private Label label1 = new Label("Root directory:");
	private TextField directory = new TextField();
	private Button selectDirectory = new Button("Select Directory");

	public PreferencesDialog() {
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
			DirectoryChooser directoryChooser = new DirectoryChooser();
			String currentDirectory = ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory();
			if (!(currentDirectory == null || currentDirectory.isBlank() || currentDirectory.isEmpty())) {
				directoryChooser.setInitialDirectory(new File(currentDirectory));
			}
			File chosenDirectory = directoryChooser.showDialog(this.getOwner());
			if (chosenDirectory != null) {
				directory.textProperty().set(chosenDirectory.getAbsolutePath());
			}
		});

		ButtonType buttonTypeOk = new ButtonType("Set Preferences", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.NO);
		getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
		getDialogPane().lookupButton(buttonTypeOk).disableProperty().bind(invalidInputProperty());

		setResultConverter(new Callback<ButtonType, PreferencesData>() {

			@Override
			public PreferencesData call(ButtonType param) {
				if (param == buttonTypeOk) {
					PreferencesData data = new PreferencesData();
					data.setDirectory(directory.textProperty().get());
					return data;
				}
				return null;
			}
		});

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
