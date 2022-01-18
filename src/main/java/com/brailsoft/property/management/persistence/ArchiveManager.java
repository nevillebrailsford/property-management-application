package com.brailsoft.property.management.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.launcher.PropertyManager;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class ArchiveManager {
	private static final String CLASS_NAME = ArchiveManager.class.getName();
	private static final Logger LOGGER = Logger.getLogger(PropertyManager.class.getName());

	private static ArchiveManager instance = null;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-uuuu-MM-dd-HH-mm-ss");

	public synchronized static ArchiveManager getInstance() {
		if (instance == null) {
			instance = new ArchiveManager();
		}
		return instance;
	}

	private ArchiveManager() {
	}

	public void archive(String directory) throws IOException {
		LOGGER.entering(CLASS_NAME, "archive", directory);
		if (directory == null) {
			LOGGER.exiting(CLASS_NAME, "archive");
			throw new InvalidParameterException("ArchiveManager: directory was null");
		}
		File activeDirectory = ApplicationPreferences.getInstance(Constants.NODE_NAME).getActiveDirectory(directory);
		File modelDirectory = new File(activeDirectory, Constants.MODEL_DIRECTORY);
		File activeFile = new File(modelDirectory, LocalStorage.FILE_NAME);
		if (!activeFile.exists()) {
			LOGGER.fine("File " + activeFile.getAbsolutePath() + " was not found");
			LOGGER.exiting(CLASS_NAME, "archive");
			return;
		}
		File archiveDirectory = ApplicationPreferences.getInstance(Constants.NODE_NAME).getArchiveDirectory(directory);
		File archiveFile = new File(archiveDirectory, LocalStorage.FILE_NAME + formatter.format(LocalDateTime.now()));
		Path activePath = activeFile.toPath();
		Path archivePath = archiveFile.toPath();
		try {
			Files.copy(activePath, archivePath);
		} catch (IOException e) {
			LOGGER.warning("ArchiveManager: exception caught " + e);
			LOGGER.throwing(CLASS_NAME, "archive", e);
			throw e;
		} finally {
			LOGGER.exiting(CLASS_NAME, "archive");
		}
	}
}
