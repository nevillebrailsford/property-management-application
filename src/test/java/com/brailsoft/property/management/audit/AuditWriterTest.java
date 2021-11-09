package com.brailsoft.property.management.audit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.constants.TestConstants;
import com.brailsoft.property.management.persistence.LocalStorage;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class AuditWriterTest {

	AuditRecord record;
	ApplicationPreferences applicationPreferences;
	File applicationDirectory;
	File rootDirectory;
	File auditFile;

	@BeforeEach
	void setUp() throws Exception {
		applicationPreferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		applicationPreferences.setDirectory(TestConstants.TEST_DIRECTORY);
		rootDirectory = new File(ApplicationPreferences.getInstance(Constants.NODE_NAME).getDirectory());
		applicationDirectory = new File(rootDirectory, LocalStorage.DIRECTORY);
		auditFile = new File(applicationDirectory, AuditWriter.AUDIT_FILE);
		deleteFiles();
	}

	@AfterEach
	void tearDown() throws Exception {
		deleteFiles();
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

	private void deleteFiles() throws IOException {
		File[] files = applicationDirectory.listFiles();
		if (files != null) {
			for (File f : files) {
				Files.deleteIfExists(Paths.get(f.getAbsolutePath()));
			}
		}
		Files.deleteIfExists(Paths.get(applicationDirectory.getAbsolutePath()));
		Files.deleteIfExists(Paths.get(rootDirectory.getAbsolutePath()));
	}

}
