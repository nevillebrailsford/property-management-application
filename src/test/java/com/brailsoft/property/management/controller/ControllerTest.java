package com.brailsoft.property.management.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.launcher.PropertyManager;
import com.brailsoft.property.management.logging.PropertyManagerLogConfigurer;
import com.brailsoft.property.management.preference.ApplicationPreferences;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

class ControllerTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	}

	@BeforeEach
	void setUp() throws Exception {
		try {
			Platform.startup(() -> {
			});
		} catch (IllegalStateException e) {
		}
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
		PropertyManagerLogConfigurer.changeLevel(Level.OFF);
	}

	@AfterEach
	void tearDown() throws Exception {
		Platform.exit();
	}

	@Test
	void test() throws IOException {
		FXMLLoader loader = new FXMLLoader(PropertyManager.class.getResource("PropertyManager.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotNull(loader.getController());
	}

}
