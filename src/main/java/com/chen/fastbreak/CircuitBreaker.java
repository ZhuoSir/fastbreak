package com.chen.fastbreak;

public interface CircuitBreaker<T> {

    T execute() throws Throwable;

}
