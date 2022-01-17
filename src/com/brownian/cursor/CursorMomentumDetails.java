package com.brownian.cursor;

import java.awt.*;

/**
 * TODO - comment ... Record that ...
 */
public record CursorMomentumDetails(CursorPositionDetails currentPosition, float xSpeedPxPerMs, float ySpeedPxPerMs) {
    /**
     * Predict the position of the cursor will be in the given milliseconds.
     *
     * @param futureMs Future time we are predicting.
     * @return Predicted cursor coordinates.
     */
    public Point predictCursorPosition(int futureMs) {
        Point currentPosition = this.currentPosition.position();

        int xPos = currentPosition.x + (int) (this.xSpeedPxPerMs * futureMs),
            yPos = currentPosition.y + (int) (this.ySpeedPxPerMs * futureMs);
        return new Point(xPos, yPos);
    }
}
