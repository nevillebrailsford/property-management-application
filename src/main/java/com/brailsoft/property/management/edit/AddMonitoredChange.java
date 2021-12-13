package com.brailsoft.property.management.edit;

import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.PropertyMonitor;

public class AddMonitoredChange extends AbstractChange {
	private static final String CLASS_NAME = AddMonitoredChange.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private MonitoredItem monitoredItem;

	public AddMonitoredChange(MonitoredItem monitoredItem) {
		this.monitoredItem = monitoredItem;
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
		PropertyMonitor.getInstance().removeItem(monitoredItem);
		PropertyMonitor.getInstance().auditRemoveItem(monitoredItem);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		PropertyMonitor.getInstance().addItem(monitoredItem);
		PropertyMonitor.getInstance().auditAddItem(monitoredItem);
		LOGGER.exiting(CLASS_NAME, "reHook");
	}

}
