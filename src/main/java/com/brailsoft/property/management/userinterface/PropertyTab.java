package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PropertyTab extends Tab {
	Property property;

	Image tick = new Image(getClass().getResourceAsStream("tick-16.png"));

	private ItemTableView tableView;

	private Button actionComplete;

	public PropertyTab(Property property) {
		super();
		this.property = new Property(property);

		this.setText(property.getAddress().getPostCode().toString());
		VBox content = new VBox();
		this.setContent(content);
		AddressHBox addressHBox = new AddressHBox(property.getAddress());
		content.getChildren().add(addressHBox);

		tableView = new ItemTableView();
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
						tableView.replaceItem(monitoredItem);
					}
				} else if (change.wasAdded()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						tableView.addItem(monitoredItem);
					}
				} else if (change.wasRemoved()) {
					for (MonitoredItem monitoredItem : change.getRemoved()) {
						tableView.removeItem(monitoredItem);
					}
				}
			}
		}
	};

	public ListChangeListener<? super MonitoredItem> getListener() {
		return listener;
	}

}
