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
import com.brailsoft.property.management.userinterface.PropertyTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane;

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
					PropertyTab tab = new PropertyTab(p);
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
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText("Property Management \nVersion 1.0.0\nBuild date: 26/10/2021");
		alert.setTitle("About Property Management");
		alert.setHeaderText("Property Management");
		alert.showAndWait();
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
			PropertyTab selectedTab = (PropertyTab) tabPane.getSelectionModel().getSelectedItem();
			Property property = selectedTab.getProperty();
			item.setOwner(property);
			propertyMonitor.addItem(item);
		}
	}

	@FXML
	void exitApplication(ActionEvent event) {
		propertyManager.shutdown();
	}

}
