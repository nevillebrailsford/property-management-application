package com.brailsoft.property.management.userinterface;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PropertyTab extends Tab {
	Property property;

	public PropertyTab(Property property) {
		super();
		this.property = new Property(property);
		this.setText(property.getAddress().getPostCode().toString());
		VBox content = new VBox();
		this.setContent(content);
		AddressHBox addressHBox = new AddressHBox(property.getAddress());
		content.getChildren().add(addressHBox);
		Pane pane = new Pane();
		VBox.setVgrow(pane, Priority.ALWAYS);
		content.getChildren().add(pane);
		pane = new Pane();
		VBox.setVgrow(pane, Priority.ALWAYS);
		content.getChildren().add(pane);
		PropertyMonitor.getInstance().addListener(listener, property);
	}

	public Property getProperty() {
		return new Property(property);
	}

	private ListChangeListener<? super MonitoredItem> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			while (change.next()) {
				if (change.wasReplaced()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						for (int index = 1; index < ((VBox) getContent()).getChildren().size(); index++) {
							ItemHBox itemHBox = (ItemHBox) ((VBox) getContent()).getChildren().get(index);
							if (itemHBox.getMonitoredItem().equals(monitoredItem)) {
								itemHBox.refresh(monitoredItem);
							}
						}
					}
				} else if (change.wasAdded()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						ItemHBox itemHBox = new ItemHBox(monitoredItem);
						VBox vBox = (VBox) getContent();
						vBox.getChildren().add(vBox.getChildren().size() - 1, itemHBox);
					}
				} else if (change.wasRemoved()) {
					for (MonitoredItem monitoredItem : change.getRemoved()) {
						for (int index = 1; index < ((VBox) getContent()).getChildren().size(); index++) {
							ItemHBox itemHBox = (ItemHBox) ((VBox) getContent()).getChildren().get(index);
							if (itemHBox.getMonitoredItem().equals(monitoredItem)) {
								((VBox) getContent()).getChildren().remove(index);
								break;
							}
						}
					}
				}
			}
		}
	};
}
