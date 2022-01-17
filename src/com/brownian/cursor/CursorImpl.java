package com.brownian.cursor;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
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
    private CursorPositionDetails lastPosition;

    /**
     * Current momentum the cursor has.
     */
    private CursorMomentumDetails currentMomentum;

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
     * Calculate the current momentum details of the mouse cursor.
     */
    private void updateCursorMomentum(Point current) {
        float xSpeed = 0;
        float ySpeed = 0;

        if (this.lastPosition != null) {
            long currentTime = new Date().getTime();
            long timeDif = currentTime - this.lastPosition.timeStamp();

            Point previous = this.lastPosition.position();

            xSpeed = (current.x - previous.x) / (float) timeDif;
            ySpeed = (current.y - previous.y) / (float) timeDif;
        }

        if (Float.isNaN(xSpeed) || Float.isInfinite(xSpeed)) {
            xSpeed = 0;
        }

        if (Float.isNaN(ySpeed) || Float.isInfinite(ySpeed)) {
            ySpeed = 0;
        }

        this.lastPosition = new CursorPositionDetails(current, new Date().getTime());
        this.currentMomentum = new CursorMomentumDetails(this.lastPosition, xSpeed, ySpeed);
    }

    /**
     * Updates all listeners with current position details of the cursor.
     */
    private void updateCursor(Point currentPosition) {
        if (!this.enabledUpdates) {
            // We are currently ignoring updates.
            return;
        }

        this.updateCursorMomentum(currentPosition);
        this.listenerCollection.forEach(listener -> listener.OnCursorMove(this.currentMomentum));
    }
}