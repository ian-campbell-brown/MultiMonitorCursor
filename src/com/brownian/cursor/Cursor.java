package com.brownian.cursor;

import java.awt.*;

public interface Cursor {
    /**
     * Moves the cursor to the new position.
     *
     * @param newPosition Point on monitors to move cursor.
     */
    void moveTo(Point newPosition);

    /**
     * Registers the given listener, allowing them to be notified of any changes to the mouse cursor.
     *
     * @param listener Object wishing to be notified of cursor movements.
     */
    void registerListener(CursorEventsListener listener);
}
