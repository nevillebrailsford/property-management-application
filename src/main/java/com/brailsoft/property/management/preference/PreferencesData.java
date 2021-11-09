package com.brailsoft.property.management.preference;

import java.util.logging.Level;

public class PreferencesData {
	private String directory = "";
	private Level level = null;

	public PreferencesData() {
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "directory=" + directory + ", level=" + level;
	}

}
