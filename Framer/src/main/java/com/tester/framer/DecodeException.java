package com.tester.framer;

class DecodeException extends Exception {
    DecodeException(String message) {
        super(message);
    }

    void show() {
        System.err.println("DecodeException:\n\t" + getMessage());
    }
}
