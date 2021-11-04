package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ItemTableView extends TableView<MonitoredItem> {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	TableColumn<MonitoredItem, String> monitoredItem = new TableColumn<>("Item");
	TableColumn<MonitoredItem, String> description = new TableColumn<>("Description");
	TableColumn<MonitoredItem, String> dateOf = new TableColumn<>("Date of");
	TableColumn<MonitoredItem, String> lastAction = new TableColumn<>("Last Action");
	TableColumn<MonitoredItem, String> nextNotice = new TableColumn<>("Next Notification");
	TableColumn<MonitoredItem, String> nextAction = new TableColumn<>("Next Action");

	public ItemTableView() {
		super();
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
		getColumns().add(monitoredItem);
	}

	public void addItem(MonitoredItem item) {
		LocalDateTime itemTime = item.getTimeForNextAction();
		int positionToInsert = -1;
		for (int i = getItems().size() - 1; i >= 0; i--) {
			LocalDateTime listTime = getItems().get(i).getTimeForNextAction();
			if (itemTime.isBefore(listTime)) {
				positionToInsert = i;
				break;
			}
		}
		if (positionToInsert == -1) {
			getItems().add(item);
		} else {
			getItems().add(positionToInsert, item);
		}
	}

	public void replaceItem(MonitoredItem item) {
		for (int i = 0; i < getItems().size(); i++) {
			if (getItems().get(i).equals(item)) {
				getItems().set(i, item);
				break;
			}
		}
	}

	public void removeItem(MonitoredItem item) {
		for (int i = 0; i < getItems().size(); i++) {
			if (getItems().get(i).equals(item)) {
				getItems().remove(i);
				break;
			}
		}

	}

	private class NextActionTableCelll extends TableCell<MonitoredItem, String> {

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty) {
				setText(item);
				setGraphic(null);
				LocalDate givendate = LocalDate.parse(item, formatter);
				LocalDate currentdate = LocalDate.now();
				if (currentdate.isAfter(givendate)) {
					setStyle("-fx-background-color: red;");
				}
			} else {
				setText("");
				setStyle("");
			}
		}
	}

	private class NextNoticeTableCelll extends TableCell<MonitoredItem, String> {

		@Override
		protected void updateItem(String item, boolean empty) {
			if (!empty) {
				setText(item);
				setGraphic(null);
				LocalDate givendate = LocalDate.parse(item, formatter);
				LocalDate currentdate = LocalDate.now();
				if (currentdate.isAfter(givendate)) {
					setStyle("-fx-background-color: orange;");
				}
			} else {
				setText("");
				setStyle("");
			}
			System.out.println(getStyle());

		}
	}
}
