package com.brailsoft.property.management.launcher;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.controller.PropertyManagerController;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PropertyManager extends Application {
	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);

	private static PropertyManagerController mainController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (firstUse()) {
			String selectedDirectory = selectDirectory();
			if (selectedDirectory.isBlank() || selectedDirectory.isBlank()) {
				Platform.exit();
			}
			try {
				tellPreferencesChosenDirectoryIs(selectedDirectory);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				Platform.exit();
			}
		}
		LoadProperty loadProperty = loadFXML("PropertyManager");
		Scene scene = new Scene(loadProperty.getParent());
		scene.getStylesheets().add(getClass().getResource("PropertyManager.css").toExternalForm());
		mainController = loadProperty.getLoader().getController();
		mainController.setPropertyManager(this);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Property Management");
		primaryStage.setResizable(false);
		primaryStage.show();
		scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if (!mainController.shutdown()) {
					event.consume();
				} else {
					performShutdown();
				}

			}
		});
	}

	public void shutdown() {
		if (mainController.shutdown()) {
			performShutdown();
		}
	}

	private void performShutdown() {
		mainController.saveIniFile();
		Platform.exit();
	}

	public static LoadProperty loadFXML(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader(PropertyManager.class.getResource(fxml + ".fxml"));
		Parent root = loader.load();
		return new LoadProperty(loader, root);
	}

	public static void main(String[] args) {
		launch(args);
	}

	private boolean firstUse() {
		boolean result = false;
		String directory = applicationPreferences.getDirectory();
		if (directory == null || directory.isBlank() || directory.isEmpty()) {
			result = true;
		}
		return result;
	}

	private String selectDirectory() {
		String result = "";
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Directory Selection");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedDirectory = directoryChooser.showDialog(null);
		if (selectedDirectory != null) {
			result = selectedDirectory.getAbsolutePath();
		}
		return result;
	}

	private void tellPreferencesChosenDirectoryIs(String directory) throws BackingStoreException {
		applicationPreferences.setDirectory(directory);
	}
}
