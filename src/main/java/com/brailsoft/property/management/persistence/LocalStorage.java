package com.brailsoft.property.management.persistence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.launcher.PropertyManager;

public class LocalStorage {
	private static final String CLASS_NAME = LocalStorage.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	public static final String DIRECTORY = "property.management";
	public static final String FILE_NAME = "property.dat";

	private static LocalStorage instance = null;

	private File directory = null;
	private ExecutorService executor = PropertyManager.executor();

	public synchronized static LocalStorage getInstance(File rootDirectory) {
		if (rootDirectory == null) {
			throw new IllegalArgumentException("LocalStorage: rootDirectory was null");
		}
		if (instance == null) {
			instance = new LocalStorage();
			instance.updateDirectory(new File(rootDirectory, DIRECTORY));
		} else {
			instance.updateDirectory(new File(rootDirectory, DIRECTORY));
		}
		return instance;
	}

	private LocalStorage() {
	}

	private void updateDirectory(File directory) {
		LOGGER.entering(CLASS_NAME, "updateDirectory", directory);
		this.directory = new File(directory, Constants.MODEL_DIRECTORY);
		if (!this.directory.exists()) {
			this.directory.mkdirs();
		}
		LOGGER.exiting(CLASS_NAME, "updateDirectory");
	}

	public void loadStoredData() throws IOException {
		LOGGER.entering(CLASS_NAME, "loadStoredData");
		File archiveFile = new File(directory, FILE_NAME);
		if (!archiveFile.exists()) {
			IOException exc = new IOException(
					"LocalStorage: archiveFile " + archiveFile.getAbsolutePath() + " not found");
			LOGGER.throwing(CLASS_NAME, "loadStoredData", exc);
			LOGGER.exiting(CLASS_NAME, "loadStoredData");
			throw exc;
		}
		LoadData worker = new LoadData(archiveFile);
		executor.execute(worker);
		LOGGER.exiting(CLASS_NAME, "loadArchivedData");
	}

	public void storeData() throws IOException {
		LOGGER.entering(CLASS_NAME, "storeData");
		File archiveFile = new File(directory, FILE_NAME);
		SaveData worker = new SaveData(archiveFile);
		executor.execute(worker);
		LOGGER.exiting(CLASS_NAME, "storeData");
	}

	public File getDirectory() {
		return directory;
	}

}
