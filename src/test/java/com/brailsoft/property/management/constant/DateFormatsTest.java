package com.brailsoft.property.management.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.util.StringConverter;

class DateFormatsTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void ensureDateFormatForCalendarViewRemainsUnchanged() {
		assertEquals("EEE dd LLL uuuu", DateFormats.dateFormatForCalendarView);
	}

	@Test
	void ensureDateFormatForLogRecordRemainsUnchanged() {
		assertEquals("uu/MM/dd HH:mm:ss:SSS zzz", DateFormats.dateFormatForLogRecord);
	}

	@Test
	void ensureDateFormatForUIRemainsUnchanged() {
		assertEquals("dd/MM/uuuu", DateFormats.dateFormatForUI);
	}

	@Test
	void ensureDateFormatForAuditRecordRemainsUnchanged() {
		assertEquals("uuuu/MM/dd HH:mm:ss zzz", DateFormats.dateFormatForAuditRecord);
	}

	@Test
	void testCreateDateConverter() {
		assertNotNull(DateFormats.createDateConverter());
	}

	@Test
	void testDateConverter() {
		StringConverter<LocalDate> converter = DateFormats.createDateConverter();
		LocalDate today = LocalDate.now();
		String convertedDate = converter.toString(today);
		LocalDate convertedToday = converter.fromString(convertedDate);
		assertEquals(today, convertedToday);
	}

	@Test
	void testDateConverterIsSingleton() {
		StringConverter<LocalDate> converter1 = DateFormats.createDateConverter();
		StringConverter<LocalDate> converter2 = DateFormats.createDateConverter();
		assertTrue(converter1 == converter2);
	}

	@Test
	void testInvalidString() {
		StringConverter<LocalDate> converter = DateFormats.createDateConverter();
		Exception exc = assertThrows(DateTimeParseException.class, () -> {
			converter.fromString("202a/11/11");
		});
		assertEquals("Text '202a/11/11' could not be parsed at index 2", exc.getMessage());
	}
}
