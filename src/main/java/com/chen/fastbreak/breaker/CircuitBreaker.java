package com.chen.fastbreak.breaker;

public interface CircuitBreaker<T> {

    T execute() throws Throwable;

}
