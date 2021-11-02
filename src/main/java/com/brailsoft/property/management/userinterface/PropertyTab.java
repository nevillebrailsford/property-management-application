package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.brailsoft.property.management.dialog.DateDialog;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class PropertyTab extends Tab {
	Property property;

	Image tick = new Image(getClass().getResourceAsStream("tick-16.png"));

	private TableView<MonitoredItem> tableView;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	TableColumn<MonitoredItem, String> monitoredItem = new TableColumn<>("Item");
	TableColumn<MonitoredItem, String> description = new TableColumn<>("Description");
	TableColumn<MonitoredItem, String> dateOf = new TableColumn<>("Date of");
	TableColumn<MonitoredItem, String> lastAction = new TableColumn<>("Last Action");
	TableColumn<MonitoredItem, String> nextNotice = new TableColumn<>("Next Notification");
	TableColumn<MonitoredItem, String> nextAction = new TableColumn<>("Next Action");

	Button actionComplete;

	public PropertyTab(Property property) {
		super();
		this.property = new Property(property);

		this.setText(property.getAddress().getPostCode().toString());
		VBox content = new VBox();
		this.setContent(content);
		AddressHBox addressHBox = new AddressHBox(property.getAddress());
		content.getChildren().add(addressHBox);

		createTable();
		content.getChildren().add(tableView);

		ButtonBar buttonBar = createButtonBar();
		content.getChildren().add(buttonBar);

		PropertyMonitor.getInstance().addListener(listener, property);
	}

	private ButtonBar createButtonBar() {
		ButtonBar buttonBar = new ButtonBar();
		ImageView imageView = new ImageView(tick);
		actionComplete = new Button("Mark Complete", imageView);
		actionComplete.setOnAction(event -> {
			recordActionComplete();
		});
		actionComplete.setDisable(true);
		actionComplete.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			return tableView.getSelectionModel().selectedItemProperty().get() == null;
		}, tableView.getSelectionModel().selectedItemProperty()));

		Button selectNone = new Button("Clear Selection");
		selectNone.setOnAction(event -> {
			clearSelection();
		});
		buttonBar.getButtons().addAll(selectNone, actionComplete);
		return buttonBar;
	}

	private void createTable() {
		description.setCellValueFactory(new PropertyValueFactory<>("description"));
		lastAction.setCellValueFactory(new PropertyValueFactory<>("lastAction"));

		nextNotice.setCellValueFactory(new PropertyValueFactory<>("nextNotice"));
		nextNotice.setCellFactory(new Callback<TableColumn<MonitoredItem, String>, TableCell<MonitoredItem, String>>() {
			@Override
			public TableCell<MonitoredItem, String> call(TableColumn<MonitoredItem, String> param) {
				return new NextNoticeTableCelll();
			}
		});

		nextAction.setCellValueFactory(new PropertyValueFactory<>("nextAction"));
		nextAction.setCellFactory(new Callback<TableColumn<MonitoredItem, String>, TableCell<MonitoredItem, String>>() {
			@Override
			public TableCell<MonitoredItem, String> call(TableColumn<MonitoredItem, String> param) {
				return new NextActionTableCelll();
			}
		});

		monitoredItem.setMinWidth(900);
		description.setMinWidth(450);
		dateOf.setMinWidth(450);
		lastAction.setMinWidth(150);
		nextNotice.setMinWidth(150);
		nextAction.setMinWidth(150);
		dateOf.getColumns().addAll(lastAction, nextNotice, nextAction);
		monitoredItem.getColumns().addAll(description, dateOf);

		tableView = new TableView<>();
		tableView.getColumns().add(monitoredItem);

	}

	public Property getProperty() {
		return new Property(property);
	}

	private void recordActionComplete() {
		Optional<LocalDate> result = new DateDialog().showAndWait();
		if (result.isPresent()) {
			MonitoredItem item = tableView.getSelectionModel().getSelectedItem();
			item.actionPerformed(result.get().atStartOfDay());
			PropertyMonitor propertyMonitor = PropertyMonitor.getInstance();
			propertyMonitor.replaceItem(item);
			propertyMonitor.auditReplaceItem(item);
		}
	}

	private void clearSelection() {
		tableView.getSelectionModel().clearSelection();
	}

	private ListChangeListener<? super MonitoredItem> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			while (change.next()) {
				if (change.wasReplaced()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						for (int i = 0; i < tableView.getItems().size(); i++) {
							if (tableView.getItems().get(i).equals(monitoredItem)) {
								tableView.getItems().set(i, monitoredItem);
								break;
							}
						}
					}
				} else if (change.wasAdded()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						tableView.getItems().add(monitoredItem);
					}
				} else if (change.wasRemoved()) {
					for (MonitoredItem monitoredItem : change.getRemoved()) {
						for (int i = 0; i < tableView.getItems().size(); i++) {
							if (tableView.getItems().get(i).equals(monitoredItem)) {
								tableView.getItems().remove(i);
								break;
							}
						}
					}
				}
			}
		}
	};

	public class NextActionTableCelll extends TableCell<MonitoredItem, String> {

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty) {
				setText(item);
				setGraphic(null);
				LocalDate givendate = LocalDate.parse(item, formatter);
				LocalDate currentdate = LocalDate.now();
				if (currentdate.isAfter(givendate)) {
					this.setStyle("-fx-background-color: red;");
				}
			}
		}
	}

	public class NextNoticeTableCelll extends TableCell<MonitoredItem, String> {

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty) {
				setText(item);
				setGraphic(null);
				LocalDate givendate = LocalDate.parse(item, formatter);
				LocalDate currentdate = LocalDate.now();
				if (currentdate.isAfter(givendate)) {
					this.setStyle("-fx-background-color: orange;");
				}
			}
		}
	}
}
