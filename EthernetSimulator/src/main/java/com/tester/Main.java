package com.tester;

public class Main {

    public static void main(String[] args) {
        ConnectionSimulator simulator =
            new ConnectionSimulator(2,70,20, 0.5);
        simulator.run();
    }
}
