package com.chen.fastbreak.breaker;

public interface CircuitBreakerPolicy {

    boolean isDisabled();

    void successfulCall();

    void unsuccessfulCall();

    void unsuccessfulCall(Exception e);

    CircuitBreakerState currentState();

    boolean shouldAttemptReset();

}
