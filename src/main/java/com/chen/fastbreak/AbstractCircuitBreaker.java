package com.chen.fastbreak;

/**
 * CircuitBreaker的核心思想实现；
 *
 * circuitBreakerPolicy 为CircuitBreaker熔断逻辑代理类；
 * circuitBreakerFallBack 为熔断处理的回调接口；
 * */
public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T> {

    CircuitBreakerPolicy circuitBreakerPolicy;

    CircuitBreakerFallBack<T> circuitBreakerFallBack;

    public AbstractCircuitBreaker(CircuitBreakerPolicy circuitBreakerPolicy, CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        this.circuitBreakerPolicy = circuitBreakerPolicy;
        this.circuitBreakerFallBack = circuitBreakerFallBack;
    }

    /**
     * 执行具体业务逻辑的运行方法，也是CircuitBreake的逻辑核心
     *
     * 首先检查当前熔断器状态（#CircuitBreakerState.CLOSED,CircuitBreakerState.OPEN,CircuitBreakerState.HALFOPEN）
     * 如果状态为OPEN，熔断功能开启，返回拒绝执行；
     * 如果状态为HALFOPEN, 熔断功能半开，可以尝试执行，但如果执行结果失败次数达到阈值，状态改为OPEN；
     *
     * 如果状态为CLOSED，熔断关闭；
     * 执行业务逻辑代码，成功回调；发生异常则失败，失败回调；
     *
     * */
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
            throw e;
        }

        return ret;
    }
}
