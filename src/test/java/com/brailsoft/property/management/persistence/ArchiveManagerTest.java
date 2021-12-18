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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constants.TestConstants;

class ArchiveManagerTest {
	private final String FILE_NAME = "property.dat";
	private final File rootDir = new File(TestConstants.TEST_DIRECTORY);
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss");

	@AfterEach
	@BeforeEach
	public void cleanUpFiles() {
		if (rootDir.exists()) {
			File targetDir = new File(rootDir, LocalStorage.DIRECTORY);
			File[] files = targetDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
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
		File file = new File(targetDir, FILE_NAME);
		Path newFile = Files.createFile(file.toPath());
		File f = newFile.toFile();
		assertTrue(f.exists());
		assertEquals(1, targetDir.listFiles().length);
		ArchiveManager.getInstance().archive(TestConstants.TEST_DIRECTORY);
		String finish = formatter.format(LocalDateTime.now());
		assertEquals(2, targetDir.listFiles().length);
		File[] files = targetDir.listFiles();
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
