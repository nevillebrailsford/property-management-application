package com.brailsoft.property.management.print;

import java.util.List;

import com.brailsoft.property.management.model.MonitoredItem;
import com.brailsoft.property.management.model.Property;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class PropertyManagementPrinter {

	private static final String PRINT_FAILED = "Print request has failed";
	private static final String PRINT_COMPLETED = "Print request has completed";
	private static final String PRINTER_STATUS = "Printer status";
	private static final String ALERT_TITLE = "Print Job";

	public static void print(PrinterJob job) {

		boolean success = job.printPage(generateReportNode());

		if (success) {
			job.endJob();
			showSuccess();
		} else {
			showCancelled();
		}
	}

	private static void showSuccess() {
		showPrintStatus(PRINT_COMPLETED);
	}

	private static void showCancelled() {
		showPrintStatus(PRINT_FAILED);
	}

	private static void showPrintStatus(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(ALERT_TITLE);
		alert.setHeaderText(PRINTER_STATUS);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private static VBox generateReportNode() {
		VBox vBox = new VBox(10);
		populateWithProperties(vBox);
		return vBox;
	}

	private static void populateWithProperties(VBox vBox) {
		List<Property> properties = PropertyMonitor.getInstance().getProperties();
		for (Property property : properties) {
			vBox.getChildren().add(new PropertyPrintBox(property));
			populateWithItemsForProperty(vBox, property);
			vBox.getChildren().add(new Separator());
		}
	}

	private static void populateWithItemsForProperty(VBox vBox, Property property) {
		List<MonitoredItem> items = property.getItems();
		for (MonitoredItem item : items) {
			vBox.getChildren().add(new ItemPrintBox(item));
		}
	}

}
