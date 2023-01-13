package org.daeyoung;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Response {

    static Logger logger = Logger.getLogger("UReceiver");
    final String type;

    public Response(String type) {
        this.type = type;
    }

    byte[] prepareResponse() throws IOException {
        if (type.equals("AUTO")) {
            return "(Response) OK\n".getBytes();
        } else if (type.equals("INPUT")) {
            logger.info("INPUT >>> ");
            StringBuilder rb = new StringBuilder();
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = stdin.readLine()) != null) {
                if (input.length() == 0) {
                    break;
                }
                rb.append(input);
            }
            String plain = rb.toString()
                    .replaceAll("[\\s\\n]", "")
                    .replaceAll("\\(byte\\)", "")
                    .replaceAll("0x", "")
                    .replaceAll(",", "")
                    .trim();

            if (plain.length() % 2 > 0 || !plain.matches("[\\da-f]+")) {
                logger.log(Level.WARNING, "It's not a Byte Array");
                return "(Response) OK\n".getBytes();
            } else {
                byte[] response = new byte[plain.length() / 2];
                for (int i = 0; i < plain.length(); i += 2) {
                    response[i/2] = (byte) (
                            ( Character.digit(plain.charAt(i),16) << 4 ) +
                                    ( Character.digit(plain.charAt(i+1),16) ));
                }
                return response;
            }
        }

        return null;
    }
}
