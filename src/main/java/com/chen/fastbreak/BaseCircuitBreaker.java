package com.chen.fastbreak;

public class BaseCircuitBreaker<T> extends AbstractCircuitBreaker<T> {

    public BaseCircuitBreaker(int tripThreshold,
                              int thresholdWindow,
                              int halfOpenTimeout,
                              int succThresholdInHalfOpen,
                              CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        super(new BaseCircuitBreakerPolicy(tripThreshold, thresholdWindow, halfOpenTimeout, succThresholdInHalfOpen), circuitBreakerFallBack);
    }
}
