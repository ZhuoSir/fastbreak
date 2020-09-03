package com.chen.fastbreak;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chen.fastbreak.CircuitBreakerState.*;

/**
 * 基础熔断器代理
 *
 * 熔断器在三个状态间切换，CLOSED -> OPEN -> HALFOPEN -> CLOSED/OPEN
 *
 * CLOSED 熔断器关闭，服务可以正常访问；
 * OPEN， 熔断器打开，服务不能访问；
 * HALFOPEN，熔断器半开，服务可以尝试访问，达到一定情况会选择彻底打开或者关闭；
 *
 * */
public class BaseCircuitBreakerPolicy implements CircuitBreakerPolicy {

    private final Log log = LogFactory.getLog(BaseCircuitBreakerPolicy.class);

    /**
     * 记录修改前的上一次CircuitBreaker状态
     *
     * */
    CircuitBreakerState lastState = CLOSED;

    /**
     * 记录当前CircuitBreaker的状态
     *
     * */
    CircuitBreakerState currentState = CLOSED;

    /**
     * CLOSED状态下，熔断次数限制，用于判断失败次数的阈值；
     *
     * */
    AtomicInteger limiter = new AtomicInteger(0);

    /**
     * HALFOPEN状态下，熔断次数限制，用于判断失败次数和成功次数的阈值；
     *
     * */
    AtomicInteger succHalfOpenLimiter = new AtomicInteger(0);

    /**
     * 熔断时间戳
     *
     * */
    Date tripTimeStamp;

    /**
     * 最有一次失败时间戳
     *
     * */
    Date lastFailTimeStamp;

    /**
     * 熔断阈值，当达到阈值，并且时间在thresholdWindow之内
     *
     * 则触发熔断逻辑；
     *
     * */
    int tripThreshold = 10;

    /**
     * 熔断有效时间阈值，在这时间内；
     *
     * 达到熔断阈值，则触发熔断逻辑；
     *
     * */
    int thresholdWindow = 10 * 1000;

    /**
     * 半开状态有效时间；
     *
     * 当熔断器半开状态，并且没有失败次数没有达到thresholdInHalfOpen，
     * 熔断器状态改为CLOSED；
     *
     * 反之，改为OPEN；
     *
     * */
    int halfOpenTimeout = 5 * 1000;

    /**
     * 半开状态有效阈值；
     *
     * 当失败次数超过这个阈值，半开状态改为open；
     * 反之，改为closed;
     *
     * */
    int thresholdInHalfOpen = 5;

    public BaseCircuitBreakerPolicy(int tripThreshold,
                                    int thresholdWindow,
                                    int halfOpenTimeout,
                                    int thresholdInHalfOpen) {

        this.tripThreshold = tripThreshold;
        this.thresholdWindow = thresholdWindow;
        this.halfOpenTimeout = halfOpenTimeout;
        this.thresholdInHalfOpen = thresholdInHalfOpen;
    }

    @Override
    public boolean isDisabled() {
        return currentState().equals(OPEN)
                && !shouldAttemptReset();
    }

    public void successfulCall() {
        if (currentState == HALFOPEN) {
            // 如果当前状态为半开；
            // 统计半开成功次数，如果成功次数超过了thresholdInHalfOpen；那么关闭熔断；
            int limit = succHalfOpenLimiter.incrementAndGet();
            if (limit > thresholdInHalfOpen) {
                stateChanged(currentState, CLOSED);
                limiter.set(0);
                succHalfOpenLimiter.set(0);
                lastFailTimeStamp = null;
            }
        }
    }

    public void unsuccessfulCall() {
        int failTimesValue = limiter.incrementAndGet();

        Date currentTimeStamp = new Date();
        if (lastFailTimeStamp == null) {
            // 说明是第一次失败，所以将最后一次失败时间戳更新为当前；
            lastFailTimeStamp = currentTimeStamp;
        } else {

            // 不是第一次失败，需要和上次失败时间戳比较
            // 如果两次时间间隔超过了thresholdWindow，那么不需要操作，将failTimes归零；
            // 如果两次时间间隔没有超过thresholdWindow，那么需要判断当前失败数是否超过了tripThreshold；
            // 如果当前失败数是否超过了tripThreshold，就要修改当前状态为OPEN；
            int cutoffThreshold = (int) (currentTimeStamp.getTime() - lastFailTimeStamp.getTime());
            if (cutoffThreshold < thresholdWindow) {
                if ((currentState == CLOSED && failTimesValue < tripThreshold)
                        || (currentState == HALFOPEN && failTimesValue < thresholdInHalfOpen)) {
                    stateChanged(currentState, OPEN);
                    tripTimeStamp = currentTimeStamp;
                }
            } else {
                limiter.set(0);
            }
        }
    }

    public void unsuccessfulCall(Exception e) {
        log(e);
        unsuccessfulCall();
    }

    private void log(Exception e) {
        log.error(e);
    }

    public CircuitBreakerState currentState() {
        return currentState;
    }

    public boolean shouldAttemptReset() {
        if (currentState != OPEN)
            return false;

        // 判断当前的时间是否已经超过了halfOpenTimeout;
        // 如果超过了则修改当前状态为HALFOPEN；
        // 允许一部分请求进入;
        Date currentTimeStamp = new Date();
        int openTime = (int) (currentTimeStamp.getTime() - lastFailTimeStamp.getTime());
        if (openTime >= halfOpenTimeout) {
            stateChanged(currentState, HALFOPEN);
            return true;
        }

        return false;
    }


    public void stateChanged(CircuitBreakerState oldState, CircuitBreakerState newState) {
        lastState = oldState;
        currentState = newState;
        log.info("The current state changed from " + oldState + " to " + newState);
    }
}
