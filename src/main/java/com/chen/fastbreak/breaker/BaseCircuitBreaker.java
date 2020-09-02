package com.chen.fastbreak.breaker;

public class BaseCircuitBreaker<T> extends AbstractCircuitBreaker<T> {


    public BaseCircuitBreaker(int tripThreshold,
                              int thresholdWindow,
                              int halfOpenTimeOut,
                              CircuitBreakerRunner<T> circuitBreakerRunner) {
        super(new BaseCircuitBreakerPolicy(tripThreshold, thresholdWindow, halfOpenTimeOut), circuitBreakerRunner);
    }
}
