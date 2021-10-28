package com.brailsoft.property.management.launcher;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoadProperty {
	private FXMLLoader loader;
	private Parent parent;

	public LoadProperty(FXMLLoader loader, Parent parent) {
		this.loader = loader;
		this.parent = parent;
	}

	public FXMLLoader getLoader() {
		return loader;
	}

	public Parent getParent() {
		return parent;
	}
}
