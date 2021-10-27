package com.brailsoft.property.management.launcher;

import java.io.IOException;

import com.brailsoft.property.management.controller.PropertyManagerController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PropertyManager extends Application {
	private static PropertyManagerController mainController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(loadFXMLAndSetMainController("PropertyManager"));
		scene.getStylesheets().add(getClass().getResource("PropertyManager.css").toExternalForm());
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

	public static Parent loadFXMLAndSetMainController(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader(PropertyManager.class.getResource(fxml + ".fxml"));
		Parent root = loader.load();
		Object controller = loader.getController();
		if (controller instanceof PropertyManagerController) {
			setMainController((PropertyManagerController) controller);
		}
		return root;
	}

	private static void setMainController(PropertyManagerController controller) {
		mainController = controller;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
