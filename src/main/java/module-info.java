module com.brailsoft.property.manager {
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	requires java.xml;
	requires java.prefs;
	requires java.logging;

	opens com.brailsoft.property.management.audit;
	opens com.brailsoft.property.management.edit;
	opens com.brailsoft.property.management.launcher;
	opens com.brailsoft.property.management.constant;
	opens com.brailsoft.property.management.controller;
	opens com.brailsoft.property.management.model;
	opens com.brailsoft.property.management.persistence;
	opens com.brailsoft.property.management.print;
	opens com.brailsoft.property.management.preference;

	exports com.brailsoft.property.management.launcher;
}