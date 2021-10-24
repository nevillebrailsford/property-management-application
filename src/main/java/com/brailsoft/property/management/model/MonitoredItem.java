package com.brailsoft.property.management.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MonitoredItem implements Comparable<MonitoredItem> {
	private StringProperty description = new SimpleStringProperty(this, "description", "");
	private ObjectProperty<LocalDateTime> lastActionPerformed = new SimpleObjectProperty<>(this, "lastActionPerformed",
			null);
	private ObjectProperty<LocalDateTime> timeForNextAction = new SimpleObjectProperty<>(this, "timeForNextAction",
			null);
	private ObjectProperty<LocalDateTime> timeForNextNotice = new SimpleObjectProperty<>(this, "timeForNextNotice",
			null);
	private ObjectProperty<Period> periodForNextAction = new SimpleObjectProperty<>(this, "periodForNextAction", null);;
	private IntegerProperty noticeEvery = new SimpleIntegerProperty(this, "noticeEvery", 0);;
	private ObjectProperty<Period> periodForNextNotice = new SimpleObjectProperty<>(this, "periodForNextAction", null);;
	private IntegerProperty advanceNotice = new SimpleIntegerProperty(this, "advanceNotice", 0);
	private ObjectProperty<Property> owner = new SimpleObjectProperty<>(this, "owner", null);

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
		this.description.set(description);
		this.periodForNextAction.set(periodForNextAction);
		this.noticeEvery.set(noticeEvery);
		this.advanceNotice.set(advanceNotice);
		this.periodForNextNotice.set(periodForNextNotice);
		this.lastActionPerformed.set(lastActioned);
		this.timeForNextAction.set(calculateTimeForNextAction(periodForNextAction, noticeEvery, lastActioned));
		this.timeForNextNotice
				.set(calculateTimeForNextNotice(periodForNextNotice, advanceNotice, this.timeForNextAction.get()));
		this.owner.set(null);
	}

	public MonitoredItem(MonitoredItem that) {
		if (that == null) {
			throw new IllegalArgumentException("MonitoredItem: item was null");
		}
		this.description.set(that.description.get());
		this.periodForNextAction.set(that.periodForNextAction.get());
		this.noticeEvery.set(that.noticeEvery.get());
		this.advanceNotice.set(that.advanceNotice.get());
		this.periodForNextNotice.set(that.periodForNextNotice.get());
		this.lastActionPerformed.set(that.lastActionPerformed.get());
		this.timeForNextAction.set(that.timeForNextAction.get());
		this.timeForNextNotice.set(that.timeForNextNotice.get());
		if (that.owner.get() != null) {
			this.owner.set(new Property(that.owner.get()));
		} else {
			this.owner.set(null);
		}
	}

	public Property getOwner() {
		return new Property(owner.get());
	}

	public void setOwner(Property owner) {
		this.owner.set(new Property(owner));
	}

	public ObjectProperty<Property> ownerProperty() {
		return owner;
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public Period getPeriodForNextAction() {
		return periodForNextAction.get();
	}

	public void setPeriodForNextAction(Period periodForNextAction) {
		this.periodForNextAction.set(periodForNextAction);
		recalculateTimes();
	}

	public ObjectProperty<Period> periodForNextActionProperty() {
		return periodForNextAction;
	}

	public int getNoticeEvery() {
		return noticeEvery.get();
	}

	public void setNoticeEvery(int noticeEvery) {
		this.noticeEvery.set(noticeEvery);
		recalculateTimes();
	}

	public IntegerProperty noticeEveryProperty() {
		return noticeEvery;
	}

	public int getAdvanceNotice() {
		return advanceNotice.get();
	}

	public void setAdvanceNotice(int advanceNotice) {
		this.advanceNotice.set(advanceNotice);
		recalculateNextNotice();
	}

	public IntegerProperty advanceNoticeProperty() {
		return advanceNotice;
	}

	public LocalDateTime getLastActionPerformed() {
		return lastActionPerformed.get();
	}

	public LocalDateTime getTimeForNextAction() {
		return timeForNextAction.get();
	}

	public LocalDateTime getTimeForNextNotice() {
		return timeForNextNotice.get();
	}

	public Period getPeriodForNextNotice() {
		return periodForNextNotice.get();
	}

	public void setPeriodForNextNotice(Period periodForNextNotice) {
		this.periodForNextNotice.set(periodForNextNotice);
		recalculateNextNotice();
	}

	public ObjectProperty<Period> periodForNextNoticeProperty() {
		return periodForNextNotice;
	}

	public void actionPerformed(LocalDateTime when) {
		this.lastActionPerformed.set(when);
		recalculateTimes();
	}

	public ObjectProperty<LocalDateTime> lastActionPerformedProperty() {
		return lastActionPerformed;
	}

	public boolean overdue() {
		return overdue(LocalDateTime.now());
	}

	public boolean overdue(LocalDateTime today) {
		if (today.isAfter(timeForNextAction.get())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean noticeDue() {
		return noticeDue(LocalDateTime.now());
	}

	public boolean noticeDue(LocalDateTime today) {
		if (today.isAfter(timeForNextNotice.get())) {
			return true;
		} else {
			return false;
		}
	}

	private LocalDateTime calculateTimeForNextNotice(Period periodForNextNotice, int advanceNotice,
			LocalDateTime timeForNextAction) {
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
		timeForNextAction.set(
				calculateTimeForNextAction(periodForNextAction.get(), noticeEvery.get(), lastActionPerformed.get()));
	}

	private void recalculateNextNotice() {
		timeForNextNotice.set(
				calculateTimeForNextNotice(periodForNextNotice.get(), advanceNotice.get(), timeForNextAction.get()));
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
		return this.description.get().compareTo(that.description.get());
	}

	@Override
	public int hashCode() {
		return Objects.hash(description.get());
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
		return Objects.equals(description.get(), other.description.get());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (description.get() != null) {
			builder.append(description.get());
		}
		return builder.toString();
	}

}
