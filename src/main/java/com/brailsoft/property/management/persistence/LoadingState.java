package com.brailsoft.property.management.persistence;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;

public class LoadingState {
	private static final String CLASS_NAME = LoadingState.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);
	private static AtomicBoolean loadingData = new AtomicBoolean(false);

	public static void startLoading() {
		LOGGER.entering(CLASS_NAME, "startLoading");
		synchronized (loadingData) {
			while (!loadingData.compareAndSet(false, true)) {
				try {
					loadingData.wait();
				} catch (InterruptedException e) {
				}
			}
			loadingData.notifyAll();
		}
		LOGGER.exiting(CLASS_NAME, "startLoading", loadingData.get());
	}

	public static void stopLoading() {
		LOGGER.entering(CLASS_NAME, "stopLoading");
		synchronized (loadingData) {
			while (!loadingData.compareAndSet(true, false)) {
				try {
					loadingData.wait();
				} catch (InterruptedException e) {
				}
			}
			loadingData.notifyAll();
		}
		LOGGER.exiting(CLASS_NAME, "stopLoading", loadingData.get());
	}

	public static void reset() {
		loadingData.set(false);
	}

	public static boolean isLoading() {
		LOGGER.entering(CLASS_NAME, "isLoading");
		LOGGER.exiting(CLASS_NAME, "isLoading", loadingData.get());
		return loadingData.get();
	}
}
