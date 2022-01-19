package com.brailsoft.property.management.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class LoadingStateTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ApplicationPreferences.getInstance(TestConstants.NODE_NAME);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterEach
	void tearDown() throws Exception {
		LoadingState.reset();
	}

	@Test
	void testStartLoading() {
		LoadingState.startLoading();
		assertTrue(LoadingState.isLoading());
	}

	@Test
	void testStopLoading() {
		LoadingState.startLoading();
		assertTrue(LoadingState.isLoading());
		LoadingState.stopLoading();
		assertFalse(LoadingState.isLoading());
	}

	@Test
	void testIsLoading() {
		assertFalse(LoadingState.isLoading());
		LoadingState.startLoading();
		assertTrue(LoadingState.isLoading());
		LoadingState.stopLoading();
		assertFalse(LoadingState.isLoading());
	}

	@Test
	void testConflict() throws InterruptedException {
		assertFalse(LoadingState.isLoading());
		new Thread(() -> {
			LoadingState.startLoading();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			assertTrue(LoadingState.isLoading());
			LoadingState.stopLoading();
		}).start();
		new Thread(() -> {
			LoadingState.startLoading();
			assertTrue(LoadingState.isLoading());
			LoadingState.stopLoading();
			assertFalse(LoadingState.isLoading());
		}).start();
		Thread.sleep(200);
		assertFalse(LoadingState.isLoading());
	}

}
