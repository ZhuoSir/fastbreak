package com.chen.fastbreak;

/**
 * 基础熔断器；
 *
 *  熔断器在三个状态间切换，CLOSED -> OPEN -> HALFOPEN -> CLOSED/OPEN
 *
 *  CLOSED 熔断器关闭，服务可以正常访问；
 *  OPEN， 熔断器打开，服务不能访问；
 *  HALFOPEN，熔断器半开，服务可以尝试访问，达到一定情况会选择彻底打开或者关闭；
 *
 * */
public class BaseCircuitBreaker<T> extends AbstractCircuitBreaker<T> {

    /**
     * @param tripThreshold             熔断阈值
     * @param thresholdWindow           熔断有效时间阈值
     * @param halfOpenTimeout           半开状态有效时间
     * @param succThresholdInHalfOpen   半开状态有效阈值
     * @param circuitBreakerFallBack    熔断器执行回调
     *
     * */
    public BaseCircuitBreaker(int tripThreshold,
                              int thresholdWindow,
                              int halfOpenTimeout,
                              int succThresholdInHalfOpen,
                              CircuitBreakerFallBack<T> circuitBreakerFallBack) {
        super(new BaseCircuitBreakerPolicy(tripThreshold, thresholdWindow, halfOpenTimeout, succThresholdInHalfOpen), circuitBreakerFallBack);
    }
}
