package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;

import com.brailsoft.property.management.model.MonitoredItem;

import javafx.scene.control.TableView;

public abstract class MonitoredItemsTableBase extends TableView<MonitoredItem> {

	public void addItem(MonitoredItem item) {
		LocalDate itemTime = item.getTimeForNextAction();
		int positionToInsert = -1;
		for (int i = getItems().size() - 1; i >= 0; i--) {
			LocalDate listTime = getItems().get(i).getTimeForNextAction();
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

}
