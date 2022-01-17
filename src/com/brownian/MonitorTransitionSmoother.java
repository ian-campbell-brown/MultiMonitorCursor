package com.brownian;

import com.brownian.cursor.Cursor;
import com.brownian.cursor.CursorEventsListener;
import com.brownian.cursor.CursorMomentumDetails;
import com.brownian.monitors.MonitorLayout;

import java.awt.*;

public class MonitorTransitionSmoother implements CursorEventsListener {
    /**
     * TODO - comment
     */
    private final int FuturePredictionMs = 15;

    /**
     * TODO - comment
     */
    private final Cursor cursor;

    /**
     * Current layout of the monitors.
     */
    private final MonitorLayout monitorLayout;

    /**
     * Flag letting us know if smoothing is required for the cursor when transition monitors.
     */
    private final Boolean smoothingRequired;

    /**
     * Flag indicating we should skip the next update.
     */
    private Boolean skipUpdate = false;

    /**
     * Position of the cursor on the last update.
     */
    private Point previousPosition;

    /**
     * TODO - MonitorEvents to update the monitor configuration (resolution changes) with out having to restart.
     *
     * @param monitorLayout
     * @param cursor
     */
    public MonitorTransitionSmoother(MonitorLayout monitorLayout, Cursor cursor) {
        this.cursor = cursor;
        this.monitorLayout = monitorLayout;
        this.smoothingRequired = !this.monitorLayout.monitorsHaveSameResolution();

        cursor.registerListener(this);
    }

    /**
     * Updates the listener with the new position details for the mouse cursor.
     *
     * @param cursorMomentum Current momentum the cursor has.
     */
    @Override
    public void OnCursorMove(CursorMomentumDetails cursorMomentum) {
        if (!this.smoothingRequired || this.skipUpdate) {
            // Smoothing isn't required with current monitor setup so just return.
            this.skipUpdate = false;
            return;
        }

        Point current = cursorMomentum.currentPosition().position();
        Point previous = (this.previousPosition == null) ? current : this.previousPosition;
        Point predicted = cursorMomentum.predictCursorPosition(FuturePredictionMs);

        if (this.transitionedMonitor(previous, current)) {
            // Double check we didn't miss a transition that we can attempt to retro-actively smooth.
            Point correctedPosition = this.correctPredictedPoint(previous, current);
            this.moveMouse(correctedPosition);
        } else if (this.transitionedMonitor(current, predicted)) {
            // It's predicted cursor will transition to the other monitor on next update.
            // Let's preemptively move the cursor to the correct position on the other monitor.
            Point correctedPosition = this.correctPredictedPoint(current, predicted);
            this.moveMouse(correctedPosition);
        } else if (current.x == this.monitorLayout.leftMonitor().widthPx() - 1) {
            // Stuck on the transition, popover to next monitor.
            predicted.x = current.x + 1;
            this.moveMouse(this.correctPredictedPoint(current, predicted));
        }
    }

    /**
     * Corrects the predicted position so it has a smooth transition when crossing to a monitor with a different resolution.
     * Ie. When crossing at middle don't want cursor to jump to middle of monitor beside it, we want the cursor to smoothly
     * transition to the middle like it would if both monitors had the same resolution.
     * <p>
     * TODO - support x axis scaling.
     *
     * @param cursor1
     * @param predicted
     * @return
     */
    private Point correctPredictedPoint(Point cursor1, Point predicted) {
        if (this.isOnLeftMonitor(cursor1)) {
            // Transitioning left to right. Will need to scale cursor2 to be at the same relative height on the second
            // monitor as it was on the first.
            float yPercent = (float) predicted.y / this.monitorLayout.leftMonitor().heightPx();
            predicted.y = (int) (yPercent * this.monitorLayout.rightMonitor().heightPx());
        } else {
            // Transitioning right to left.
            float yPercent = (float) predicted.y / this.monitorLayout.rightMonitor().heightPx();
            predicted.y = (int) (yPercent * this.monitorLayout.leftMonitor().heightPx());
        }

        return predicted;
    }

    /**
     * Returns true when cursor currently on the left monitor.
     *
     * @param cursorPosition Current position of the cursor.
     * @return Details for the current monitor.
     */
    private Boolean isOnLeftMonitor(Point cursorPosition) {
        return cursorPosition.x < this.monitorLayout.leftMonitor().widthPx();
    }

    /**
     * Returns true if the two positions are on different monitors.
     *
     * @return
     */
    private Boolean transitionedMonitor(Point cursor1, Point cursor2) {
        return this.isOnLeftMonitor(cursor1) != this.isOnLeftMonitor(cursor2);
    }

    /**
     * TODO - comment
     *
     * @param newPosition
     */
    private void moveMouse(Point newPosition) {
        // We can get stuck in an update loop from use trying to smooth the transition of the cursor
        // we just manually moved, causing undefined and odd behaviour. Easiest solution is to just the next update.
        this.skipUpdate = true;
        this.cursor.moveTo(newPosition);
    }
}