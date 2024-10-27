package utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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

    public static void writeToFileNoNewLine(String s, Path p) {
        try {
            Files.writeString(p, s, CREATE, APPEND);
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
        Path ret = FileSystems.getDefault().getPath(newBase, sp.toString());
        debugWriteToFile("path with switched base: " + ret);
        return ret;
    }

    public static long getFile1LineCount() {
        try (BufferedReader brInFromFile = Files.newBufferedReader(FileSystems.getDefault().getPath("cache_fl/File1.txt"), StandardCharsets.UTF_8)) {
            return brInFromFile.lines().count();
        } catch (Exception e) {
            System.out.println("h");
        }
        return -1;
    }

    public static Path convertStringToPath(String s) {
        return FileSystems.getDefault().getPath(s);
    }

    public static void sanityCheck() throws Exception {
        int i = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("cache_fl/File1.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            debugWriteToFile("Conducting sanity check...");
            while (line != null) {
                i++;
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            debugWriteToFile(everything);
            if (i == 0) {
                debugWriteToFile("Sorry, you're insane...");
            }
        }
    }

    public static void fancySanityCheck() throws Exception {
        debugWriteToFile("Conducting fancy sanity check...");
        try (var stream = Files.lines(convertStringToPath("cache_fl/File1.txt"))) {
            stream.forEach(l -> debugWriteToFile(l));
        }
    }

    private void newServerBehaviorCache(String ip, int cachePort, int serverPort, String outFilePathNewBase) throws Exception {
        try (
                ServerSocket listenSocket = new ServerSocket(cachePort)
        ) {
            while (true) {
                // Accept connection and setup I/O streams.
                Socket connectionSocket = listenSocket.accept();
                PrintWriter outToSocket = new PrintWriter(connectionSocket.getOutputStream(), true);
                BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                debugWriteToFile("cache about to read from cache-client socket");
                String cmdArg = inFromSocket.readLine();
                debugWriteToFile("cache received line from client-cache socket:\n" + cmdArg);
                Path cmdArgPath = FileSystems.getDefault().getPath(cmdArg);
                File f = new File(cmdArg);
                if (!f.exists()) {
                    debugWriteToFile("cache getting file from server");
//                    clientBehaviorGet(switchPathBase(cmdArgPath, "server_fl").toString());
                    outToSocket.println("Server response: File delivered from server.");
                } else {
                    outToSocket.println("Server response: File delivered from cache.");
                }

                debugWriteToFile("cache printing announcement to client-cache socket");

                try (
                        Stream<String> stream = Files.lines(cmdArgPath)
                ) {
                    stream.forEach(outToSocket::println);
                    outToSocket.println("file over");
                }
//                try (
//                        BufferedReader brInFromFile = Files.newBufferedReader(cmdArgPath, StandardCharsets.UTF_8)
//                ) {
//                    String ln;
//                    debugWriteToFile("File1 line count (read 2): " + brInFromFile.lines().count());
//                    fancySanityCheck();
//
//                    while ((ln = brInFromFile.readLine()) != null) {
//                        debugWriteToFile("cache printing file line to client-cache socket");
//                        outToSocket.println(ln);
//                    }
//                    outToSocket.println("file over");
//                }
            }
        }
    }

    public void clientBehaviorGet(String ip, int port, String outFilePathNewBase, String cmdArg) throws IOException {
        try (
                Socket sock = new Socket(ip, port);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        ) {
            pwOutToSock.println(cmdArg);

            Path cmdArgPath = FileSystems.getDefault().getPath(cmdArg);
            Path outFilePath = switchPathBase(cmdArgPath, outFilePathNewBase);
            String ln;
            int i = 0;
            debugWriteToFile("client about to read from sock br " + i);
            ln = brInFromSock.readLine();
            System.out.println(ln);
            debugWriteToFile("client receives line: " + ln);
            while (true) {
//                waitTilReady(brInFromSock, "client behavior get");
                debugWriteToFile("client about to read from sock br " + (++i));
                ln = brInFromSock.readLine();
                debugWriteToFile("client receives line: " + ln);
                if (ln.equals("file over")) {
                    debugWriteToFile("file is over...");
                    break;
                }
                writeToFile(ln, outFilePath);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O issue with host: " + ip);
            System.exit(1);
        }
    }
}
