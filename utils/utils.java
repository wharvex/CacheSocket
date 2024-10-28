package utils;

import interfaces.IProtocol;
import tcp.tcp_transport;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        // https://stackoverflow.com/a/4716623/16458003
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

    public static void newClientBehaviorGet(String ip, int port, String outFilePathNewBase, Command cmd) throws Exception {
        // "Try with resources" -- Objects created in the parens get "disposed" after control flow is done with the
        // curly-brace block.
        try (
                Socket sock = new Socket(ip, port);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        ) {
            // Send command.
            pwOutToSock.println(cmd.cmdLine);

            Path cmdArgPath = convertStringToPath(cmd.cmdArg);
            Path outFilePath = switchPathBase(cmdArgPath, outFilePathNewBase);
            String ln;
            int i = 0;

            // Get file.
            while (true) {
                debugWriteToFile("client-behaving party about to read from sock br " + (++i));
                ln = brInFromSock.readLine();
                debugWriteToFile("client-behaving party receives line: " + ln);
                if (ln.equals("file over")) {
                    debugWriteToFile("file is over...");
                    break;
                }
                writeToFile(ln, outFilePath);
            }

            // Get feedback.
            debugWriteToFile("client-behaving party about to read feedback from sock br");
            ln = brInFromSock.readLine();
            System.out.println(ln);
            debugWriteToFile("client-behaving party receives feedback line: " + ln);
        }
    }

    public static void newServerBehaviorCache(String serverIp, int cachePort, int serverPort) throws Exception {
        try (
                ServerSocket listenSocket = new ServerSocket(cachePort)
        ) {
            while (true) {
                try (
                        Socket connectionSocket = listenSocket.accept();
                        PrintWriter outToSocket = new PrintWriter(connectionSocket.getOutputStream(), true);
                        BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))
                ) {
                    // Get command line, create Command object, create Path from cmdArg.
                    debugWriteToFile("cache about to read from cache-client socket");
                    String cmdLine = inFromSocket.readLine();
                    debugWriteToFile("cache received line from client-cache socket:\n" + cmdLine);
                    Command cmd = new Command(cmdLine);
                    Path cmdArgPath = convertStringToPath(cmd.cmdArg);

                    // If the cache doesn't have the file, look in the server.
                    File f = new File(cmd.cmdArg);
                    String fileOrigin;
                    if (!f.exists()) {
                        debugWriteToFile("cache getting file from server");
                        var newArg = switchPathBase(cmdArgPath, "server_fl").toString();
                        var newCmd = new Command(cmd, newArg);
                        newClientBehaviorGet(serverIp, serverPort, "cache_fl", newCmd);
                        debugWriteToFile("cache printing feedback to client-cache socket");
                        fileOrigin = "server";
                    } else {
                        debugWriteToFile("cache printing feedback to client-cache socket");
                        fileOrigin = "cache";
                    }

                    IProtocol transportProtocol = new tcp_transport(outToSocket);
//
//                    try (
//                            Stream<String> stream = Files.lines(cmdArgPath)
//                    ) {
//                        stream.forEach(outToSocket::println);
//                        outToSocket.println("file over");
//                    }
                    transportProtocol.sendFile(cmdArgPath);
                    outToSocket.println("Server response: File delivered from " + fileOrigin + ".");
                }
            }
        }
    }

    public static void serverBehaviorServer(int serverPort) throws Exception {
        try (
                ServerSocket listenSocket = new ServerSocket(serverPort)
        ) {
            while (true) {
                try (
                        Socket connectionSocket = listenSocket.accept();
                        PrintWriter outToSocket = new PrintWriter(connectionSocket.getOutputStream(), true);
                        BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))
                ) {
                    // Get command line, create Command object, create Path from cmdArg.
                    debugWriteToFile("server about to read command line from socket");
                    String cmdLine = inFromSocket.readLine();
                    debugWriteToFile("server read command line from socket:\n" + cmdLine);
                    Command cmd = new Command(cmdLine);
                    Path cmdArgPath = convertStringToPath(cmd.cmdArg);

                    // Create transport protocol and proceed according to command type.
                    IProtocol transportProtocol = new tcp_transport(outToSocket);
                    switch (cmd.cmdType) {
                        case "get":
                            File f = new File(cmd.cmdArg);
                            if (!f.exists()) {
                                debugWriteToFile("server didn't find the file");
                                outToSocket.println("Server response: File not found.");
                            } else {
                                debugWriteToFile("cache printing feedback to client-cache socket");
                                outToSocket.println("Server response: File delivered from cache.");
                                transportProtocol.sendFile(cmdArgPath);
                            }
                            break;
                        case "put":
                            break;
                        default:
                            throw new Exception("Impossible!");
                    }
                    try (
                            Stream<String> stream = Files.lines(cmdArgPath)
                    ) {
                        stream.forEach(outToSocket::println);
                        outToSocket.println("file over");
                    }
                }
            }
        }
    }
}
