package com.chen.fastbreak;

public interface CircuitBreakerFallBack<T> {

    T call() throws Exception;

    void fallBack();

}
