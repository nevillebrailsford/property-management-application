package com.brailsoft.property.management.preference;

import java.util.logging.Level;

public class PreferencesData {
	private String directory = "";
	private Level level = null;
	private boolean emailNotification = false;
	private String emailList = "";

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

	public boolean getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	public String getEmailList() {
		return emailList;
	}

	public void setEmailList(String emailList) {
		this.emailList = emailList;
	}

	@Override
	public String toString() {
		return "directory=" + directory + ", level=" + level
				+ (emailNotification ? ", emailList=" + emailList : "no notification");
	}

}
