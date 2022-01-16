package com.brownian.cursor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CursorEvents {
    /**
     * Interval in milliseconds for which we poll for updates to the mouse cursor.
     */
    private final int UpdateIntervalMs = 5;

    /**
     * Collection of all the listeners for cursor movement events.
     */
    private final ArrayList<CursorEventsListener> listenerCollection = new ArrayList<>();

    /**
     * Cursor we will be monitoring.
     */
    private final Cursor cursor;

    /**
     * Momentum of the mouse cursor on our last poll.
     */
    private CursorMomentumDetails lastMomentum = null;
    
    /**
     * Constructor for the CursorEvents.
     * Starts the polling timer to check the cursor position.
     */
    public CursorEvents(Cursor cursor) {
        this.cursor = cursor;

        // Lets just poll every 50ms for current cursor position.
        // TODO - research better ways of tracking mouse vs brute polling.
        //      ie. https://github.com/kwhat/jnativehook
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCursor();
            }
        }, 0, UpdateIntervalMs);
    }

    /**
     * Registers the given listener, allowing them to be notified of any changes to the mouse cursor.
     *
     * @param listener Object wishing to be notified of cursor movements.
     */
    public void registerListener(CursorEventsListener listener) {
        this.listenerCollection.add(listener);
    }

    /**
     * Returns the current position details of the mouse cursor.
     *
     * @return The current position details of the mouse cursor.
     */
    private CursorMomentumDetails getCurrentPosition() {
        Point current = this.cursor.getCurrentPosition();
        Point previous = null;
        Point predicted = null;

        if (this.lastMomentum != null) {
            // Predict the next cursor position from the previous position.
            // TODO - more advanced prediction techniques (don't assume straight line from previous).
            previous = this.lastMomentum.currentPosition();

            int xDif = current.x - previous.x;
            int yDif = current.y - previous.y;
            predicted = new Point(current.x + xDif, current.y + yDif);
        } else {
            // Cursor isn't moving.
            previous = current;
            predicted = current;
        }

        return new CursorMomentumDetails(previous, current, predicted);
    }

    /**
     * Updates all listeners with current position details of the cursor.
     */
    private void updateCursor() {
        this.lastMomentum = this.getCurrentPosition();
        this.listenerCollection.forEach(listener -> listener.OnCursorMove(this.lastMomentum));
    }
}
