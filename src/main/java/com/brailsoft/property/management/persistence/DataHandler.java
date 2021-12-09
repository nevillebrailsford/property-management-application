package com.brailsoft.property.management.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;

import javafx.event.ActionEvent;

public abstract class DataHandler {
	private static final String CLASS_NAME = DataHandler.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	protected File archiveFile;
	private static List<StorageListener> listeners = new ArrayList<>();

	public DataHandler(File archiveFile) {
		LOGGER.entering(CLASS_NAME, "init", archiveFile.getAbsolutePath());
		this.archiveFile = archiveFile;
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public static void addStorageListener(StorageListener listener) {
		listeners.add(listener);
	}

	public static void removeStorageListener(StorageListener listener) {
		listeners.remove(listener);
	}

	void tellListeners(ActionEvent event) {
		listeners.stream().forEach(listener -> listener.actionComplete(event));
	}
}
