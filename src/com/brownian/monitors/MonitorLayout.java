package com.brownian.monitors;

/**
 * Class for retrieving information about the current layout of the monitors.
 * Only supports 2 side by side at this time.
 */
public record MonitorLayout(MonitorDetails leftMonitor, MonitorDetails rightMonitor) {
    /**
     * Returns true if both monitors have the same resolution.
     *
     * @return True if both monitors have the same resolution.
     */
    public Boolean monitorsHaveSameResolution() {
        return this.leftMonitor.equals(this.rightMonitor);
    }
}