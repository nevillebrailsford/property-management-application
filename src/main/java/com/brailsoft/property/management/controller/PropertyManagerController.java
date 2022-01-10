package com.brailsoft.property.management.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.dialog.AddInventoryDialog;
import com.brailsoft.property.management.dialog.DeleteInventoryDialog;
import com.brailsoft.property.management.dialog.DeleteItemDialog;
import com.brailsoft.property.management.dialog.EventDialog;
import com.brailsoft.property.management.dialog.PreferencesDialog;
import com.brailsoft.property.management.dialog.PropertyDialog;
import com.brailsoft.property.management.edit.AddInventoryChange;
import com.brailsoft.property.management.edit.AddMonitoredChange;
import com.brailsoft.property.management.edit.AddPropertyChange;
import com.brailsoft.property.management.edit.ChangeManager;
import com.brailsoft.property.management.edit.RemoveInventoryChange;
import com.brailsoft.property.management.edit.RemoveMonitoredChange;
import com.brailsoft.property.management.edit.RemovePropertyChange;
import com.brailsoft.property.management.launcher.LoadProperty;
import com.brailsoft.property.management.launcher.PropertyManager;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.model.InventoryItem;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;
import com.brailsoft.property.management.preference.PreferencesData;
import com.brailsoft.property.management.print.PrintReport;
import com.brailsoft.property.management.userinterface.CalendarView;
import com.brailsoft.property.management.userinterface.PropertyTab;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PropertyManagerController implements Initializable {
	private static final String CLASS_NAME = PropertyManagerController.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private PropertyManager propertyManager;
	private ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);
	private File rootDirectory = new File(applicationPreferences.getDirectory());
	private LocalStorage localStorage = LocalStorage.getInstance(rootDirectory);
	private PropertyMonitor propertyMonitor = PropertyMonitor.getInstance();

	private BooleanProperty propertiesExist = new SimpleBooleanProperty(this, "propertiesExist", false);

	@FXML
	private MenuItem addProperty;

	@FXML
	private MenuItem addItem;

	@FXML
	private MenuItem addInventory;

	@FXML
	private MenuItem deleteProperty;

	@FXML
	private MenuItem deleteItem;

	@FXML
	private MenuItem deleteInventory;

	@FXML
	private TabPane tabPane;

	@FXML
	private MenuItem undo;

	@FXML
	private MenuItem redo;

	@FXML
	private TextField status;

	private ListChangeListener<? super Property> listener = new ListChangeListener<>() {
		@Override
		public void onChanged(Change<? extends Property> change) {
			while (change.next()) {
				if (change.wasAdded()) {
					for (Property p : change.getAddedSubList()) {
						PropertyTab tab = new PropertyTab(p);
						tabPane.getTabs().add(tab);
						tabPane.getSelectionModel().select(tab);
					}
				}
				if (change.wasRemoved()) {
					change.getRemoved().stream().forEach(p -> {
						for (int i = 0; i < tabPane.getTabs().size(); i++) {
							PropertyTab tab = (PropertyTab) tabPane.getTabs().get(i);
							if (tab.getProperty().equals(p)) {
								tabPane.getTabs().remove(i);
								break;
							}
						}
					});
				}
			}
			updatePropertiesExist();
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			StatusMonitor.getInstance(this);
		} catch (Throwable t) {
			System.out.println(t);
			Platform.exit();
		}
		propertyMonitor.addListener(listener);
		try {
			localStorage.loadStoredData();
		} catch (IOException e) {
			if (e.getMessage().startsWith("LocalStorage: archiveFile") && e.getMessage().endsWith("not found")) {
			} else {
				System.out.println(e.getMessage());
			}
		}
		undo.disableProperty().bind(ChangeManager.getInstance().undoableProperty().not());
		redo.disableProperty().bind(ChangeManager.getInstance().redoableProperty().not());
		addItem.disableProperty().bind(propertiesExist.not());
		addInventory.disableProperty().bind(propertiesExist.not());
		deleteProperty.disableProperty().bind(propertiesExist.not());
		deleteItem.disableProperty().bind(propertiesExist.not());
		deleteInventory.disableProperty().bind(propertiesExist.not());
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	public void updateStatus(String status) {
		this.status.textProperty().set(status);
	}

	@FXML
	void about(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "about");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText("Property Management \nVersion 1.0.0\nBuild date: 10/01/2022");
		alert.setTitle("About Property Management");
		alert.setHeaderText("Property Management");
		alert.showAndWait();
		LOGGER.exiting(CLASS_NAME, "about");
	}

	@FXML
	void undo(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "undo");
		ChangeManager.getInstance().undo();
		LOGGER.exiting(CLASS_NAME, "undo");
	}

	@FXML
	void redo(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "redo");
		ChangeManager.getInstance().redo();
		LOGGER.exiting(CLASS_NAME, "redo");
	}

	@FXML
	void addProperty(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "addProperty");
		Optional<Property> result = new PropertyDialog().showAndWait();
		if (result.isPresent()) {
			Property property = result.get();
			AddPropertyChange addPropertyChange = new AddPropertyChange(property);
			ChangeManager.getInstance().execute(addPropertyChange);
		}
		LOGGER.exiting(CLASS_NAME, "addProperty");
	}

	@FXML
	void deleteProperty(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "deleteProperty");
		Property property = getSelectedProperty();
		if (userWantsToDeleteProperty(property) == ButtonType.YES) {
			RemovePropertyChange removePropertyChange = new RemovePropertyChange(property);
			ChangeManager.getInstance().execute(removePropertyChange);
		}
		LOGGER.exiting(CLASS_NAME, "deleteProperty");
	}

	@FXML
	void addItem(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "addItem");
		Optional<MonitoredItem> result = new EventDialog().showAndWait();
		if (result.isPresent()) {
			MonitoredItem item = result.get();
			Property property = getSelectedProperty();
			item.setOwner(property);
			AddMonitoredChange addMonitoredChange = new AddMonitoredChange(item);
			ChangeManager.getInstance().execute(addMonitoredChange);
		}
		LOGGER.exiting(CLASS_NAME, "addItem");
	}

	@FXML
	void deleteItem(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "deleteItem");
		Property property = getSelectedProperty();
		Optional<MonitoredItem> result = new DeleteItemDialog(property).showAndWait();
		if (result.isPresent()) {
			MonitoredItem item = result.get();
			RemoveMonitoredChange removeMonitoredChange = new RemoveMonitoredChange(item);
			ChangeManager.getInstance().execute(removeMonitoredChange);
		}
		LOGGER.exiting(CLASS_NAME, "deleteItem");
	}

	@FXML
	void addInventory(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "addInventory");
		Property property = getSelectedProperty();
		Optional<InventoryItem> result = new AddInventoryDialog(property).showAndWait();
		if (result.isPresent()) {
			InventoryItem item = result.get();
			item.setOwner(property);
			AddInventoryChange addInventoryChange = new AddInventoryChange(item);
			ChangeManager.getInstance().execute(addInventoryChange);
		}
		LOGGER.exiting(CLASS_NAME, "addInventory");
	}

	@FXML
	void deleteInventory(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "deleteInventory");
		Property property = getSelectedProperty();
		Optional<InventoryItem> result = new DeleteInventoryDialog(property).showAndWait();
		if (result.isPresent()) {
			InventoryItem item = result.get();
			RemoveInventoryChange removeInventoryChange = new RemoveInventoryChange(item);
			ChangeManager.getInstance().execute(removeInventoryChange);
		}
		LOGGER.exiting(CLASS_NAME, "deleteInventory");
	}

	@FXML
	void exitApplication(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "exitApplication");
		propertyManager.shutdown();
		LOGGER.exiting(CLASS_NAME, "exitApplication");
	}

	@FXML
	void viewAllItems(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "viewAllItems");
		Stage stage = new Stage();
		stage.setTitle("All Items");
		Scene scene;
		try {
			LoadProperty LoadProperty = PropertyManager.loadFXML("AllItems");
			scene = new Scene(LoadProperty.getParent());
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IllegalArgumentException exc = new IllegalArgumentException("PropertyManagerController: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "viewAllItems", exc);
			LOGGER.exiting(CLASS_NAME, "viewAllItems");
			throw exc;
		}
		scene.getStylesheets().add(PropertyManager.class.getResource("PropertyManager.css").toExternalForm());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.show();
		LOGGER.exiting(CLASS_NAME, "viewAllItems");
	}

	@FXML
	void viewOverdueItems(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "viewOverdueItems");
		Stage stage = new Stage();
		stage.setTitle("Overdue Items");
		Scene scene;
		try {
			LoadProperty LoadProperty = PropertyManager.loadFXML("OverdueItems");
			scene = new Scene(LoadProperty.getParent());
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IllegalArgumentException exc = new IllegalArgumentException("PropertyManagerController: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "viewOverdueItems", exc);
			LOGGER.exiting(CLASS_NAME, "viewOverdueItems");
			throw exc;
		}
		scene.getStylesheets().add(PropertyManager.class.getResource("PropertyManager.css").toExternalForm());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.show();
		LOGGER.exiting(CLASS_NAME, "viewOverdueItems");
	}

	@FXML
	void viewNotifiedItems(ActionEvent event) {
		LOGGER.entering(CLASS_NAME, "viewNotifiedItems");
		Stage stage = new Stage();
		stage.setTitle("Notified Items");
		Scene scene;
		try {
			LoadProperty LoadProperty = PropertyManager.loadFXML("NotifiedItems");
			scene = new Scene(LoadProperty.getParent());
		} catch (Exception e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			IllegalArgumentException exc = new IllegalArgumentException("PropertyManagerController: " + e.getMessage());
			LOGGER.throwing(CLASS_NAME, "viewNotifiedItems", exc);
			LOGGER.exiting(CLASS_NAME, "viewNotifiedItems");
			throw exc;
		}
		scene.getStylesheets().add(PropertyManager.class.getResource("PropertyManager.css").toExternalForm());
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.show();
		LOGGER.exiting(CLASS_NAME, "viewNotifiedItems");
	}

	@FXML
	void printReport() {
		LOGGER.entering(CLASS_NAME, "printReport");
		PrintReport.printReport((Stage) tabPane.getScene().getWindow());
		LOGGER.exiting(CLASS_NAME, "printReport");
	}

	@FXML
	void printInventory() {
		LOGGER.entering(CLASS_NAME, "");
		PrintReport.printInventory((Stage) tabPane.getScene().getWindow());
		LOGGER.exiting(CLASS_NAME, "");
	}

	@FXML
	void preferences() {
		LOGGER.entering(CLASS_NAME, "preferences");
		Optional<PreferencesData> result = new PreferencesDialog().showAndWait();
		if (result.isPresent()) {
			String newDirectory = result.get().getDirectory();
			Level loggingLevel = result.get().getLevel();
			try {
				ApplicationPreferences applicationPreferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);
				if (!applicationPreferences.getLevel().equals(loggingLevel)) {
					applicationPreferences.setLevel(loggingLevel);
					PropertyManagerLogConfigurer.changeLevel(loggingLevel);
				}
				if (!applicationPreferences.getDirectory().equals(newDirectory)) {
					applicationPreferences.setDirectory(newDirectory);
					removeTabsFromView();
					resetModelToEmpty();
					LocalStorage.getInstance(new File(applicationPreferences.getDirectory())).loadStoredData();
				}
			} catch (BackingStoreException e) {
				LOGGER.warning("Caught exception: " + e.getMessage());
			} catch (IOException e) {
				LOGGER.warning("Caught exception: " + e.getMessage());
			}
		}
		LOGGER.entering(CLASS_NAME, "preferences");
	}

	@FXML
	void viewCalendar() {
		LOGGER.entering(CLASS_NAME, "viewCalendar");
		Stage stage = new Stage();
		stage.setTitle("Calendar");
		CalendarView mainRoot = new CalendarView();
		Scene scene = new Scene(mainRoot, 35 + (53 * 14), 125);
		stage.setScene(scene);
		stage.show();

		LOGGER.exiting(CLASS_NAME, "viewCalendar");
	}

	private void resetModelToEmpty() {
		LOGGER.entering(CLASS_NAME, "resetModelToEmpty");
		PropertyMonitor.getInstance().removeListener(listener);
		PropertyMonitor.getInstance().clear();
		PropertyMonitor.getInstance().addListener(listener);
		LOGGER.exiting(CLASS_NAME, "resetModelToEmpty");
	}

	private void removeTabsFromView() {
		LOGGER.entering(CLASS_NAME, "removeTabsFromView");
		for (Tab t : tabPane.getTabs()) {
			PropertyTab pt = (PropertyTab) t;
			PropertyMonitor.getInstance().removeListener(pt.getItemListener(), pt.getProperty());
			PropertyMonitor.getInstance().removeInventoryListener(pt.getInventoryListener(), pt.getProperty());
		}
		tabPane.getTabs().clear();
		LOGGER.exiting(CLASS_NAME, "removeTabsFromView");
	}

	private ButtonType userWantsToDeleteProperty(Property property) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
		alert.setTitle("Delete Property");
		alert.setHeaderText("Confirm deletion of property");
		alert.setContentText("Are you sure you want to delete page for \n" + property + "?");
		ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
		return result;
	}

	private Property getSelectedProperty() {
		PropertyTab selectedTab = (PropertyTab) tabPane.getSelectionModel().getSelectedItem();
		Property property = selectedTab.getProperty();
		return property;
	}

	private void updatePropertiesExist() {
		propertiesExist.set(!tabPane.getTabs().isEmpty());
	}
}
