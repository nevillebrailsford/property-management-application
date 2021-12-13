package com.brailsoft.property.management.edit;

import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.InventoryItem;
import com.brailsoft.property.management.model.PropertyMonitor;

public class AddInventoryChange extends AbstractChange {
	private static final String CLASS_NAME = AddInventoryChange.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private InventoryItem inventoryItem;

	public AddInventoryChange(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	@Override
	protected void doHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "doHook");
		redoHook();
		LOGGER.exiting(CLASS_NAME, "doHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		PropertyMonitor.getInstance().removeItem(inventoryItem);
		PropertyMonitor.getInstance().auditRemoveItem(inventoryItem);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		PropertyMonitor.getInstance().addItem(inventoryItem);
		PropertyMonitor.getInstance().auditAddItem(inventoryItem);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

}
