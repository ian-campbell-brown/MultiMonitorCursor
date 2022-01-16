package com.brownian;


import com.brownian.cursor.Cursor;
import com.brownian.cursor.CursorEvents;
import com.brownian.cursor.CursorImpl;
import com.brownian.exceptions.InvalidMonitorConfigurationException;
import com.brownian.monitors.MonitorLayout;
import com.brownian.monitors.MonitorLayoutFactory;


public class Main {

    public static void main(String[] args)  {
        Cursor cursor = new CursorImpl();
        CursorEvents cursorEvents = new CursorEvents(cursor);

        try {
            MonitorLayout layout = new MonitorLayoutFactory().getMonitorLayout();
            MonitorTransitionSmoother transitionSmoother = new MonitorTransitionSmoother(cursorEvents, layout, cursor);
        }
        catch (InvalidMonitorConfigurationException exception) {
            System.out.print(exception.getMessage());
        }
    }
}
