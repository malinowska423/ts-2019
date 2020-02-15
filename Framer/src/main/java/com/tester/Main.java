package com.tester;

import com.tester.framer.FileDecoder;

public class Main {
    public static void main(String[] args) {
        FileDecoder decoder = new FileDecoder();
        decoder.encodeFile("input.txt", "output.txt");
        decoder.decodeFile("output.txt", "result.txt");
    }
}
