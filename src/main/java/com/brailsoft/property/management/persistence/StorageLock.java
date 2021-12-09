package com.brailsoft.property.management.persistence;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StorageLock {
	private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private static final Lock readLock = rwl.readLock();
	private static final Lock writeLock = rwl.writeLock();

	public static Lock readLock() {
		return readLock;
	}

	public static Lock writeLock() {
		return writeLock;
	}
}
