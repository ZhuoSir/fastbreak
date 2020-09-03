package com.chen.fastbreak;

public class SimpleCircuitBreaker<T> extends AbstractCircuitBreaker<T> {

    public SimpleCircuitBreaker(int tripThreshold, int thresholdWindow, int openTimeOut, CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        super(new SimpleCircuitBreakerPolicy(tripThreshold, thresholdWindow, openTimeOut), circuitBreakerFallBack);
    }
}
