package com.brailsoft.property.management.edit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.brailsoft.property.management.edit.Change.State;

class ChangeTest {

	private Change stateErrorChange = new UTChange(State.STUCK, 0);

	@Test
	void testGetState() {
		assertEquals(State.STUCK, stateErrorChange.getState());
	}

	@Test
	void testExecute() {
		assertThrows(AssertionError.class, () -> {
			stateErrorChange.execute();
		});
	}

	@Test
	void testUndo() {
		assertThrows(AssertionError.class, () -> {
			stateErrorChange.undo();
		});
	}

	@Test
	void testRedo() {
		assertThrows(AssertionError.class, () -> {
			stateErrorChange.redo();
		});
	}

	@Test
	void testExecuteFailure() {
		Change failureChange = new UTChange(State.READY, 1);
		assertEquals(State.READY, failureChange.getState());
		failureChange.execute();
		assertEquals(State.STUCK, failureChange.getState());
	}

	@Test
	void testUndoFailure() {
		Change failureChange = new UTChange(State.DONE, 2);
		assertEquals(State.DONE, failureChange.getState());
		failureChange.undo();
		assertEquals(State.STUCK, failureChange.getState());
	}

	@Test
	void testRedoFailure() {
		Change failureChange = new UTChange(State.UNDONE, 3);
		assertEquals(State.UNDONE, failureChange.getState());
		failureChange.redo();
		assertEquals(State.STUCK, failureChange.getState());
	}

	private class UTChange extends AbstractChange {
		private int failurePoint = 0;

		private UTChange(State state, int failurePoint) {
			this.state = state;
			this.failurePoint = failurePoint;
		}

		@Override
		protected void doHook() throws Failure {
			if (failurePoint == 1) {
				throw new Failure();
			}
		}

		@Override
		protected void undoHook() throws Failure {
			if (failurePoint == 2) {
				throw new Failure();
			}
		}

		@Override
		protected void redoHook() throws Failure {
			if (failurePoint == 3) {
				throw new Failure();
			}
		}

	}

}
