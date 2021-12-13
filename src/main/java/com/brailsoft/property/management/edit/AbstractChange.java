package com.brailsoft.property.management.edit;

import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;

public abstract class AbstractChange implements Change {
	private static final String CLASS_NAME = AbstractChange.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	State state = State.READY;

	@Override
	public State getState() {
		LOGGER.entering(CLASS_NAME, "getState");
		LOGGER.exiting(CLASS_NAME, "getState", state);
		return state;
	}

	@Override
	public void execute() {
		LOGGER.entering(CLASS_NAME, "execute");
		assert state == State.READY;
		try {
			doHook();
			state = State.DONE;
		} catch (Failure e) {
			state = State.STUCK;
		} catch (Throwable e) {
			assert false;
		} finally {
			LOGGER.exiting(CLASS_NAME, "execute");
		}

	}

	@Override
	public void undo() {
		LOGGER.exiting(CLASS_NAME, "undo");
		assert state == State.DONE;
		try {
			undoHook();
			state = State.UNDONE;
		} catch (Failure e) {
			state = State.STUCK;
		} catch (Throwable e) {
			assert false;
		} finally {
			LOGGER.exiting(CLASS_NAME, "undo");
		}
	}

	@Override
	public void redo() {
		LOGGER.entering(CLASS_NAME, "redo");
		assert state == State.UNDONE;
		try {
			redoHook();
			state = State.DONE;
		} catch (Failure e) {
			state = State.STUCK;
		} catch (Throwable e) {
			assert false;
		} finally {
			LOGGER.exiting(CLASS_NAME, "redo");
		}
	}

	protected abstract void doHook() throws Failure;

	protected abstract void undoHook() throws Failure;

	protected abstract void redoHook() throws Failure;

}
