package com.chen.fastbreak;

public interface CircuitBreakerRunner<T> {

    T run() throws Exception;

    void fallBack();

    void fallBack(Exception e);
}
