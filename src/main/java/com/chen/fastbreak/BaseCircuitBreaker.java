package com.chen.fastbreak;

public class BaseCircuitBreaker<T> extends AbstractCircuitBreaker<T> {


    public BaseCircuitBreaker(CircuitBreakerConfig circuitBreakerConfig,
                              CircuitBreakerRunner<T> circuitBreakerRunner) {
        super(new BaseCircuitBreakerPolicy(circuitBreakerConfig), circuitBreakerRunner);
    }
}
