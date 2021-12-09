package com.brailsoft.property.management.persistence;

import javafx.event.ActionEvent;

@FunctionalInterface
public interface StorageListener {
	public void actionComplete(ActionEvent event);
}
