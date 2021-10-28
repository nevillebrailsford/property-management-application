module com.brailsoft.property.manager {
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	requires java.xml;

	opens com.brailsoft.property.management.launcher;
	opens com.brailsoft.property.management.controller;
	opens com.brailsoft.property.management.model;
	opens com.brailsoft.property.management.persistence;
	opens com.brailsoft.property.management.print;

	exports com.brailsoft.property.management.launcher;
}