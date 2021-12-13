package com.brailsoft.property.management.edit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ChangeManagerTest {

	private Change change = new UTChange();
	private Counter count = new Counter();

	@Test
	void testGetInstance() {
		assertNotNull(ChangeManager.getInstance());
	}

	@Test
	void testExecute() {
		assertEquals(Change.State.READY, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().execute(change);
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
	}

	@Test
	void testUndo() {
		assertEquals(Change.State.READY, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().execute(change);
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
		ChangeManager.getInstance().undo();
		assertEquals(Change.State.UNDONE, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
	}

	@Test
	void testRedo() {
		assertEquals(Change.State.READY, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().execute(change);
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
		ChangeManager.getInstance().undo();
		assertEquals(Change.State.UNDONE, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().redo();
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
	}

	@Test
	void testMultiUndo() {
		assertEquals(Change.State.READY, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().execute(change);
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
		ChangeManager.getInstance().undo();
		assertEquals(Change.State.UNDONE, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().undo();
		assertEquals(Change.State.UNDONE, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
	}

	@Test
	void testMultiRedo() {
		assertEquals(Change.State.READY, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().execute(change);
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
		ChangeManager.getInstance().undo();
		assertEquals(Change.State.UNDONE, change.getState());
		assertEquals(1, ((UTChange) change).getCount());
		ChangeManager.getInstance().redo();
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
		ChangeManager.getInstance().redo();
		assertEquals(Change.State.DONE, change.getState());
		assertEquals(2, ((UTChange) change).getCount());
	}

	@Test
	void testUndoAndRedoMatch() {
		for (int i = 0; i < 10; i++) {
			CounterChange change = new CounterChange(count);
			assertEquals(Change.State.READY, change.getState());
			ChangeManager.getInstance().execute(change);
		}
		assertEquals(10, count.getCount());
		for (int i = 0; i < 10; i++) {
			ChangeManager.getInstance().undo();
		}
		assertEquals(0, count.getCount());
		for (int i = 0; i < 10; i++) {
			ChangeManager.getInstance().redo();
		}
		assertEquals(10, count.getCount());
	}

	private class UTChange extends AbstractChange {
		private int count = 1;

		public UTChange() {
			super();
		}

		@Override
		protected void doHook() throws Failure {
			count++;
		}

		@Override
		protected void undoHook() throws Failure {
			count--;
		}

		@Override
		protected void redoHook() throws Failure {
			count++;
		}

		public int getCount() {
			return count;
		}

	}

	private class Counter {
		private int count;

		void inc() {
			count++;
		}

		void dec() {
			count--;
		}

		int getCount() {
			return count;
		}
	}

	private class CounterChange extends AbstractChange {
		private Counter count;

		public CounterChange(Counter count) {
			this.count = count;
		}

		@Override
		protected void doHook() throws Failure {
			redoHook();
		}

		@Override
		protected void undoHook() throws Failure {
			count.dec();
		}

		@Override
		protected void redoHook() throws Failure {
			count.inc();
		}

	}
}
