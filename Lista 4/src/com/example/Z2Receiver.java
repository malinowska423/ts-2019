package com.example;

import java.net.*;
import java.util.HashMap;

public class Z2Receiver {
  private static final int datagramSize = 50;
  private InetAddress localHost;
  private int destinationPort;
  private DatagramSocket socket;
  private ReceiverThread receiver;
  private HashMap<Integer, Character> received;
  private int lastPrinted;
  
  private Z2Receiver(int myPort, int destPort)
      throws Exception {
    localHost = InetAddress.getByName("127.0.0.1");
    destinationPort = destPort;
    socket = new DatagramSocket(myPort);
    receiver = new ReceiverThread();
    received = new HashMap<>();
    lastPrinted = 0;
  }
  
  public static void main(String[] args)
      throws Exception {
    Z2Receiver receiver = new Z2Receiver(Integer.parseInt(args[0]),
        Integer.parseInt(args[1]));
    receiver.receiver.start();
  }
  
  class ReceiverThread extends Thread {
    
    public void run() {
      try {
        while (true) {
          byte[] data = new byte[datagramSize];
          DatagramPacket packet =
              new DatagramPacket(data, datagramSize);
          socket.receive(packet);
          Z2Packet p = new Z2Packet(packet.getData());
          received.put(p.getIntAt(0), (char) p.data[4]);
          while (received.get(lastPrinted) != null) {
            System.out.println("R: " + lastPrinted + ": " + received.get(lastPrinted));
            lastPrinted++;
          }
          packet.setPort(destinationPort);
          socket.send(packet);
        }
      } catch (Exception e) {
        System.out.println("Z2Receiver.ReceiverThread.run: " + e);
      }
    }
    
  }
  
}
