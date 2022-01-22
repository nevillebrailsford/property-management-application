package com.brailsoft.property.management.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class EmailConfigurer {
	private static final String CLASS_NAME = EmailConfigurer.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private static EmailConfigurer instance = null;
	private ApplicationPreferences preferences = ApplicationPreferences.getInstance();

	private String username = null;
	private String password = null;
	private boolean validConfiguration = true;
	private Session session = null;

	public synchronized static EmailConfigurer getInstance() {
		if (instance == null) {
			instance = new EmailConfigurer();
		}
		instance.loadProperties();
		return instance;
	}

	private EmailConfigurer() {
	}

	public Session getSession() {
		return session;
	}

	public boolean isValidConfiguration() {
		return validConfiguration;
	}

	private void loadProperties() {
		LOGGER.entering(CLASS_NAME, "loadProperties");
		Properties prop = new Properties();
		File rootDirectory = preferences.getActiveDirectory(preferences.getDirectory());
		File mailFile = new File(rootDirectory, "mail.properties");
		try (FileReader reader = new FileReader(mailFile)) {
			prop.load(reader);
		} catch (FileNotFoundException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.warning("Recording invalid configuration");
			validConfiguration = false;
			LOGGER.exiting(CLASS_NAME, "loadProperties");
			return;
		} catch (IOException e) {
			LOGGER.warning("Caught exception: " + e.getMessage());
			LOGGER.warning("Recording invalid configuration");
			validConfiguration = false;
			LOGGER.exiting(CLASS_NAME, "loadProperties");
			return;
		}
		username = prop.getProperty("username", null);
		password = prop.getProperty("password", null);
		if (username == null || password == null) {
			LOGGER.warning("Recording invalid configuration");
			validConfiguration = false;
			LOGGER.exiting(CLASS_NAME, "loadProperties");
			return;
		}
		session = createSession();
		LOGGER.exiting(CLASS_NAME, "loadProperties");
	}

	public String userName() {
		return username;
	}

	private Session createSession() {
		LOGGER.entering(CLASS_NAME, "createSession");
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp-mail.outlook.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		LOGGER.exiting(CLASS_NAME, "createSession");
		return session;
	}

}
