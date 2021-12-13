package com.brailsoft.property.management.edit;

public interface Change {
	enum State {
		READY, DONE, UNDONE, STUCK
	};

	State getState();

	void execute();

	void undo();

	void redo();
}
