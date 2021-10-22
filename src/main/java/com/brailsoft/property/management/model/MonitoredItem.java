package com.brailsoft.property.management.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MonitoredItem implements Comparable<MonitoredItem> {
	private String description;
	private LocalDateTime lastActionPerformed;
	private LocalDateTime timeForNextAction;
	private LocalDateTime timeForNextNotice;
	private Period periodForNextAction;
	private int noticeEvery;
	private Period periodForNextNotice;
	private int advanceNotice;

	public MonitoredItem(String description, Period periodForNextAction, int noticeEvery, LocalDateTime lastActioned,
			int advanceNotice, Period periodForNextNotice) {
		if (description == null || description.isBlank() || description.isEmpty()) {
			throw new IllegalArgumentException("MonitoredItem: description not specified");
		}
		if (periodForNextAction == null) {
			throw new IllegalArgumentException("MonitoredItem: period was null");
		}
		if (noticeEvery < 1) {
			throw new IllegalArgumentException("MonitoredItem: noticeEvery less than 1");
		}
		if (advanceNotice < 1) {
			throw new IllegalArgumentException("MonitoredItem: advanceNotice less than 1");
		}
		if (lastActioned == null) {
			throw new IllegalArgumentException("MonitoredItem: lastActioned was null");
		}
		if (periodForNextNotice == null) {
			throw new IllegalArgumentException("MonitoredItem: periodForNextNotice was null");
		}
		this.description = description;
		this.periodForNextAction = periodForNextAction;
		this.noticeEvery = noticeEvery;
		this.advanceNotice = advanceNotice;
		this.periodForNextNotice = periodForNextNotice;
		this.lastActionPerformed = lastActioned;
		this.timeForNextAction = calculateTimeForNextAction(periodForNextAction, noticeEvery, lastActioned);
		this.timeForNextNotice = calculateTimeForNextNotice(this.timeForNextAction, advanceNotice, periodForNextNotice);
	}

	public MonitoredItem(MonitoredItem that) {
		if (that == null) {
			throw new IllegalArgumentException("MonitoredItem: item was null");
		}
		this.description = that.description;
		this.periodForNextAction = that.periodForNextAction;
		this.noticeEvery = that.noticeEvery;
		this.advanceNotice = that.advanceNotice;
		this.periodForNextNotice = that.periodForNextNotice;
		this.lastActionPerformed = that.lastActionPerformed;
		this.timeForNextAction = that.timeForNextAction;
		this.timeForNextNotice = that.timeForNextNotice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Period getPeriodForNextAction() {
		return periodForNextAction;
	}

	public void setPeriodForNextAction(Period periodForNextAction) {
		this.periodForNextAction = periodForNextAction;
		recalculateTimes();
	}

	public int getNoticeEvery() {
		return noticeEvery;
	}

	public void setNoticeEvery(int noticeEvery) {
		this.noticeEvery = noticeEvery;
		recalculateTimes();
	}

	public int getAdvanceNotice() {
		return advanceNotice;
	}

	public void setAdvanceNotice(int advanceNotice) {
		this.advanceNotice = advanceNotice;
		recalculateNextNotice();
	}

	public LocalDateTime getLastActionPerformed() {
		return lastActionPerformed;
	}

	public LocalDateTime getTimeForNextAction() {
		return timeForNextAction;
	}

	public LocalDateTime getTimeForNextNotice() {
		return timeForNextNotice;
	}

	public Period getPeriodForNextNotice() {
		return periodForNextNotice;
	}

	public void setPeriodForNextNotice(Period periodForNextNotice) {
		this.periodForNextNotice = periodForNextNotice;
		recalculateNextNotice();
	}

	public void actionPerformed(LocalDateTime when) {
		this.lastActionPerformed = when;
		recalculateTimes();
	}

	public boolean overdue() {
		return overdue(LocalDateTime.now());
	}

	public boolean overdue(LocalDateTime today) {
		if (today.isAfter(timeForNextAction)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean noticeDue() {
		return noticeDue(LocalDateTime.now());
	}

	public boolean noticeDue(LocalDateTime today) {
		if (today.isAfter(timeForNextNotice)) {
			return true;
		} else {
			return false;
		}
	}

	private LocalDateTime calculateTimeForNextNotice(LocalDateTime timeForNextAction, int advanceNotice,
			Period periodForNextNotice) {
		var result = switch (periodForNextNotice) {
			case WEEKLY -> reduceTimeStampByWeeks(advanceNotice, timeForNextAction);
			case MONTHLY -> reduceTimeStampByMonths(advanceNotice, timeForNextAction);
			case YEARLY -> reduceTimeStampByYears(advanceNotice, timeForNextAction);
		};
		return result;
	}

	private LocalDateTime calculateTimeForNextAction(Period period, int noticeEvery, LocalDateTime lastActioned) {
		var result = switch (period) {
			case WEEKLY -> increaesTimeStampByWeeks(noticeEvery, lastActioned);
			case MONTHLY -> increaseTimeStampByMonths(noticeEvery, lastActioned);
			case YEARLY -> increaseTimeStampByYears(noticeEvery, lastActioned);
		};
		return result;
	}

	private void recalculateTimes() {
		recalculateNextAction();
		recalculateNextNotice();
	}

	private void recalculateNextAction() {
		timeForNextAction = calculateTimeForNextAction(periodForNextAction, noticeEvery, lastActionPerformed);
	}

	private void recalculateNextNotice() {
		timeForNextNotice = calculateTimeForNextNotice(timeForNextAction, advanceNotice, periodForNextNotice);
	}

	private LocalDateTime increaesTimeStampByWeeks(int noticeEvery, LocalDateTime lastActioned) {
		return lastActioned.plusWeeks(noticeEvery);
	}

	private LocalDateTime increaseTimeStampByMonths(int noticeEvery, LocalDateTime lastActioned) {
		return lastActioned.plusMonths(noticeEvery);
	}

	private LocalDateTime increaseTimeStampByYears(int noticeEvery, LocalDateTime lastActioned) {
		return lastActioned.plusYears(noticeEvery);
	}

	private LocalDateTime reduceTimeStampByWeeks(int advanceNotice, LocalDateTime nextAction) {
		return nextAction.minusWeeks(advanceNotice);
	}

	private LocalDateTime reduceTimeStampByMonths(int advanceNotice, LocalDateTime nextAction) {
		return nextAction.minusMonths(advanceNotice);
	}

	private LocalDateTime reduceTimeStampByYears(int advanceNotice, LocalDateTime nextAction) {
		return nextAction.minusYears(advanceNotice);
	}

	@Override
	public int compareTo(MonitoredItem that) {
		return this.description.compareTo(that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonitoredItem other = (MonitoredItem) obj;
		return Objects.equals(description, other.description);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (description != null) {
			builder.append(description);
		}
		return builder.toString();
	}

}
