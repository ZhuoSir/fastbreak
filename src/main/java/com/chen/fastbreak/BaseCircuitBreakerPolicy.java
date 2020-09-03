package com.chen.fastbreak;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chen.fastbreak.CircuitBreakerState.*;

public class BaseCircuitBreakerPolicy implements CircuitBreakerPolicy {

    private final Log log = LogFactory.getLog(BaseCircuitBreakerPolicy.class);

    CircuitBreakerState lastState = CLOSED;

    CircuitBreakerState currentState = CLOSED;

    AtomicInteger limiter = new AtomicInteger(0);
    AtomicInteger succHalfOpenLimiter = new AtomicInteger(0);

    Date tripTimeStamp;
    Date lastFailTimeStamp;

    int tripThreshold = 10;
    int thresholdWindow = 10;
    int halfOpenTimeout = 5;
    int thresholdInHalfOpen = 5;

    public BaseCircuitBreakerPolicy() {
    }

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
            int cutoffThreshold = (int) (currentTimeStamp.getTime() - lastFailTimeStamp.getTime()) / 1000;
            if (cutoffThreshold < thresholdWindow) {
                if ((currentState == CLOSED && failTimesValue < tripThreshold)
                        || (currentState == HALFOPEN && failTimesValue < thresholdInHalfOpen)) {
                    stateChanged(currentState, OPEN);
                    tripTimeStamp = currentTimeStamp;
                    if (currentState == HALFOPEN) {

                    }
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
        int openTime = (int) (currentTimeStamp.getTime() - lastFailTimeStamp.getTime()) / 1000;
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
