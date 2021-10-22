package com.brailsoft.property.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonitoredItemTest {

	private MonitoredItem testItem;
	private LocalDateTime startTest;
	private LocalDateTime lastAction;
	private LocalDateTime nextAction;
	private LocalDateTime nextNotice;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		startTest = LocalDateTime.now();
		testItem = new MonitoredItem("item1", Period.YEARLY, 1, startTest, 1, Period.WEEKLY);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testMonitoredItemStringPeriodIntLocalDateTimeIntPeriod() {
		assertNotNull(testItem);
	}

	@Test
	void testMonitoredItemMonitoredItem() {
		MonitoredItem item = new MonitoredItem(testItem);
		assertNotNull(item);
		assertEquals(testItem, item);
		assertEquals(testItem.getDescription(), item.getDescription());
		assertEquals(testItem.getPeriodForNextAction(), item.getPeriodForNextAction());
	}

	@Test
	void testGetDescription() {
		assertEquals("item1", testItem.getDescription());
	}

	@Test
	void testSetDescription() {
		assertEquals("item1", testItem.getDescription());
		testItem.setDescription("item2");
		assertEquals("item2", testItem.getDescription());
	}

	@Test
	void testGetPeriodForNextAction() {
		assertTrue(Period.YEARLY == testItem.getPeriodForNextAction());
	}

	@Test
	void testSetPeriodForNextAction() {
		assertTrue(Period.YEARLY == testItem.getPeriodForNextAction());
		testItem.setPeriodForNextAction(Period.MONTHLY);
		assertTrue(Period.MONTHLY == testItem.getPeriodForNextAction());
	}

	@Test
	void testGetNoticeEvery() {
		assertEquals(1, testItem.getNoticeEvery());
	}

	@Test
	void testSetNoticeEvery() {
		assertEquals(1, testItem.getNoticeEvery());
		testItem.setNoticeEvery(2);
		assertEquals(2, testItem.getNoticeEvery());
	}

	@Test
	void testGetAdvanceNotice() {
		assertEquals(1, testItem.getAdvanceNotice());
	}

	@Test
	void testSetAdvanceNotice() {
		assertEquals(1, testItem.getAdvanceNotice());
		testItem.setAdvanceNotice(3);
		assertEquals(3, testItem.getAdvanceNotice());
	}

	@Test
	void testGetLastActionPerformed() {
		assertEquals(startTest.format(formatter), testItem.getLastActionPerformed().format(formatter));
	}

	@Test
	void testGetTimeForNextAction() {
		nextAction = startTest.plusYears(1);
		assertEquals(nextAction.format(formatter), testItem.getTimeForNextAction().format(formatter));
	}

	@Test
	void testGetTimeForNextNotice() {
		nextNotice = startTest.plusYears(1).minusWeeks(1);
		assertEquals(nextNotice.format(formatter), testItem.getTimeForNextNotice().format(formatter));
	}

	@Test
	void testGetPeriodForNextNotice() {
		assertTrue(Period.WEEKLY == testItem.getPeriodForNextNotice());
	}

	@Test
	void testSetPeriodForNextNotice() {
		assertTrue(Period.WEEKLY == testItem.getPeriodForNextNotice());
		testItem.setPeriodForNextNotice(Period.MONTHLY);
		assertTrue(Period.MONTHLY == testItem.getPeriodForNextNotice());
	}

	@Test
	void testActionPerformed() {
		testItem.actionPerformed(startTest.plusYears(1));
		lastAction = startTest.plusYears(1);
		nextAction = lastAction.plusYears(1);
		nextNotice = nextAction.minusWeeks(1);
		assertEquals(lastAction.format(formatter), testItem.getLastActionPerformed().format(formatter));
		assertEquals(nextAction.format(formatter), testItem.getTimeForNextAction().format(formatter));
		assertEquals(nextNotice.format(formatter), testItem.getTimeForNextNotice().format(formatter));
	}

	@Test
	void testDueNow() {
		assertFalse(testItem.overdue());
		assertFalse(testItem.overdue(startTest));
		assertFalse(testItem.overdue(startTest.plusYears(1)));
		assertTrue(testItem.overdue(startTest.plusYears(1).plusMinutes(1)));
	}

	@Test
	void testNoticeDue() {
		assertFalse(testItem.noticeDue());
		assertFalse(testItem.noticeDue(startTest));
		assertFalse(testItem.noticeDue(startTest.plusYears(1).minusWeeks(1)));
		assertTrue(testItem.noticeDue(startTest.plusYears(1).minusWeeks(1).plusMinutes(1)));
	}

	@Test
	void testNullDescription() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem(null, Period.MONTHLY, 1, LocalDateTime.now(), 1, Period.WEEKLY);
		});
	}

	@Test
	void testEmptyDescription() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("", Period.MONTHLY, 1, LocalDateTime.now(), 1, Period.WEEKLY);
		});
	}

	@Test
	void testBlankDescription() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem(" ", Period.MONTHLY, 1, LocalDateTime.now(), 1, Period.WEEKLY);
		});
	}

	@Test
	void testInvalidNoticeEvery() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("item", Period.MONTHLY, 0, LocalDateTime.now(), 1, Period.WEEKLY);
		});
	}

	@Test
	void testInvalidAdvanceAction() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("item", Period.MONTHLY, 1, LocalDateTime.now(), 0, Period.WEEKLY);
		});
	}

	@Test
	void testNullLastAction() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("item", Period.MONTHLY, 1, null, 1, Period.WEEKLY);
		});
	}

	@Test
	void testNullItem() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem(null);
		});
	}

	@Test
	void testNullPeriod() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("item", null, 1, LocalDateTime.now(), 1, Period.WEEKLY);
		});
	}

	@Test
	void testNullPeriodFroNextNotice() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MonitoredItem("item", Period.YEARLY, 1, LocalDateTime.now(), 1, null);
		});
	}

}