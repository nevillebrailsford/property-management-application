package com.brailsoft.property.management.timer;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class Timer {
	private static Timer instance = null;
	private Timeline timeLine;
	private List<TimerListener> listeners = new ArrayList<>();

	public synchronized static Timer getInstance() {
		if (instance == null) {
			instance = new Timer();
		}
		return instance;
	}

	private Timer() {
	}

	public void start() {
		timeLine = new Timeline(new KeyFrame(Duration.minutes(1), (event) -> {
			tellListeners(event);
		}));
		timeLine.setCycleCount(Timeline.INDEFINITE);
		timeLine.setDelay(Duration.minutes(1));
		timeLine.play();
	}

	public void stop() {
		timeLine.stop();
	}

	public void addListener(TimerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(TimerListener listener) {
		listeners.remove(listener);
	}

	private void tellListeners(ActionEvent event) {
		listeners.forEach(listener -> {
			listener.timerPopped(event);
		});
	}
}
