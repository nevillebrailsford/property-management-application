package com.brailsoft.property.management.launcher;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.controller.PropertyManagerController;
import com.brailsoft.property.management.dialog.PreferencesDialog;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.preference.ApplicationPreferences;
import com.brailsoft.property.management.preference.PreferencesData;
import com.brailsoft.property.management.timer.Timer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PropertyManager extends Application {
	private static final String CLASS_NAME = PropertyManager.class.getName();
	private static final Logger LOGGER = Logger.getLogger(PropertyManager.class.getName());

	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);

	private static PropertyManagerController mainController;
	private Timer timer;

	@Override
	public void start(Stage primaryStage) throws Exception {
		LOGGER.entering(CLASS_NAME, "start");
		if (firstUse()) {
			PreferencesData preferencesData = makeInitialChoices();
			if (preferencesData == null) {
				LOGGER.exiting(CLASS_NAME, "start");
				Platform.exit();
				System.exit(0);
			}
			String selectedDirectory = preferencesData.getDirectory();
			Level selectedLevel = preferencesData.getLevel();
			if (selectedDirectory.isBlank() || selectedDirectory.isEmpty()) {
				LOGGER.exiting(CLASS_NAME, "start");
				Platform.exit();
				System.exit(0);
			}
			try {
				tellPreferencesChosenLoggingLevelIs(selectedLevel);
				tellPreferencesChosenDirectoryIs(selectedDirectory);
			} catch (Exception e) {
				LOGGER.warning("Caught exception: " + e.getMessage());
				LOGGER.exiting(CLASS_NAME, "start");
				Platform.exit();
				System.exit(0);
			}
		}
		LoadProperty LoadProperty = loadFXML("PropertyManager");
		Scene scene = new Scene(LoadProperty.getParent());
		scene.getStylesheets().add(getClass().getResource("PropertyManager.css").toExternalForm());
		mainController = LoadProperty.getLoader().getController();
		mainController.setPropertyManager(this);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Property Management");
		primaryStage.setResizable(false);
		primaryStage.show();
		scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				performShutdown();
			}
		});
		timer = Timer.getInstance();
		LOGGER.exiting(CLASS_NAME, "start");
	}

	private static void configureLogging() {
		PropertyManagerLogConfigurer.setUp();
	}

	public void shutdown() {
		LOGGER.entering(CLASS_NAME, "shutdown");
		performShutdown();
		LOGGER.exiting(CLASS_NAME, "shutdown");
	}

	private void performShutdown() {
		LOGGER.entering(CLASS_NAME, "performShutdown");
		timer.stop();
		LOGGER.exiting(CLASS_NAME, "performShutdown");
		Platform.exit();
	}

	public static LoadProperty loadFXML(String fxml) throws IOException {
		LOGGER.entering(CLASS_NAME, "loadFXML", fxml);
		FXMLLoader loader = new FXMLLoader(PropertyManager.class.getResource(fxml + ".fxml"));
		Parent root = loader.load();
		LoadProperty LoadProperty = new LoadProperty(loader, root);
		LOGGER.exiting(CLASS_NAME, "loadFXML", LoadProperty);
		return LoadProperty;
	}

	public static void main(String[] args) {
		configureLogging();
		launch(args);
	}

	private boolean firstUse() {
		LOGGER.entering(CLASS_NAME, "firstUse");
		boolean result = false;
		String directory = applicationPreferences.getDirectory();
		if (directory == null || directory.isBlank() || directory.isEmpty()) {
			result = true;
		}
		LOGGER.exiting(CLASS_NAME, "firstUse", result);
		return result;
	}

	private PreferencesData makeInitialChoices() {
		LOGGER.entering(CLASS_NAME, "makeInitialChoices");
		PreferencesData data = null;
		Optional<PreferencesData> result = new PreferencesDialog().showAndWait();
		if (result.isPresent()) {
			data = result.get();
		}
		LOGGER.exiting(CLASS_NAME, "makeInitialChoices", data);
		return data;
	}

	private void tellPreferencesChosenDirectoryIs(String directory) throws BackingStoreException {
		LOGGER.entering(CLASS_NAME, "tellPreferencesChosenDirectoryIs", directory);
		try {
			applicationPreferences.setDirectory(directory);
		} catch (BackingStoreException e) {
			LOGGER.throwing(CLASS_NAME, "tellPreferencesChosenDirectoryIs", e);
			LOGGER.exiting(CLASS_NAME, "tellPreferencesChosenDirectoryIs");
			throw e;
		}
		LOGGER.exiting(CLASS_NAME, "tellPreferencesChosenDirectoryIs");
	}

	private void tellPreferencesChosenLoggingLevelIs(Level level) throws BackingStoreException {
		LOGGER.entering(CLASS_NAME, "tellPreferencesChosenLoggingLevelIs", level);
		try {
			applicationPreferences.setLevel(level);
		} catch (BackingStoreException e) {
			LOGGER.throwing(CLASS_NAME, "tellPreferencesChosenLoggingLevelIs", e);
			LOGGER.exiting(CLASS_NAME, "tellPreferencesChosenLoggingLevelIs");
			throw e;
		}
		LOGGER.exiting(CLASS_NAME, "tellPreferencesChosenLoggingLevelIs");
	}
}
