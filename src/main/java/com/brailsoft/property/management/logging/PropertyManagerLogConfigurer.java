package com.brailsoft.property.management.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class PropertyManagerLogConfigurer {
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);

	public static void setUp() {
		Logger parent = LOGGER;
		while (parent != null) {
			for (Handler handler : parent.getHandlers()) {
				if (handler instanceof ConsoleHandler) {
					parent.removeHandler(handler);
				}
			}
			parent = parent.getParent();
		}
		LOGGER.setLevel(preferences.getLevel());
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(preferences.getLevel());
		handler.setFormatter(new PropertyManagerFormatter());
		LOGGER.addHandler(handler);
		LOGGER.setLevel(preferences.getLevel());
	}
}
