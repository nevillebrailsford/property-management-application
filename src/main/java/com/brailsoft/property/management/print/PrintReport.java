package com.brailsoft.property.management.print;

import javafx.print.PrinterJob;
import javafx.stage.Stage;

public class PrintReport {
	public static void printReport(Stage owner) {

		PrinterJob job = PrinterJob.createPrinterJob();

		if (job == null) {
			throw new IllegalStateException("Could not create a print job");
		}

		boolean proceed = job.showPrintDialog(owner);

		if (proceed) {
			PropertyManagementPrinter.print(job);
		}
	}

	public static void printInventory(Stage owner) {

		PrinterJob job = PrinterJob.createPrinterJob();

		if (job == null) {
			throw new IllegalStateException("Could not create a print job");
		}

		boolean proceed = job.showPrintDialog(owner);

		if (proceed) {
			PropertyManagementPrinter.printInventory(job);
		}
	}
}
