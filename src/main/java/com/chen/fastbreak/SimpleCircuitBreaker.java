package com.chen.fastbreak;

/**
 * 简易式熔断器
 *
 * 熔断器只在CLOSED , OPEN中切换；
 *
 * 在一定的频率内，失败次数达到一定阈值，则切换为OPEN，在固定时间内会切换到CLOSED；
 *
 * */
public class SimpleCircuitBreaker<T> extends AbstractCircuitBreaker<T> {

    /**
     * @param tripThreshold   熔断次数阈值
     * @param thresholdWindow 熔断范围阈值
     * @param openTimeOut     熔断时间限制
     * @param circuitBreakerFallBack 熔断回调接口
     *
     * */
    public SimpleCircuitBreaker(int tripThreshold, int thresholdWindow, int openTimeOut, CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        super(new SimpleCircuitBreakerPolicy(tripThreshold, thresholdWindow, openTimeOut), circuitBreakerFallBack);
    }
}
