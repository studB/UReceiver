package org.daeyoung;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Decoder {

    public static String decomposePacket(int length, ByteBuffer byteBuffer) {
        int idx = 0;
        ArrayList<String> byteArrayList = new ArrayList<>();
        if (length > 8)
            byteArrayList.add("\t\n");
        while (length > idx) {
            byteArrayList.add( String.format("(byte)0x%02x", byteBuffer.get()));
            idx++;
            if (idx % 8 < 1 && length != idx)
                byteArrayList.add("\t\n");
        }
        return String.format("ByteBuffer[%d] { %s }", length, String.join(" ", byteArrayList));
    }
}
