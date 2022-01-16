package com.brownian.cursor;

import java.awt.*;

public interface Cursor {
    /**
     * Returns the current position of the cursor.
     *
     * @return current point
     */
    Point getCurrentPosition();

    /**
     * Moves the cursor to the new position.
     *
     * @param newPosition Point on monitors to move cursor.
     */
    void moveTo(Point newPosition);
}
