package com.chen.fastbreak.breaker;

public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T> {

    CircuitBreakerState state = CircuitBreakerState.CLOSED;

    CircuitBreakerPolicy circuitBreakerPolicy;

    CircuitBreakerRunner<T> circuitBreakerRunner;

    public AbstractCircuitBreaker(CircuitBreakerPolicy circuitBreakerPolicy, CircuitBreakerRunner<T> circuitBreakerRunner) {
        this.circuitBreakerPolicy = circuitBreakerPolicy;
        this.circuitBreakerRunner = circuitBreakerRunner;
    }

    public T execute() throws Throwable {

        T ret = null;
        if (circuitBreakerPolicy.currentState().equals(CircuitBreakerState.OPEN)
                && !circuitBreakerPolicy.shouldAttemptReset()) {
            // 熔断器打开状态
            circuitBreakerRunner.fallBack();
        }

        try {
            //执行
            ret = circuitBreakerRunner.run();
            circuitBreakerPolicy.successfulCall();

        } catch (Exception e) {
            circuitBreakerPolicy.unsuccessfulCall(e);
            circuitBreakerRunner.fallBack(e);
        }

        return ret;
    }
}
