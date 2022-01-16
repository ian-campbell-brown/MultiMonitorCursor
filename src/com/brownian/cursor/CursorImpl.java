package com.brownian.cursor;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CursorImpl implements Cursor, NativeMouseMotionListener {
    /**
     * Collection of all the listeners for cursor movement events.
     */
    private final ArrayList<CursorEventsListener> listenerCollection = new ArrayList<>();

    /**
     * Last measured momentum for the cursor.
     */
    private CursorMomentumDetails lastMomentum;

    /**
     * Flag indicating whether updates are enabled.
     */
    private Boolean enabledUpdates = true;

    /**
     * Moves the cursor to the new position.
     *
     * @param newPosition Point on monitors to move cursor.
     */
    public void moveTo(Point newPosition) {
        try {
            // Disable the updates momentarily, so we don't get stuck in a loop.
            // TODO - right to left still janky, temp disable updates somewhat resolves issues but still much to improve.
            this.enabledUpdates = false;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    enableUpdates();
                }
            }, 50);

            Robot robot = new Robot();
            robot.mouseMove(newPosition.x, newPosition.y);
            System.out.println(newPosition);
        } catch (AWTException exception) {
            System.out.print(exception.getMessage());
        }
    }

    /**
     * Invoked when the mouse has been moved.
     *
     * @param nativeEvent the native mouse event.
     */
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        this.updateCursor(new Point(nativeEvent.getX(), nativeEvent.getY()));
    }

    /**
     * Invoked when the mouse has been moved while a button is depressed.
     *
     * @param nativeEvent the native mouse event
     * @since 1.1
     */
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        this.updateCursor(new Point(nativeEvent.getX(), nativeEvent.getY()));
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
     * Re enable the updates.
     */
    private void enableUpdates() {
        this.enabledUpdates = true;
    }

    /**
     * Returns the current position details of the mouse cursor.
     *
     * @return The current position details of the mouse cursor.
     */
    private void getCurrentPosition(Point current) {
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

        this.lastMomentum = new CursorMomentumDetails(previous, current, predicted);
    }

    /**
     * Updates all listeners with current position details of the cursor.
     */
    private void updateCursor(Point currentPosition) {
        if (!this.enabledUpdates) {
            // We are currently ignoring updates.
            return;
        }

        this.getCurrentPosition(currentPosition);
        this.listenerCollection.forEach(listener -> listener.OnCursorMove(this.lastMomentum));
    }
}