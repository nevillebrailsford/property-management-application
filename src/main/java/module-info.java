module com.brailsoft.property.manager {
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;

	opens com.brailsoft.property.management.launcher;
	opens com.brailsoft.property.management.controller;
	opens com.brailsoft.property.management.model;

	exports com.brailsoft.property.management.launcher;
}