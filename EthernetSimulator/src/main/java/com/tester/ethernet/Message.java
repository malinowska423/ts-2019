package com.tester.ethernet;

public class Message {
  enum Direction {
    LEFT, RIGHT, BOTH
  }
  private int code;
  private Direction direction;
  
  public Message(int code) {
    this.code = code;
    this.direction = Direction.BOTH;
  }
  
  public Message(int code, Direction direction) {
    this.code = code;
    this.direction = direction;
  }
  
  public int getCode() {
    return code;
  }
  
  public Direction getDirection() {
    return direction;
  }
  
  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}
