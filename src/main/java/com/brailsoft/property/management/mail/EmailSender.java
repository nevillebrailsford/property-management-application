package com.brailsoft.property.management.mail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class EmailSender {
	private static final String CLASS_NAME = PropertyMonitor.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private static EmailSender instance = null;
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance();

	private Properties prop = new Properties();
	private Session session;

	private String username = null;
	private String password = null;

	public synchronized static EmailSender getInstance() throws Exception {
		if (instance == null) {
			instance = new EmailSender();
			instance.loadProperties();
		}
		return instance;
	}

	private EmailSender() {
		prop.put("mail.smtp.host", "smtp-mail.outlook.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	public void sendMessage(String text) throws Exception, Exception {
		LOGGER.entering(CLASS_NAME, "sendMessage", text);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("brailsoft_property@outlook.com"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("neville_brailsford@hotmail.com"));
		message.setSubject("Property Managment Report");
		message.setText(text);
		Transport.send(message);
		LOGGER.exiting(CLASS_NAME, "sendMessage");
	}

	private void loadProperties() throws Exception {
		LOGGER.entering(CLASS_NAME, "loadProperties");
		File rootDirectory = preferences.getActiveDirectory(preferences.getDirectory());
		File mailFile = new File(rootDirectory, "mail.properties");
		Properties prop = new Properties();
		prop.load(new FileReader(mailFile));
		username = prop.getProperty("username");
		password = prop.getProperty("password");
		if (username == null || password == null) {
			IOException e = new IOException("mail.properties file incorrectly specified");
			LOGGER.throwing(CLASS_NAME, "loadProperties", e);
			LOGGER.exiting(CLASS_NAME, "loadProperties");
			throw e;
		}
		LOGGER.exiting(CLASS_NAME, "loadProperties");
	}
}
