package com.example;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

class Z2Sender {
  static final int maxPacket = 50;
  private static final int datagramSize = 50;
  private static final int sleepTime = 500;
  private InetAddress localHost;
  private int destinationPort;
  private DatagramSocket socket;
  private SenderThread sender;
  private ReceiverThread receiver;
  private HashMap<Integer, Character> toSend;
  private HashMap<Integer, Character> received;
  private int lastPrinted;
  
  private Z2Sender(int myPort, int destPort)
      throws Exception {
    localHost = InetAddress.getByName("127.0.0.1");
    destinationPort = destPort;
    socket = new DatagramSocket(myPort);
    sender = new SenderThread();
    receiver = new ReceiverThread();
    toSend = new HashMap<>();
    received = new HashMap<>();
    lastPrinted = 0;
  }
  
  public static void main(String[] args)
      throws Exception {
    Z2Sender sender = new Z2Sender(Integer.parseInt(args[0]),
        Integer.parseInt(args[1]));
    sender.sender.start();
    sender.receiver.start();
  }
  
  private void sendPacket(int key) {
//    System.out.println("sending key = [" + key + "]");
    try {
      Z2Packet p = new Z2Packet(4 + 1);
      p.setIntAt(key, 0);
      p.data[4] = (byte) (int) toSend.get(key);
      DatagramPacket packet =
          new DatagramPacket(p.data, p.data.length,
              localHost, destinationPort);
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NullPointerException ignored) {
    }
  }
  
  class SenderThread extends Thread {
    public void run() {
      int i, x;
      try {
        for (i = 0; (x = System.in.read()) >= 0; i++) {
          toSend.put(i, (char) x);
          sendPacket(i);
        }
      } catch (Exception e) {
        System.out.println("Z2Sender.SenderThread.run: " + e);
      }
    }
    
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
          saveData(p.getIntAt(0), (char) p.data[4]);
          while (received.get(lastPrinted) != null) {
            System.out.println("S: " + lastPrinted + ": " + received.get(lastPrinted));
            lastPrinted++;
          }
          if (toSend.get(lastPrinted) != null) {
            sendPacket(lastPrinted);
            sleep(sleepTime);
          }
        }
      } catch (Exception e) {
        System.out.println("Z2Sender.ReceiverThread.run: " + e);
      }
    }
    
    private synchronized void saveData(int key, char value) {
//      System.out.println("saving key = [" + key + "], value = [" + value + "]");
      received.put(key, value);
      toSend.remove(key);
    }
    
  }
  
}
