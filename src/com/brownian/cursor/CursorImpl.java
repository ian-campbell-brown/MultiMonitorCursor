package com.brownian.cursor;

import java.awt.*;

public class CursorImpl implements Cursor {
    /**
     * Returns the current position of the cursor.
     *
     * @return current point
     */
    public Point getCurrentPosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        return pointerInfo.getLocation();
    }

    /**
     * Moves the cursor to the new position.
     *
     * @param newPosition Point on monitors to move cursor.
     */
    public void moveTo(Point newPosition) {
        try {
            Robot robot = new Robot();
            robot.mouseMove(newPosition.x, newPosition.y);
        } catch (AWTException exception) {
            System.out.print(exception.getMessage());
        }
    }
}