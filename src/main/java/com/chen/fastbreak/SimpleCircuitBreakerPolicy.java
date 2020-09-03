package com.chen.fastbreak;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chen.fastbreak.CircuitBreakerState.*;

public class SimpleCircuitBreakerPolicy implements CircuitBreakerPolicy {

    private CircuitBreakerState currentState = CLOSED;
    private CircuitBreakerState lastState    = CLOSED;

    private Deque<Integer> deque = new ConcurrentLinkedDeque<>();

    private int tripThreshold = 10;

    private int thresholdWindow = 50;

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
//        log.info("The current state changed from " + oldState + " to " + newState);
        System.out.println("The current state changed from " + oldState + " to " + newState);
    }
}
