package com.brailsoft.property.management.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class PropertyManagerLogConfigurer {
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(Constants.NODE_NAME);
	private static final String LOG_FILE = "property.manager.log";

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

		try {
			String rootDirectory = preferences.getDirectory() + File.separator + LocalStorage.DIRECTORY;
			String logfileName = rootDirectory + File.separator + LOG_FILE;
			FileHandler fileHandler = new FileHandler(logfileName, 8192000, 1, false);
			fileHandler.setFormatter(new PropertyManagerFormatter());
			LOGGER.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		changeLevel(preferences.getLevel());
		LogRecord record = new LogRecord(Level.CONFIG, "setting configuration");
		LOGGER.log(record);
	}

	public static void changeLevel(Level level) {
		Logger parent = LOGGER;
		while (parent != null) {
			for (Handler handler : parent.getHandlers()) {
				handler.setLevel(level);
			}
			parent.setLevel(level);
			parent = parent.getParent();
		}
		LogRecord record = new LogRecord(Level.WARNING, "logging level has been set to " + level);
		LOGGER.log(record);
	}
}
