package com.chen.fastbreak.breaker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chen.fastbreak.breaker.CircuitBreakerState.*;

public class BaseCircuitBreakerPolicy implements CircuitBreakerPolicy {

    private final Log log = LogFactory.getLog(BaseCircuitBreakerPolicy.class);

    private CircuitBreakerState lastState = CLOSED;

    private CircuitBreakerState currentState = CLOSED;

    private AtomicInteger failTimes = new AtomicInteger(0);

    private int tripThreshold;

    private int thresholdWindow;

    private int halfOpenTimeout;

    private Date tripTimeStamp;

    private Date lastFailTimeStamp;

    public BaseCircuitBreakerPolicy(int tripThreshold,
                                    int thresholdWindow,
                                    int halfOpenTimeOut) {

        if( tripThreshold <= 0 ) {
            tripThreshold = 10;
        }

        if( halfOpenTimeOut <= 0 ) {
            halfOpenTimeOut = 10;
        }

        if (thresholdWindow <= 0) {
            thresholdWindow = 10;
        }

        this.tripThreshold = tripThreshold;
        this.halfOpenTimeout = halfOpenTimeOut;
        this.thresholdWindow = thresholdWindow;
    }

    @Override
    public boolean isDisabled() {
        return currentState().equals(CircuitBreakerState.OPEN)
                && !shouldAttemptReset();
    }

    public void successfulCall() {
        if (currentState != CLOSED) {
            stateChanged(currentState, CLOSED);
            failTimes.set(0);
            lastFailTimeStamp = null;
        }
    }

    public void unsuccessfulCall() {
//        failures.push(new Date());
        int failTimesValue = failTimes.incrementAndGet();

        Date currentTimeStamp = new Date();
        if (lastFailTimeStamp == null) {
            // 说明是第一次失败，所以将最后一次失败时间戳更新为当前；
            lastFailTimeStamp = currentTimeStamp;
        } else {

            if (currentState == CLOSED) {
                // 不是第一次失败，需要和上次失败时间戳比较
                // 如果两次时间间隔超过了thresholdWindow，那么不需要操作，将failTimes归零；
                // 如果两次时间间隔没有超过thresholdWindow，那么需要判断当前失败数是否超过了tripThreshold；
                // 如果当前失败数是否超过了tripThreshold，就要修改当前状态为OPEN；
                int cutoffThreshold = (int) (currentTimeStamp.getTime() - lastFailTimeStamp.getTime()) / 1000;
                if (cutoffThreshold < thresholdWindow) {
                    if (failTimesValue < tripThreshold
                            && (currentState == CLOSED || currentState == HALFOPEN)) {
                        stateChanged(currentState, OPEN);
                        tripTimeStamp = currentTimeStamp;
                    }
                } else {
                    failTimes.set(0);
                }
            }

            else if (currentState == HALFOPEN) {

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
//        log.info("The current state changed from " + oldState + " to " + newState);
        System.out.println("The current state changed from " + oldState + " to " + newState);
    }
}
