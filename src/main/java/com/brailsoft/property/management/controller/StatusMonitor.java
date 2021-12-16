package com.brailsoft.property.management.controller;

import com.brailsoft.property.management.launcher.PropertyManager;

import javafx.application.Platform;

public class StatusMonitor {
	private static StatusMonitor instance = null;
	private PropertyManagerController controller;

	public synchronized static StatusMonitor getInstance(PropertyManagerController... controllers) {
		if (instance == null) {
			if (PropertyManager.started()) {
				if (controllers == null || controllers.length != 1) {
					throw new IllegalArgumentException("StatusMonitor: controller must be specified");
				}
				instance = new StatusMonitor();
				instance.controller = controllers[0];
			} else {
				instance = new StatusMonitor();
				instance.controller = null;
			}
		}
		return instance;
	}

	public void update(String status) {
		if (controller != null) {
			Platform.runLater(() -> {
				controller.updateStatus(status);
			});
		}
	}

	public static void reset() {
		instance = null;
	}
}
