package com.brailsoft.property.management.audit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class AuditWriterTest {

	AuditRecord record;
	ApplicationPreferences applicationPreferences;
	@TempDir
	File rootDirectory;
	File applicationDirectory;
	File auditFile;

	@BeforeEach
	void setUp() throws Exception {
		applicationPreferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		applicationPreferences.setDirectory(rootDirectory.getAbsolutePath());
		rootDirectory = new File(ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory());
		applicationDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		auditFile = new File(applicationDirectory, AuditWriter.AUDIT_FILE);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testWrite() {
		assertFalse(auditFile.exists());
		record = new AuditRecord(AuditType.ADDED, AuditObject.PROPERTY);
		record.setDescription("test of audit write");
		AuditWriter.write(record);
		assertTrue(fileExistsAndIsValid(auditFile, 1));
	}

	private boolean fileExistsAndIsValid(File file, int expectedCount) {
		int noOfRecords = 0;
		if (file.exists()) {
			try (BufferedReader inputFile = new BufferedReader(new FileReader(file))) {
				do {
					inputFile.readLine();
					noOfRecords++;
				} while (inputFile.ready());
			} catch (Exception e) {
				return false;
			}
		}
		return (noOfRecords == expectedCount);
	}

}
