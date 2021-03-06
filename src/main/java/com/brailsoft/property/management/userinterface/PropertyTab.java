package com.brailsoft.property.management.userinterface;

import java.time.LocalDate;
import java.util.Optional;

import com.brailsoft.property.management.dialog.DateDialog;
import com.brailsoft.property.management.edit.ChangeManager;
import com.brailsoft.property.management.edit.ReplaceMonitoredChange;
import com.brailsoft.property.management.model.InventoryItem;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PropertyTab extends Tab {
	Property property;

	Image tick = new Image(getClass().getResourceAsStream("tick-16.png"));

	private ItemTableView itemTableView;
	private InventoryTableView inventoryTableView;
	private TabPane content;

	private Button actionComplete;

	public PropertyTab(Property property) {
		super();
		this.property = new Property(property);

		this.setText(property.getAddress().getPostCode().toString());

		content = new TabPane();
		this.setContent(content);

		createItemTab(property);

		createInventoryTab(property);

		PropertyMonitor.getInstance().addListener(itemListener, property);
		PropertyMonitor.getInstance().addInventoryListener(inventoryListener, property);
	}

	private void createItemTab(Property property) {
		Tab itemTab = new Tab();
		itemTab.setText("Items");
		content.getTabs().add(itemTab);

		VBox vboxContent = new VBox();
		itemTab.setContent(vboxContent);

		AddressHBox addressHBox = new AddressHBox(property.getAddress());
		vboxContent.getChildren().add(addressHBox);

		itemTableView = new ItemTableView();
		vboxContent.getChildren().add(itemTableView);

		ButtonBar buttonBar = createItemButtonBar();
		vboxContent.getChildren().add(buttonBar);
	}

	private void createInventoryTab(Property property) {
		Tab inventoryTab = new Tab();
		inventoryTab.setText("Inventory");
		content.getTabs().add(inventoryTab);

		VBox vboxContent = new VBox();
		inventoryTab.setContent(vboxContent);

		AddressHBox addressHBox = new AddressHBox(property.getAddress());
		vboxContent.getChildren().add(addressHBox);

		inventoryTableView = new InventoryTableView();
		vboxContent.getChildren().add(inventoryTableView);

	}

	private ButtonBar createItemButtonBar() {
		ButtonBar buttonBar = new ButtonBar();
		ImageView imageView = new ImageView(tick);
		actionComplete = new Button("Mark Complete", imageView);
		actionComplete.setOnAction(event -> {
			recordActionComplete();
		});
		actionComplete.setDisable(true);
		actionComplete.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			return itemTableView.getSelectionModel().selectedItemProperty().get() == null;
		}, itemTableView.getSelectionModel().selectedItemProperty()));

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
		MonitoredItem item = itemTableView.getSelectionModel().getSelectedItem();
		Optional<LocalDate> result = new DateDialog(item).showAndWait();
		if (result.isPresent()) {
			MonitoredItem before = new MonitoredItem(item);
			item.actionPerformed(result.get());
			ReplaceMonitoredChange replaceMonitoredChange = new ReplaceMonitoredChange(before, item);
			ChangeManager.getInstance().execute(replaceMonitoredChange);
		}
	}

	private void clearSelection() {
		itemTableView.getSelectionModel().clearSelection();
	}

	private ListChangeListener<? super MonitoredItem> itemListener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends MonitoredItem> change) {
			while (change.next()) {
				if (change.wasReplaced()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						itemTableView.replaceItem(monitoredItem);
					}
				} else if (change.wasAdded()) {
					for (MonitoredItem monitoredItem : change.getAddedSubList()) {
						itemTableView.addItem(monitoredItem);
					}
				} else if (change.wasRemoved()) {
					for (MonitoredItem monitoredItem : change.getRemoved()) {
						itemTableView.removeItem(monitoredItem);
					}
				}
			}
		}
	};

	public ListChangeListener<? super MonitoredItem> getItemListener() {
		return itemListener;
	}

	private ListChangeListener<? super InventoryItem> inventoryListener = new ListChangeListener<>() {

		@Override
		public void onChanged(Change<? extends InventoryItem> change) {
			while (change.next()) {
				if (change.wasReplaced()) {
					for (InventoryItem inventoryItem : change.getAddedSubList()) {
						inventoryTableView.replaceItem(inventoryItem);
					}
				} else if (change.wasAdded()) {
					for (InventoryItem inventoryItem : change.getAddedSubList()) {
						inventoryTableView.addItem(inventoryItem);
					}
				} else if (change.wasRemoved()) {
					for (InventoryItem inventoryItem : change.getRemoved()) {
						inventoryTableView.removeItem(inventoryItem);
					}
				}
			}
		}
	};

	public ListChangeListener<? super InventoryItem> getInventoryListener() {
		return inventoryListener;
	}
}
