package com.tester.ethernet;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class Wire {
    private ArrayList<Station> stations;
    private WireElement [] wireArray;

    public Wire(int wireLength) {
        this.wireArray = new WireElement[wireLength];
        for (int i = 0; i < wireLength; i++) {
            wireArray[i] = new WireElement();
        }
    }

    public boolean addStation(int position, Station station) {
        if (stations == null) {
            stations = new ArrayList<>();
        }
        for (Station connectedStation :
                stations) {
            if (connectedStation.getPosition() == position) {
                return false;
            }
        }
        stations.add(station);
        station.setPosition(position);
        return true;
    }

    private boolean isSilence(int position) {
        return !(wireArray[position] != null && wireArray[position].getMessages().size() > 0) ;
    }

    private int getWireLength() {
        return wireArray.length;
    }

    private boolean hasConflictOccurred(int position) {
        return (wireArray[position] != null && wireArray[position].getMessages().size() > 1);
    }
    
    
    public void moveSignal() {
        WireElement [] tempWire = new WireElement[getWireLength()];
        for (int i = 0; i < tempWire.length; i++) {
            tempWire[i] = new WireElement();
        }
        for (int i = 1; i < getWireLength() - 1; i++) {
            if (wireArray[i] != null && !wireArray[i].getMessages().isEmpty()) {
                for (Message message :
                    wireArray[i].getMessages()) {
                    if (message.getDirection() == Message.Direction.RIGHT) {
                        tempWire[i+1].addMessage(message);
                    } else if (message.getDirection() == Message.Direction.LEFT) {
                        tempWire[i-1].addMessage(message);
                    } else if (message.getDirection() == Message.Direction.BOTH) {
                        tempWire[i-1].addMessage(new Message(message.getCode(), Message.Direction.LEFT));
                        tempWire[i+1].addMessage(new Message(message.getCode(), Message.Direction.RIGHT));
                    }
                }
            }
        }
        wireArray = tempWire;
        sendSignals();
    }
    
    private void sendSignals() {
        for (Station station :
            stations) {
            if (station.isSending()) {
                if ( station.getSendingTime() < 2 * getWireLength()) {
                    wireArray[station.getPosition()].addMessage(station.getMessage());
                    station.setSendingTime(station.getSendingTime()+1);
                }
                if (hasConflictOccurred(station.getPosition())) {
                    station.setConflicted(getWireLength());
                }
            }
            if (station.isConflicted()) {
                if (station.getWaitingTime() > 0) {
                    station.setWaitingTime(station.getWaitingTime() - 1);
                } else {
                    station.setSending(true);
                }
            }
            if (station.isSending() && station.getSendingTime() >= 2 * getWireLength()) {
                if (station.isConflicted()) {
                    station.resolveConflict();
                } else {
                    station.setSending(false);
                }
            }
        }
    }
    
    public void printWire() {
        StringBuilder wireString = new StringBuilder();
        for (int i = 0; i < getWireLength(); i++) {
            if (wireArray[i] != null && !wireArray[i].getMessages().isEmpty()) {
                if (wireArray[i].getMessages().size() == 1) {
                    int code = wireArray[i].getMessages().get(0).getCode();
                    wireString.append(code < 10 ? " " + code : code ).append(" ");
                } else {
                    wireString.append(" # ");
                }
            } else {
                wireString.append("-- ");
            }
        }
        System.out.println(wireString);
    }
    
    public void printStatistics() {
        double sumAll = 0;
        for (Station station :
            stations) {
            System.out.print(station.getPosition() + " : " + station.getAvgResolveTime() + " | ");
            sumAll += station.getAvgResolveTime();
        }
        System.out.println("all : " + sumAll/stations.size());
    }
    
    public void sendRandomMessages(double sendingProbability) {
        Random rand = new SecureRandom();
        for (Station station :
            stations) {
            if (rand.nextDouble() <= sendingProbability && !station.isConflicted()) {
                sendMessageFrom(station);
            }
        }
        
    }
    
    private void sendMessageFrom(Station station) {
        if (!station.isSending() && isSilence(station.getPosition())) {
//            System.out.println("Sending message from " + station.getPosition());
            System.err.println("\t\tStation " + station.getPosition() + " is sending random message");
            station.setSending(true);
            wireArray[station.getPosition()].addMessage(station.getMessage());
            station.setSendingTime(1);
        }
    }
    
    
    class WireElement {
        private ArrayList<Message> messages;
    
        WireElement() {
            messages = new ArrayList<>();
        }
        
        void addMessage(Message message) {
            messages.add(message);
        }
    
        ArrayList<Message> getMessages() {
            return messages;
        }
    }
}
