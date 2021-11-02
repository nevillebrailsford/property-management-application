package com.brailsoft.property.management.timer;

import javafx.event.ActionEvent;

@FunctionalInterface
public interface TimerListener {
	void timerPopped(ActionEvent event);
}
