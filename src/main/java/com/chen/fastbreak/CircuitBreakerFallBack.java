package com.chen.fastbreak;

/**
 * 熔断器执行回调
 *
 * */
public interface CircuitBreakerFallBack<T> {

    /**
     * 具体执行方法
     *
     * */
    T call() throws Exception;

    /**
     * 熔断触发时回调接口
     *
     * */
    void fallBack();

}
