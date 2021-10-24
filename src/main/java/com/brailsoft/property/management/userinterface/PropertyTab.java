package com.brailsoft.property.management.userinterface;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
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
		PropertyMonitor.getInstance().addListener(listener, property);
	}

	public Property getProperty() {
		return new Property(property);
	}

	private ListChangeListener<? super MonitoredItem> listener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			change.next();
			if (change.wasAdded()) {
				for (MonitoredItem monitoredItem : change.getAddedSubList()) {
					ItemHBox itemHBox = new ItemHBox(monitoredItem);
					VBox vBox = (VBox) getContent();
					vBox.getChildren().add(itemHBox);
				}
			}
		}
	};
}