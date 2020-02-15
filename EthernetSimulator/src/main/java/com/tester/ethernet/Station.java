package com.tester.ethernet;

import java.security.SecureRandom;

public class Station {
    private Integer position;
    private Message message;
    private boolean isSending;
    private boolean isConflicted;
    private int sendingTime;
    private int waitingTime;
    private int conflictCounter;
    private int resolvedConflicts;
    private int allConflicts;

    public Station() {
        this.position = null;
        this.isSending = false;
        this.isConflicted = false;
        this.sendingTime = 0;
        this.waitingTime = 0;
        this.conflictCounter = 0;
        this.resolvedConflicts = 1;
        this.allConflicts = 0;
    }

    void resolveConflict() {
        this.resolvedConflicts++;
        this.isConflicted = false;
        this.conflictCounter = 0;
        this.waitingTime = 0;
        this.isSending = true;
        System.err.println("\tStation " + position + " resolved conflict");
    }

    void setPosition(Integer position) {
        this.position = position;
        this.message = new Message(position);
    }
    
    public double getAvgResolveTime() {
        return allConflicts/resolvedConflicts;
    }
    
    Integer getPosition() {
        return position;
    }
    
    Message getMessage() {
        return message;
    }
    
    boolean isSending() {
        return isSending;
    }

    boolean isConflicted() {
        return isConflicted;
    }

    int getSendingTime() {
        return sendingTime;
    }

    int getWaitingTime() {
        return waitingTime;
    }

    void setSending(boolean sending) {
        isSending = sending;
    }

    void setConflicted(int wireLength) {
        isConflicted = true;
        setSending(false);
        conflictCounter++;
        allConflicts++;
        int pow;
        if (conflictCounter < 16) {
            if (conflictCounter > 10) {
                pow = 10;
            } else {
                pow = conflictCounter;
            }
            waitingTime = 2*wireLength*((new SecureRandom()).nextInt((int) Math.pow(2, pow))+1);
        } else {
            conflictCounter = 0;
            waitingTime = 0;
        }
//        System.out.println("Conflict no " + conflictCounter + "! st " + position + " time " + waitingTime);
        System.err.println("\tStation " + position + " is conflicted for the " + conflictCounter + " time. Waits: " + waitingTime);
    }

    void setSendingTime(int sendingTime) {
        this.sendingTime = sendingTime;
    }

    void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
