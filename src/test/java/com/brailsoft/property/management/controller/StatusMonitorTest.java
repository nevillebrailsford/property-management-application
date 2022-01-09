package com.brailsoft.property.management.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;

class StatusMonitorTest {
	private static ApplicationPreferences preferences = ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	private PropertyManagerController controller = new PropertyManagerController();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try {
			Platform.startup(() -> {
			});
		} catch (IllegalStateException e) {
		}
		PropertyManagerLogConfigurer.changeLevel(Level.OFF);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		preferences.clear();
		Platform.exit();
	}

	@BeforeEach
	void setUp() throws Exception {
		StatusMonitor.reset();
	}

	@AfterEach
	void tearDown() throws Exception {
		StatusMonitor.reset();
	}

	@Test
	void testGetInstance() {
		assertNotNull(StatusMonitor.getInstance(controller));
	}

	@Test
	void testGetMultiInstance() {
		assertNotNull(StatusMonitor.getInstance(controller));
		assertNotNull(StatusMonitor.getInstance());
	}

}
