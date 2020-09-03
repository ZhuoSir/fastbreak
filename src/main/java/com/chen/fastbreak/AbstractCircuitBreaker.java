package com.chen.fastbreak;

public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T> {


    CircuitBreakerPolicy circuitBreakerPolicy;

    CircuitBreakerFallBack<T> circuitBreakerFallBack;

    public AbstractCircuitBreaker(CircuitBreakerPolicy circuitBreakerPolicy, CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        this.circuitBreakerPolicy = circuitBreakerPolicy;
        this.circuitBreakerFallBack = circuitBreakerFallBack;
    }

    public T execute() throws Throwable {

        T ret = null;
        if (circuitBreakerPolicy.isDisabled()) {
            // 熔断器打开状态
            circuitBreakerFallBack.fallBack();
            return null;
        }

        try {
            //执行
            ret = circuitBreakerFallBack.call();
            circuitBreakerPolicy.successfulCall();
        } catch (Exception e) {
            circuitBreakerPolicy.unsuccessfulCall(e);
//            circuitBreakerFallBack.fallBack();
            throw e;
        }

        return ret;
    }
}
