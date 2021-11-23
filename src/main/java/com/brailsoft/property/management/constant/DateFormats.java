package com.brailsoft.property.management.constant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class DateFormats {
	public static final String dateFormatForUI = "dd/MM/uuuu";
	public static final String dateFormatForAuditRecord = "uuuu/MM/dd HH:mm:ss zzz";
	public static final String dateFormatForLogRecord = "uu/MM/dd HH:mm:ss:SSS zzz";
	public static final String dateFormatForCalendarView = "EEE dd LLL uuuu";

	private static StringConverter<LocalDate> dateConverter = null;

	// comment just to get code up to date

	public synchronized static StringConverter<LocalDate> createDateConverter() {
		if (dateConverter == null) {
			dateConverter = new StringConverter<LocalDate>() {
				private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatForUI);

				@Override
				public String toString(LocalDate localDate) {
					if (localDate == null) {
						return "";
					}
					return formatter.format(localDate);
				}

				@Override
				public LocalDate fromString(String dateString) {
					if (dateString == null || dateString.trim().isEmpty()) {
						return null;
					}
					return LocalDate.parse(dateString, formatter);
				}
			};
		}
		return dateConverter;
	}

}
