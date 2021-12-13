package com.brailsoft.property.management.edit;

import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

public class RemovePropertyChange extends AbstractChange {
	private static final String CLASS_NAME = RemovePropertyChange.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private Property property;

	public RemovePropertyChange(Property property) {
		this.property = property;
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
		PropertyMonitor.getInstance().addProperty(property);
		PropertyMonitor.getInstance().auditAddProperty(property);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		PropertyMonitor.getInstance().removeProperty(property);
		PropertyMonitor.getInstance().auditRemoveProperty(property);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

}
