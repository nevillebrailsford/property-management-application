package com.brailsoft.property.management.edit;

import java.util.Stack;
import java.util.logging.Logger;

import com.brailsoft.property.management.constant.Constants;

public class ChangeManager {
	private static final String CLASS_NAME = ChangeManager.class.getName();
	private static final Logger LOGGER = Logger.getLogger(Constants.LOGGER_NAME);

	private static ChangeManager instance = null;

	private Stack<Change> undoStack = new Stack<>();
	private Stack<Change> redoStack = new Stack<>();

	public static synchronized ChangeManager getInstance() {
		if (instance == null) {
			instance = new ChangeManager();
		}
		return instance;
	}

	private ChangeManager() {
	}

	public void reset() {
		undoStack.clear();
		redoStack.clear();
	}

	public void execute(Change change) {
		LOGGER.entering(CLASS_NAME, "execute", change);
		change.execute();
		if (change.getState() == Change.State.DONE) {
			undoStack.push(change);
			redoStack.clear();
		} else {
			undoStack.clear();
			redoStack.clear();
		}
		LOGGER.exiting(CLASS_NAME, "execute");
	}

	public void undo() {
		LOGGER.entering(CLASS_NAME, "undo");
		if (undoStack.size() > 0) {
			Change change = undoStack.pop();
			change.undo();
			if (change.getState() == Change.State.UNDONE) {
				redoStack.push(change);
			} else {
				undoStack.clear();
				redoStack.clear();
			}
		}
		LOGGER.exiting(CLASS_NAME, "undo");
	}

	public void redo() {
		LOGGER.entering(CLASS_NAME, "redo");
		if (redoStack.size() > 0) {
			Change change = redoStack.pop();
			change.redo();
			if (change.getState() == Change.State.DONE) {
				undoStack.push(change);
			} else {
				undoStack.clear();
				redoStack.clear();
			}
		}
		LOGGER.exiting(CLASS_NAME, "redo");
	}
}
