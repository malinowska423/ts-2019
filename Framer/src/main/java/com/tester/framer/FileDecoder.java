package com.tester.framer;

import java.io.*;

public class FileDecoder {
    private enum Action {DECODE, ENCODE}

    public void encodeFile(String inputFileName, String outputFileName) {
        handleFile(Action.ENCODE, inputFileName, outputFileName);
    }

    public void decodeFile(String inputFileName, String outputFileName) {
        handleFile(Action.DECODE, inputFileName, outputFileName);
    }

    private void handleFile(Action action, String inputFileName, String outputFileName){
        try {
            String path = "src/main/resources/";
            BufferedReader reader = new BufferedReader(new FileReader(path + inputFileName));
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + outputFileName));
            String currentLine = reader.readLine();
            Frame frame;
            if (action == Action.ENCODE) {
                while (currentLine != null) {
                    frame = new Frame();
                    frame.fillFrame(currentLine);
                    writer.write(frame.getFrameContent() + "\n");
                    currentLine = reader.readLine();
                }
            } else if (action == Action.DECODE) {
                while (currentLine != null) {
                    frame = new Frame(currentLine);
                    try {
                        writer.write(frame.decodeFrame() + "\n");
                    } catch (DecodeException e) {
                        e.show();
                        writer.write(e.getMessage() + "\n");
                    }
                    currentLine = reader.readLine();
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.err.println("File " + inputFileName + " not found");
        }
    }
}
