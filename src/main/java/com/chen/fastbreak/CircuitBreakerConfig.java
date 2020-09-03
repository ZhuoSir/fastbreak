package com.chen.fastbreak;

public class CircuitBreakerConfig {

    private static CircuitBreakerConfig defaultConfig = new CircuitBreakerConfig();

    private int tripThreshold = 10;

    private int thresholdWindow = 10 * 6000;

    private int halfOpenTimeout = 5 * 6000;

    private int succThresholdInHalfOpen = 5;

    public CircuitBreakerConfig() {
    }

    public static CircuitBreakerConfig defaultConfig() {
        return defaultConfig;
    }

    public int getTripThreshold() {
        return tripThreshold;
    }

    public void setTripThreshold(int tripThreshold) {
        this.tripThreshold = tripThreshold;
    }

    public int getThresholdWindow() {
        return thresholdWindow;
    }

    public void setThresholdWindow(int thresholdWindow) {
        this.thresholdWindow = thresholdWindow;
    }

    public int getHalfOpenTimeout() {
        return halfOpenTimeout;
    }

    public void setHalfOpenTimeout(int halfOpenTimeout) {
        this.halfOpenTimeout = halfOpenTimeout;
    }

    public int getSuccThresholdInHalfOpen() {
        return succThresholdInHalfOpen;
    }

    public void setSuccThresholdInHalfOpen(int succThresholdInHalfOpen) {
        this.succThresholdInHalfOpen = succThresholdInHalfOpen;
    }
}
