package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class utils {
    public static int tryParsePort(String portStr) {
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad port argument");
        }
    }

    public static void waitTilReady(BufferedReader br, String comment) throws IOException {
        while (!br.ready()) {
            debugWriteToFile("waiting for BufferedReader to be ready; " + comment);
        }
    }

    public static void debugWriteToFile(String s) {
        try {
            Files.writeString(
                    Path.of(System.getProperty("java.io.tmpdir"), "networking_debug_output.txt"),
                    s + "\n\n",
                    CREATE,
                    APPEND);
        } catch (IOException e) {
            System.out.println("Logging error -- exiting");
            System.exit(-1);
        }
    }

    public static void writeToFile(String s, Path p) {
        try {
            Files.writeString(p, s + "\n", CREATE, APPEND);
        } catch (IOException e) {
            System.out.println("Logging error -- exiting");
            System.exit(-1);
        }
    }

    public static Path switchPathBase(Path orig, String newBase) {
        int nc = orig.getNameCount();
        Path sp = orig.subpath(nc - 1, nc);
        debugWriteToFile("orig path: " + orig);
        debugWriteToFile("sub-path: " + sp);
        return FileSystems.getDefault().getPath(newBase, sp.toString());
    }
}
