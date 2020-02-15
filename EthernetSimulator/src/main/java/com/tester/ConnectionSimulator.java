package com.tester;

import com.tester.ethernet.Station;
import com.tester.ethernet.Wire;

import java.security.SecureRandom;
import java.util.Random;

public class ConnectionSimulator extends Thread {
  private int interval;
  private final Wire wire;
  private double stationSendingProbability;
  
  ConnectionSimulator(int stationNumber, int interval, int wireLength, double stationSendingProbability) {
    if (stationNumber > wireLength || stationNumber <= 0 || interval <= 0 || stationSendingProbability < 0 || stationSendingProbability > 1.0) {
      throw new IllegalArgumentException();
    }
    this.interval = interval;
    this.wire = new Wire(wireLength);
    this.stationSendingProbability = stationSendingProbability;
    int i = 0;
    Random random = new SecureRandom();
    while (i < stationNumber) {
      if (wire.addStation(random.nextInt(wireLength), new Station())) {
        i++;
      }
    }
  }
  
  @Override
  public void run() {
    wire.printWire();
//    for (int i = 0; i < 10000; i++) {
    while (true) {
        try {
          wire.sendRandomMessages(stationSendingProbability);
          wire.printWire();
          wire.moveSignal();
          sleep(interval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }
//    wire.printStatistics();
  }
}
