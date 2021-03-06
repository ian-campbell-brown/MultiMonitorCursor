package com.brownian.monitors;

import com.brownian.exceptions.InvalidMonitorConfigurationException;

import java.awt.*;

/**
 * Class for retrieving information about the current layout of the monitors.
 * Only supports 2 side by side at this time.
 */
public class MonitorLayoutFactory {
    /**
     * Returns the current monitor layout.
     *
     * @return current monitor layout.
     * @throws InvalidMonitorConfigurationException When current configuration is not valid/supported.
     */
    public MonitorLayout getMonitorLayout() throws InvalidMonitorConfigurationException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] monitorCollection = ge.getScreenDevices();
        if (monitorCollection.length != 2) {
            throw new InvalidMonitorConfigurationException("Currently only support 2 side by side monitors");
        }

        MonitorDetails leftMonitor = null, rightMonitor = null;
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            MonitorDetails monitorDetails = new MonitorDetails(
                    gd.getDisplayMode().getWidth(),
                    gd.getDisplayMode().getHeight());

            if (leftMonitor == null) {
                leftMonitor = monitorDetails;
            } else {
                rightMonitor = monitorDetails;
            }
        }

        return new MonitorLayout(leftMonitor, rightMonitor);
    }
}
