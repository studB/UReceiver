package org.daeyoung;

import com.google.common.primitives.UnsignedBytes;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver {

    static Logger logger = Logger.getLogger("UReceiver");

    private static DatagramSocket socket;
    private static boolean running = false;
    private static byte[] buf;
    private final static String ROOT_PATH = System.getProperty("user.dir");
    private final static String GLOBAL_CONFIG="global.properties";

    public static void main(String[] args) throws IOException {
        String globalConfigPath = String.format("%s/%s", ROOT_PATH,GLOBAL_CONFIG);
        Properties globalConfigs = new Properties();
        globalConfigs.load(new FileInputStream(globalConfigPath));

        final int port = Integer.parseInt(globalConfigs.getProperty("PORT"));

        try {
            socket = new DatagramSocket(port);
            running = true;
            logger.log(Level.INFO, String.format("RUN U-RECEIVER : (port) %d", port));
        } catch (SocketException e) { logger.log(Level.SEVERE,"BAD SERVER RUNNING !"); e.printStackTrace(); }

        while (running) {
            buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                logger.log(Level.INFO, "========== [GET PACKET] ;");
                logger.log(Level.INFO,
                        "\n"
                        + String.format("(Socket Address) %s", packet.getSocketAddress()) + "\n"
                        + String.format("(Length) %d , (Offset) %d", packet.getLength(), packet.getOffset()) + "\n"
                        + decomposePacket(packet.getLength(), ByteBuffer.wrap(packet.getData()))
                );

                byte[] responseMsgBuf = "(Response) OK\n".getBytes();
                packet = new DatagramPacket(responseMsgBuf, responseMsgBuf.length, packet.getAddress(), packet.getPort());
                socket.send(packet);
            } catch (IOException e) { logger.log(Level.SEVERE, "BAD COMMUNICATION !"); e.printStackTrace(); }
            finally { logger.log(Level.INFO, "========== [END COMMUNICATION] ;"); }
        }
    }

    public static String decomposePacket(int length, ByteBuffer byteBuffer) {
        int idx = 0;
        ArrayList<String> byteArrayList = new ArrayList<>();
        while (length > idx) {
            byteArrayList.add( UnsignedBytes.toString(byteBuffer.get()));
            idx++;
        }
        return String.format("ByteBuffer[%d] { %s }", length, String.join(", ", byteArrayList));
    }
}