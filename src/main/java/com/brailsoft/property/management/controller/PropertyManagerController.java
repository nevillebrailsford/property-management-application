package com.brailsoft.property.management.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.brailsoft.property.management.dialog.EventDialog;
import com.brailsoft.property.management.dialog.PropertyDialog;
import com.brailsoft.property.management.launcher.PropertyManager;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.persistence.LocalStorage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class PropertyManagerController implements Initializable {

	private PropertyManager propertyManager;
	private LocalStorage localStorage = LocalStorage.getInstance();
	private PropertyMonitor propertyMonitor = PropertyMonitor.getInstance();

	@FXML
	private TabPane tabPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		propertyMonitor.addListener(change -> {
			change.next();
			if (change.wasAdded()) {
				for (Property p : change.getAddedSubList()) {
					Tab tab = new Tab(p.getAddress().getPostCode().toString(), new VBox(new Label(p.toString())));
					tabPane.getTabs().add(tab);
				}
			}
		});
		try {
			localStorage.loadArchivedData();
		} catch (IOException e) {
			if (e.getMessage().startsWith("LocalStorage: archiveFile") && e.getMessage().endsWith("not found")) {

			} else {
				System.out.println(e.getMessage());
			}
		}

	}

	public boolean shutdown() {
		return true;
	}

	public void saveIniFile() {

	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	@FXML
	void about(ActionEvent event) {
		System.out.println("about");
	}

	@FXML
	void addProperty(ActionEvent event) {
		Optional<Property> result = new PropertyDialog().showAndWait();
		if (result.isPresent()) {
			Property property = result.get();
			propertyMonitor.addProperty(property);
		}
	}

	@FXML
	void addEvent(ActionEvent event) {
		Optional<MonitoredItem> result = new EventDialog().showAndWait();
		if (result.isPresent()) {
			MonitoredItem item = result.get();
			System.out.println(item);
			Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
			System.out.println(selectedTab.getText());
			Property found = null;
			for (Property p : propertyMonitor.getProperties()) {
				if (p.getAddress().getPostCode().toString()
						.equals(tabPane.getSelectionModel().getSelectedItem().getText())) {
					found = p;
				}
			}
			found.addItem(item);
			VBox selectedPane = (VBox) selectedTab.getContent();
			selectedPane.getChildren().add(new Label(item.getDescription()));
			selectedPane.getChildren().add(new Label("last action: " + item.getLastActionPerformed().toString()));
			selectedPane.getChildren().add(new Label("next action:" + item.getTimeForNextAction().toString()));
			selectedPane.getChildren().add(new Label("next notice: " + item.getTimeForNextNotice().toString()));

		}
	}

	@FXML
	void change(ActionEvent event) {
		System.out.println("change");
	}

	@FXML
	void delete(ActionEvent event) {
		System.out.println("delete");
	}

	@FXML
	void exitApplication(ActionEvent event) {
		propertyManager.shutdown();
	}

}
