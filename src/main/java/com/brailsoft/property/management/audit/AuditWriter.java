package com.brailsoft.property.management.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class AuditWriter {
	public static final String AUDIT_FILE = "application.audit";

	public static void write(AuditRecord record) {
		File rootDirectory = new File(ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory());
		File applicationDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		if (!applicationDirectory.exists()) {
			applicationDirectory.mkdirs();
		}
		File auditFile = new File(applicationDirectory, AUDIT_FILE);
		try (PrintStream writer = new PrintStream(new FileOutputStream(auditFile, true))) {
			writer.println(record);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
