package com.brailsoft.property.management.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

public class AuditWriter {

	public static void write(AuditRecord record) {
		File auditDirectory = ApplicationPreferences.getInstance().getAuditDirectory();
		File auditFile = new File(auditDirectory, Constants.AUDIT_FILE);
		try (PrintStream writer = new PrintStream(new FileOutputStream(auditFile, true))) {
			writer.println(record);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
