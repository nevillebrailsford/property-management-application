package com.brailsoft.property.management.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Base64;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.constant.TestConstants;
import com.brailsoft.property.management.preference.ApplicationPreferences;

class EmailSenderTest {

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
	}

	@Test
	void testEncodeDecode() {
		byte[] encodedBytes = Base64.getEncoder().encode("Test@".getBytes());
		byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
		assertEquals("Test@", new String(decodedBytes));
	}

}
