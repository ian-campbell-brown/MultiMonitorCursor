package com.brownian;


import com.brownian.cursor.CursorImpl;
import com.brownian.exceptions.InvalidMonitorConfigurationException;
import com.brownian.monitors.MonitorLayout;
import com.brownian.monitors.MonitorLayoutFactory;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;


public class Main {

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        CursorImpl cursor = new CursorImpl();

        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseMotionListener(cursor);

        try {
            MonitorLayout layout = new MonitorLayoutFactory().getMonitorLayout();
            MonitorTransitionSmoother transitionSmoother = new MonitorTransitionSmoother(layout, cursor);
        } catch (InvalidMonitorConfigurationException exception) {
            System.out.print(exception.getMessage());
            System.exit(1);
        }
    }
}
