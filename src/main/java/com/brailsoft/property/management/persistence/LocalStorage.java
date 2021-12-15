package com.brailsoft.property.management.persistence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
		this.directory = directory;
		if (!directory.exists()) {
			directory.mkdirs();
		}
		LOGGER.exiting(CLASS_NAME, "updateDirectory");
	}

	public void loadArchivedData() throws IOException {
		LOGGER.entering(CLASS_NAME, "loadArchivedData");
		File archiveFile = new File(directory, FILE_NAME);
		if (!archiveFile.exists()) {
			IOException exc = new IOException(
					"LocalStorage: archiveFile " + archiveFile.getAbsolutePath() + " not found");
			LOGGER.throwing(CLASS_NAME, "loadArchivedData", exc);
			LOGGER.exiting(CLASS_NAME, "loadArchivedData");
			throw exc;
		}
		LoadData worker = new LoadData(archiveFile);
		Future<String> future = executor.submit(worker);
		LOGGER.exiting(CLASS_NAME, "loadArchivedData");
	}

	public void saveArchiveData() throws IOException {
		LOGGER.entering(CLASS_NAME, "saveArchivedData");
		File archiveFile = new File(directory, FILE_NAME);
		SaveData worker = new SaveData(archiveFile);
		Future<String> future = executor.submit(worker);
		try {
			LOGGER.fine("Issuing get");
			String result = future.get();
			System.out.println(result);
		} catch (InterruptedException | ExecutionException e) {
		}
		LOGGER.exiting(CLASS_NAME, "saveArchivedData");
	}

	public File getDirectory() {
		return directory;
	}

}
