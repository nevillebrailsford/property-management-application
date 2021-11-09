package com.brailsoft.property.management.preference;

import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {
	private static final String DIRECTORY_NAME = "directory.name";
	private static final String LOGGING_LEVEL = "logging.level";
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
