package com.brailsoft.property.management.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.Constants;
import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class ArchiveManagerTest {
	private final String FILE_NAME = "property.dat";
	private final File rootDir = new File(TestConstants.TEST_DIRECTORY);
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss");

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	}

	@AfterEach
	@BeforeEach
	public void cleanUpFiles() {
		if (rootDir.exists()) {
			File targetDir = new File(rootDir, LocalStorage.DIRECTORY);
			File modelDir = new File(targetDir, Constants.MODEL_DIRECTORY);
			File archiveDir = new File(targetDir, Constants.ARCHIVE_DIRECTORY);
			File[] files = archiveDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			archiveDir.delete();
			files = modelDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			modelDir.delete();
			targetDir.delete();
			rootDir.delete();
		}
	}

	@Test
	void testGetInstance() {
		assertNotNull(ArchiveManager.getInstance());
	}

	@Test
	void testArchive() throws Exception {
		String start = formatter.format(LocalDateTime.now());
		rootDir.mkdir();
		File targetDir = new File(rootDir, LocalStorage.DIRECTORY);
		targetDir.mkdir();
		File modelDir = new File(targetDir, Constants.MODEL_DIRECTORY);
		modelDir.mkdir();
		File archiveDir = new File(targetDir, Constants.ARCHIVE_DIRECTORY);
		archiveDir.mkdir();
		File file = new File(modelDir, FILE_NAME);
		Path newFile = Files.createFile(file.toPath());
		File f = newFile.toFile();
		assertTrue(f.exists());
		assertEquals(1, modelDir.listFiles().length);
		ArchiveManager.getInstance().archive(TestConstants.TEST_DIRECTORY);
		String finish = formatter.format(LocalDateTime.now());
		assertEquals(1, modelDir.listFiles().length);
		assertEquals(1, archiveDir.listFiles().length);
		File[] files = archiveDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			assertTrue(files[i].getName().startsWith(FILE_NAME));
			if (files[i].getName().length() > FILE_NAME.length()) {
				String timedate = files[i].getName().substring(FILE_NAME.length() + 1);
				assertTrue(start.compareTo(timedate) <= 0);
				assertTrue(finish.compareTo(timedate) >= 0);
			}
		}
	}

	@Test
	void testArchiveNull() {
		Exception exc = assertThrows(InvalidParameterException.class, () -> {
			ArchiveManager.getInstance().archive(null);
		});
		assertEquals("ArchiveManager: directory was null", exc.getMessage());
	}

}
