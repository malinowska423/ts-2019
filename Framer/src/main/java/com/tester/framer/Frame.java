package com.tester.framer;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

class Frame {
    private String frameContent;
    private String marker;
    private char escapeCode;

    Frame() {
        this(null);
    }

    Frame(String frameContent) {
        this.frameContent = frameContent;
        this.marker = "01111110";
        this.escapeCode = '0';
    }

    String getFrameContent() {
        return frameContent;
    }

    void fillFrame (String message) {
        frameContent = marker + codeMessage(message + getCRC(getBytesArray(message))) + marker;
    }

    String decodeFrame() throws DecodeException {
        if (frameContent != null) {
            StringBuilder output = new StringBuilder();
            int size = frameContent.length();
            if (!frameContent.substring(0,8).equals(marker)
                    && !frameContent.substring(size-9, size).equals(marker)) {
                throw new DecodeException("Bad markers");
            }
            int counter = 0;
            for (int i = 8; i < frameContent.length()-8; i++) {
                output.append(frameContent.charAt(i));
                if (frameContent.charAt(i) == '1') {
                    counter++;
                }
                if (counter == 5) {
                    if (frameContent.charAt(i+1) != escapeCode) {
                        throw new DecodeException("Wrong escape code");
                    }
                    counter = 0;
                    i++;
                    continue;
                }
                if (frameContent.charAt(i) == '0') {
                    counter = 0;
                }
            }
            String message = output.toString().substring(0,output.toString().length()-32);
            String crc = output.toString().substring(output.toString().length()-32);
            if (!crc.equals(getCRC(getBytesArray(message)))) {
                throw new DecodeException("Wrong CRC code");
            } else {
                return message;
            }
        } else {
            throw new DecodeException("No frame content");
        }
    }

    private String codeMessage(String message) {
        StringBuilder output = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < message.length(); i++) {
            output.append(message.charAt(i));
            if (message.charAt(i) == '1') {
                counter++;
            }
            if (counter == 5) {
                output.append(escapeCode);
                counter = 0;
            }
            if (message.charAt(i) == '0') {
                counter = 0;
            }
        }
        return output.toString();
    }

    private String getCRC(byte[] bytesArray) {
        Checksum checksum = new CRC32();
        checksum.update(bytesArray,0, bytesArray.length);
        return longToBinary(checksum.getValue());
    }

    private byte [] getBytesArray(String message) {
        byte [] bytes = new byte[((int) Math.ceil(message.length() / 8.0))];
        byte b;
        int i = 0, k = 0;
        while( i < message.length()) {
            b = 0;
            for (int j = 0; j < 8 ; j++, i++) {
                b *= 2;
                if (i < message.length()) {
                    b += message.charAt(i) - '0';
                }
            }
            bytes[k] = b;
            k++;
        }
        return bytes;
    }

    private String longToBinary(long value) {
        StringBuilder tmp= new StringBuilder();
        while(value > 0) {
            tmp.append(value % 2);
            value /= 2;
        }
        while (tmp.toString().length() < 32) {
            tmp.append("0");
        }
        return tmp.reverse().toString();
    }
}
