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

}
