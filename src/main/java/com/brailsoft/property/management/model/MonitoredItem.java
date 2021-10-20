package com.brailsoft.property.management.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class MonitoredItem implements Comparable<MonitoredItem> {
	private String description;
	private long lastActionPerformed;
	private long timeForNextAction;
	private long timeForNextNotice;
	private Period periodForNextAction;
	private int noticeEvery;
	private Period periodForNextNotice;
	private int advanceNotice;

	public MonitoredItem(String description, Period period, int noticeEvery, LocalDateTime lastActioned,
			int advanceNotice, Period periodForNextNotice) {
		if (description == null || description.isBlank() || description.isEmpty()) {
			throw new IllegalArgumentException("MonitoredItem: description not specified");
		}
		if (period == null) {
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
		this.periodForNextAction = period;
		this.noticeEvery = noticeEvery;
		this.advanceNotice = advanceNotice;
		this.periodForNextNotice = periodForNextNotice;
		this.lastActionPerformed = convertDateToMilleseconds(lastActioned);
		this.timeForNextAction = calculateTimeForNextAction(period, noticeEvery, lastActioned);
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
		return convertMillisecondsToLocalDateTime(lastActionPerformed);
	}

	public LocalDateTime getTimeForNextAction() {
		return convertMillisecondsToLocalDateTime(timeForNextAction);
	}

	public LocalDateTime getTimeForNextNotice() {
		return convertMillisecondsToLocalDateTime(timeForNextNotice);
	}

	public Period getPeriodForNextNotice() {
		return periodForNextNotice;
	}

	public void setPeriodForNextNotice(Period periodForNextNotice) {
		this.periodForNextNotice = periodForNextNotice;
		recalculateNextNotice();
	}

	public void actionPerformed(LocalDateTime when) {
		this.lastActionPerformed = convertDateToMilleseconds(when);
		recalculateTimes();
	}

	public boolean overdue() {
		return overdue(LocalDateTime.now());
	}

	public boolean overdue(LocalDateTime today) {
		if (convertDateToMilleseconds(today) > timeForNextAction) {
			return true;
		} else {
			return false;
		}
	}

	public boolean noticeDue() {
		return noticeDue(LocalDateTime.now());
	}

	public boolean noticeDue(LocalDateTime today) {
		if (convertDateToMilleseconds(today) > timeForNextNotice) {
			return true;
		} else {
			return false;
		}
	}

	private long calculateTimeForNextNotice(long timeForNextAction, int advanceNotice, Period periodForNextNotice) {
		LocalDateTime nextDate = convertMillisecondsToLocalDateTime(timeForNextAction);
		var result = switch (periodForNextNotice) {
			case WEEKLY -> reduceTimeStampByWeeks(advanceNotice, nextDate);
			case MONTHLY -> reduceTimeStampByMonths(advanceNotice, nextDate);
			case YEARLY -> reduceTimeStampByYears(advanceNotice, nextDate);
		};
		return result;
	}

	private long calculateTimeForNextAction(Period period, int noticeEvery, LocalDateTime lastActioned) {
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
		timeForNextAction = calculateTimeForNextAction(periodForNextAction, noticeEvery,
				convertMillisecondsToLocalDateTime(lastActionPerformed));
	}

	private void recalculateNextNotice() {
		timeForNextNotice = calculateTimeForNextNotice(timeForNextAction, advanceNotice, periodForNextNotice);
	}

	private long increaesTimeStampByWeeks(int noticeEvery, LocalDateTime lastActioned) {
		return convertDateToMilleseconds(lastActioned.plusWeeks(noticeEvery));
	}

	private long increaseTimeStampByMonths(int noticeEvery, LocalDateTime lastActioned) {
		return convertDateToMilleseconds(lastActioned.plusMonths(noticeEvery));
	}

	private long increaseTimeStampByYears(int noticeEvery, LocalDateTime lastActioned) {
		return convertDateToMilleseconds(lastActioned.plusYears(noticeEvery));
	}

	private long reduceTimeStampByWeeks(int advanceNotice, LocalDateTime nextAction) {
		return convertDateToMilleseconds(nextAction.minusWeeks(advanceNotice));
	}

	private long reduceTimeStampByMonths(int advanceNotice, LocalDateTime nextAction) {
		return convertDateToMilleseconds(nextAction.minusMonths(advanceNotice));
	}

	private long reduceTimeStampByYears(int advanceNotice, LocalDateTime nextAction) {
		return convertDateToMilleseconds(nextAction.minusYears(advanceNotice));
	}

	private long convertDateToMilleseconds(LocalDateTime lastActioned) {
		return lastActioned.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	private LocalDateTime convertMillisecondsToLocalDateTime(long milliseconds) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
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
