package com.brownian.cursor;

/**
 * Interface which allows for the listening for mouse cursor movement events.
 */
public interface CursorEventsListener {

    /**
     * Updates the listener with the new position details for the mouse cursor.
     *
     * @param cursorPosition New position for the cursor.
     */
    void OnCursorMove(CursorMomentumDetails cursorPosition);
}
