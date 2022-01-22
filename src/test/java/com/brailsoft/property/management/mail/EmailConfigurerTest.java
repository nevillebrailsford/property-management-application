package com.brailsoft.property.management.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class EmailConfigurerTest {

	private static ApplicationPreferences preferences;
	private File activeDirectory;
	private File mailFile;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		preferences.setDirectory(TestConstants.TEST_DIRECTORY);
		PropertyManagerLogConfigurer.changeLevel(Level.OFF);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		preferences = ApplicationPreferences.getInstance();
		activeDirectory = preferences.getActiveDirectory(preferences.getDirectory());
		mailFile = new File(activeDirectory, "mail.properties");
		Files.deleteIfExists(mailFile.toPath());
		Files.deleteIfExists(activeDirectory.toPath());
		Files.deleteIfExists(activeDirectory.getParentFile().toPath());
	}

	@AfterEach
	void tearDown() throws Exception {
		activeDirectory = preferences.getActiveDirectory(preferences.getDirectory());
		mailFile = new File(activeDirectory, "mail.properties");
		Files.deleteIfExists(mailFile.toPath());
		Files.deleteIfExists(activeDirectory.toPath());
		Files.deleteIfExists(activeDirectory.getParentFile().toPath());
	}

	@Test
	void testGetInstance() {
		assertNotNull(EmailConfigurer.getInstance());
	}

	@Test
	void testGetInstanceNoFile() {
		assertFalse(EmailConfigurer.getInstance().isValidConfiguration());
	}

	@Test
	void testGetInstanceEmptyFile() throws Exception {
		createEmptyMailFile();
		assertTrue(mailFile.exists());
		assertFalse(EmailConfigurer.getInstance().isValidConfiguration());
	}

	@Test
	void testGetInstanceValidFile() throws Exception {
		createValidFile();
		assertTrue(EmailConfigurer.getInstance().isValidConfiguration());
	}

	@Test
	void testGetSession() throws Exception {
		createValidFile();
		assertNotNull(EmailConfigurer.getInstance().getSession());
	}

	@Test
	void testInavlidFileContents() throws Exception {
		createInvalidFile();
		assertFalse(EmailConfigurer.getInstance().isValidConfiguration());
	}

	@Test
	void testGetUserName() throws Exception {
		createValidFile();
		assertNotNull(EmailConfigurer.getInstance().userName());
		assertEquals("testname@neville", EmailConfigurer.getInstance().userName());
	}

	private void createEmptyMailFile() throws Exception {
		if (mailFile.exists()) {
			return;
		}
		if (!activeDirectory.exists()) {
			activeDirectory.mkdirs();
		}
		mailFile.createNewFile();
	}

	private void createValidFile() throws Exception {
		createEmptyMailFile();
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mailFile)));
		writer.println("username=testname@neville");
		writer.println("password=secret");
		writer.flush();
		writer.close();
	}

	private void createInvalidFile() throws Exception {
		createEmptyMailFile();
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mailFile)));
		writer.println("username=testname@neville");
		writer.flush();
		writer.close();
	}
}
