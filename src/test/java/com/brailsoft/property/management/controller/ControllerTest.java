package com.brailsoft.property.management.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.launcher.PropertyManager;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

class ControllerTest {

	@BeforeEach
	void setUp() throws Exception {
		Platform.startup(() -> {
		});
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
