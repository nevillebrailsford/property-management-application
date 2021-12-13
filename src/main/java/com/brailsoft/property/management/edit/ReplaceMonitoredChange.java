package com.brailsoft.property.management.edit;

import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.PropertyMonitor;

public class ReplaceMonitoredChange extends AbstractChange {
	private static final String CLASS_NAME = ReplaceMonitoredChange.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private MonitoredItem before;
	private MonitoredItem after;

	public ReplaceMonitoredChange(MonitoredItem before, MonitoredItem after) {
		this.before = before;
		this.after = after;
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
		PropertyMonitor.getInstance().replaceItem(before);
		PropertyMonitor.getInstance().auditReplaceItem(before);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		PropertyMonitor.getInstance().replaceItem(after);
		PropertyMonitor.getInstance().auditReplaceItem(after);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

}
