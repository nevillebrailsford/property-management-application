package com.brailsoft.property.management.mail;

import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.controller.StatusMonitor;
import com.brailsoft.property.management.model.PropertyMonitor;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class EmailSender implements Runnable {
	private static final String CLASS_NAME = PropertyMonitor.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private String text;

	public EmailSender(String text) {
		this.text = text;
	}

	@Override
	public void run() {
		LOGGER.entering(CLASS_NAME, "run");
		EmailConfigurer configurer = EmailConfigurer.getInstance();
		if (configurer.isValidConfiguration()) {
			Session session = configurer.getSession();
			Message message = new MimeMessage(session);
			try {
				String recipients = ApplicationPreferences.getInstance().getEMailList();
				message.setFrom(new InternetAddress(configurer.userName()));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				message.setSubject("Property Managment Report");
				message.setText(text);
				Transport.send(message);
				StatusMonitor.getInstance().update("Notification email sent successfully");
			} catch (Exception e) {
				LOGGER.warning("Caught xeception: " + e.getMessage());
				StatusMonitor.getInstance().update("Attempt to send notification email failed");
			}
		} else {
			LOGGER.warning("EmailConfiguration not valid");
		}
		LOGGER.exiting(CLASS_NAME, "run");
	}

}
