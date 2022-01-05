package com.brailsoft.property.management.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class AuditWriter {
	public static final String AUDIT_DIRECTORY = "audit";
	public static final String AUDIT_FILE = "application.audit";

	public static void write(AuditRecord record) {
		File rootDirectory = new File(ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory());
		File applicationDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		File auditDirectory = new File(applicationDirectory, AUDIT_DIRECTORY);
		if (!auditDirectory.exists()) {
			auditDirectory.mkdirs();
		}
		File auditFile = new File(auditDirectory, AUDIT_FILE);
		try (PrintStream writer = new PrintStream(new FileOutputStream(auditFile, true))) {
			writer.println(record);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
