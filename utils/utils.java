package utils;

import java.io.BufferedReader;
import java.io.IOException;

public class utils {
    public static int tryParsePort(String portStr) {
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad port argument");
        }
    }

    public static void waitTilReady(BufferedReader br) throws IOException {
        while (!br.ready()) {
            System.out.println("waiting for BufferedReader to be ready");
        }
    }
}
