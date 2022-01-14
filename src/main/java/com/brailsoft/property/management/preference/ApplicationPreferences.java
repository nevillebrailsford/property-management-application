package com.brailsoft.property.management.preference;

import java.io.File;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;

public class ApplicationPreferences {
	private static final String DIRECTORY_NAME = "directory.name";
	private static final String LOGGING_LEVEL = "logging.level";
	private static final String EMAIL_NOTOFCATIONS = "email.notifications";
	private static final String EMAIL_LIST = "email.list";
	private static ApplicationPreferences instance = null;

	private Preferences applicationPreferences = null;

	public synchronized static ApplicationPreferences getInstance(String nodeName) {
		if (nodeName == null || nodeName.isBlank() || nodeName.isEmpty()) {
			throw new IllegalArgumentException("ApplicationPreferences: nodeName was null");
		}
		if (instance == null) {
			instance = new ApplicationPreferences(nodeName);
		}
		return instance;
	}

	public File getAuditDirectory() {
		File rootDirectory = new File(getDirectory());
		File applicationDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		File auditDirectory = new File(applicationDirectory, Constants.AUDIT_DIRECTORY);
		if (!auditDirectory.exists()) {
			auditDirectory.mkdirs();
		}
		return auditDirectory;
	}

	public File getTraceDirectory() {
		File rootDirectory = new File(System.getProperty("user.home"));
		File logDirectory = new File(rootDirectory, Constants.LOG_DIRECTORY);
		File traceDirectory = new File(logDirectory, Constants.TRACE_DIRECTORY);
		if (!traceDirectory.exists()) {
			traceDirectory.mkdirs();
		}
		return traceDirectory;
	}

	public File getActiveDirectory(String directory) {
		File rootDirectory = new File(directory);
		File activeDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		return activeDirectory;
	}

	public File getArchiveDirectory(String directory) {
		File rootDirectory = new File(directory);
		File activeDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		File archiveDirectory = new File(activeDirectory, Constants.ARCHIVE_DIRECTORY);
		if (!archiveDirectory.exists()) {
			archiveDirectory.mkdirs();
		}
		return archiveDirectory;
	}

	private ApplicationPreferences(String nodeName) {
		applicationPreferences = Preferences.userRoot().node(nodeName);
	}

	public void clear() throws BackingStoreException {
		applicationPreferences.clear();
		applicationPreferences.flush();
	}

	public String getDirectory() {
		String directoryName = applicationPreferences.get(DIRECTORY_NAME, "");
		return directoryName;
	}

	public void setDirectory(String directoryName) throws BackingStoreException {
		applicationPreferences.put(DIRECTORY_NAME, directoryName);
		applicationPreferences.flush();
	}

	public Level getLevel() {
		String lvl = applicationPreferences.get(LOGGING_LEVEL, "");
		return convertStringToLevel(lvl);
	}

	public void setLevel(Level level) throws BackingStoreException {
		String lvl = convertLevelToString(level);
		applicationPreferences.put(LOGGING_LEVEL, lvl);
		applicationPreferences.flush();
	}

	public boolean getEmailNotification() {
		String notification = applicationPreferences.get(EMAIL_NOTOFCATIONS, "false");
		return Boolean.valueOf(notification).booleanValue();
	}

	public void setEmailNotification(boolean emailNotification) throws BackingStoreException {
		applicationPreferences.put(EMAIL_NOTOFCATIONS, Boolean.toString(emailNotification));
		applicationPreferences.flush();
	}

	public String getEMailList() {
		return applicationPreferences.get(EMAIL_LIST, "");
	}

	public void setEMailList(String list) throws BackingStoreException {
		applicationPreferences.put(EMAIL_LIST, list);
		applicationPreferences.flush();
	}

	private Level convertStringToLevel(String lvl) {
		if (lvl.isBlank()) {
			return Level.WARNING;
		}
		return Level.parse(lvl);
	}

	private String convertLevelToString(Level level) {
		if (level == null) {
			throw new IllegalArgumentException("ApplicationPreferences: level was null");
		}
		return level.toString();
	}
}
