package com.brailsoft.property.management.logging;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class PropertyManagerFormatter extends Formatter {

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uu/MM/dd HH:mm:ss");
	private final static String ENTRY = ">";
	private final static String EXIT = "<";
	private final static String EXCEPTION = "x";
	private final static String SEVERE = "s";
	private final static String WARNING = "w";
	private final static String INFO = "i";
	private final static String CONFIG = "c";
	private final static String FINE = "f";
	private final static String FINER = "r";
	private final static String FINEST = "t";

	@Override
	public String format(LogRecord record) {
		String message = record.getMessage();
		String messageStart = message.startsWith("ENTRY") ? "ENTRY"
				: message.startsWith("RETURN") ? "RETURN" : message.startsWith("THROW") ? "THROW" : message;
		String formattedRecord = switch (messageStart) {
			case "ENTRY" -> processEntry(record);
			case "RETURN" -> processExit(record);
			case "THROW" -> processException(record);
			default -> process(record);
		};
		return formattedRecord;
	}

	private String process(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(record));
		builder.append(convertLevel(record.getLevel())).append(" ");
		builder.append(buildClassAndMethod(record));
		builder.append(record.getMessage()).append(" ").append("\n");
		return builder.toString();
	}

	private String processEntry(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(record));
		builder.append(ENTRY).append(" ");
		builder.append(buildClassAndMethod(record));
		builder.append(processParameters(record)).append("\n");
		return builder.toString();
	}

	private String processExit(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(record));
		builder.append(EXIT).append(" ");
		builder.append(buildClassAndMethod(record));
		builder.append(processParameters(record)).append("\n");
		return builder.toString();
	}

	private String processException(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(buildHeader(record));
		builder.append(EXCEPTION).append(" ");
		builder.append(processThrowable(record)).append("\n");
		return builder.toString();
	}

	private String formatDateAndTime(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()),
				TimeZone.getDefault().toZoneId());
		builder.append("[").append(dateTime.format(formatter)).append("] ");
		return builder.toString();
	}

	private String convertLevel(Level level) {
		String levelString = level.toString();
		String result = switch (levelString) {
			case "SEVERE" -> SEVERE;
			case "WARNING" -> WARNING;
			case "INFO" -> INFO;
			case "CONFIG" -> CONFIG;
			case "FINE" -> FINE;
			case "FINER" -> FINER;
			case "FINEST" -> FINEST;
			default -> throw new IllegalArgumentException("Unexpected value: " + levelString);
		};
		return result;
	}

	private String makeClassNameSimple(String classname) {
		return classname.lastIndexOf(".") > 0 ? classname.substring(classname.lastIndexOf(".") + 1) : classname;
	}

	private String processParameters(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		String newLine = "";
		if (record.getParameters() != null) {
			for (Object obj : record.getParameters()) {
				if (obj == null) {
					builder.append(newLine).append("null");
				} else {
					builder.append(newLine).append(obj.toString());
				}
				newLine = "\n";
			}
		}
		return builder.toString();
	}

	private String processThrowable(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		Throwable t = record.getThrown();
		builder.append(t.getClass());
		if (t.getMessage() != null) {
			builder.append(":").append(t.getMessage());
		}
		builder.append("\n");
		builder.append(processStackTrace(t));
		return builder.toString();
	}

	private String processStackTrace(Throwable t) {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement e : t.getStackTrace()) {
			builder.append("    at ");
			builder.append(e);
			builder.append("\n");
		}
		return builder.toString();
	}

	private String buildHeader(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(formatDateAndTime(record));
		builder.append(record.getLongThreadID()).append(" ");
		return builder.toString();
	}

	private String buildClassAndMethod(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(makeClassNameSimple(record.getSourceClassName())).append(" ");
		builder.append(record.getSourceMethodName()).append(" ");
		return builder.toString();
	}
}
