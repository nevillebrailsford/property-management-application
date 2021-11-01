package com.brailsoft.property.management.preference;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ApplicationPreferences {
	private static final String DIRECTORY_NAME = "directory.name";
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
}
