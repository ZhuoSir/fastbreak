package com.chen.fastbreak;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.chen.fastbreak.CircuitBreakerState.CLOSED;
import static com.chen.fastbreak.CircuitBreakerState.OPEN;

/**
 * 简易式熔断器代理，
 *
 *
 * */
public class SimpleCircuitBreakerPolicy implements CircuitBreakerPolicy {

    private CircuitBreakerState currentState = CLOSED;
    private CircuitBreakerState lastState    = CLOSED;

    private Deque<Integer> deque = new ConcurrentLinkedDeque<>();

    /**
     * 熔断阈值，当在thresholdWindow频次内，
     * 达到这个阈值，则触发熔断
     *
     * */
    private int tripThreshold = 10;

    /**
     * 熔断范围阈值，在这个频次范围内，
     * 达到tripThreshold，则触发熔断；
     *
     * */
    private int thresholdWindow = 50;

    /**
     * 熔断超时时间，当熔断器为OPEN状态，达到这个时间则改为CLOSED；
     *
     * */
    private int openTimeOut = 10 * 6000;

    private Date lastOpenTimeStamp = null;

    public SimpleCircuitBreakerPolicy(int tripThreshold, int thresholdWindow, int openTimeOut) {
        this.tripThreshold = tripThreshold;
        this.thresholdWindow = thresholdWindow;
        this.openTimeOut = openTimeOut;
    }

    @Override
    public boolean isDisabled() {
        return currentState().equals(OPEN)
                && !shouldAttemptReset();
    }

    @Override
    public void successfulCall() {
        deque.push(1);

    }

    @Override
    public void unsuccessfulCall() {
        deque.push(0);

        boolean isOpen = false;
        if (deque.size() > thresholdWindow) {
            int failCount = 0;
            Iterator<Integer> iterator = deque.iterator();
            for (int i = 0; i < thresholdWindow; i++) {
                Integer succOrFail = iterator.next();
                if (succOrFail != null && succOrFail == 0) {
                    failCount++;
                }
                if (failCount > tripThreshold) {
                    isOpen = true;
                    break;
                }
            }

            if (isOpen || failCount > tripThreshold) {
                stateChanged(currentState, OPEN);
                lastOpenTimeStamp = new Date();
                deque.clear();
            } else {
                deque.pollLast();
            }
        }
    }

    @Override
    public void unsuccessfulCall(Exception e) {
        log(e);
        unsuccessfulCall();
    }

    private void log(Exception e) {

    }

    @Override
    public CircuitBreakerState currentState() {
        return currentState;
    }

    @Override
    public boolean shouldAttemptReset() {

        Date currentTimeStamp = new Date();
        int cutoffTime = (int) (currentTimeStamp.getTime() - lastOpenTimeStamp.getTime());
        if (cutoffTime >= openTimeOut) {
            stateChanged(currentState, CLOSED);
            return true;
        }
        return false;
    }

    public void stateChanged(CircuitBreakerState oldState, CircuitBreakerState newState) {
        lastState = oldState;
        currentState = newState;
    }
}
