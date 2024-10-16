package utils;

import interfaces.IProtocol;
import snw.snw_transport;
import tcp.tcp_transport;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    public static void newClientBehaviorGet(String ip, int port, String outFilePathNewBase, Command cmd, boolean isSNW) throws Exception {
        // "Try with resources" -- Objects created in the parens get "disposed" after control flow is done with the
        // curly-brace block.
        try (
                Socket sock = new Socket(ip, port);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        ) {
            // Send command.
            pwOutToSock.println(cmd.cmdLine);

            // Switch path.
            Path cmdArgPath = convertStringToPath(cmd.cmdArg);
            Path outFilePath = switchPathBase(cmdArgPath, outFilePathNewBase);

            // Get file.
            IProtocol transportProtocol = isSNW ? new snw_transport(ip, port) : new tcp_transport(pwOutToSock, brInFromSock);
            transportProtocol.receiveFile(outFilePath);

            // Get feedback.
            debugWriteToFile("client-behaving party about to read feedback from sock br");
            String feedback = brInFromSock.readLine();
            System.out.println(feedback);
            debugWriteToFile("client-behaving party read feedback line: " + feedback);
        }
    }

    public static void clientBehaviorPut(String serverIp, int serverPort, Command cmd) throws Exception {
        try (
                Socket sock = new Socket(serverIp, serverPort);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        ) {
            // Send command.
            pwOutToSock.println(cmd.cmdLine);

            // Get path and send file.
            Path cmdArgPath = convertStringToPath(cmd.cmdArg);
            IProtocol transportProtocol = new tcp_transport(pwOutToSock, brInFromSock);
            debugWriteToFile("clientBehaviorPut sending file");
            transportProtocol.sendFile(cmdArgPath);

            // Get feedback.
            debugWriteToFile("client-behaving party about to read feedback from sock br");
            String feedback = brInFromSock.readLine();
            System.out.println(feedback);
            debugWriteToFile("client-behaving party read feedback line: " + feedback);
        }
    }

    public static void newServerBehaviorCache(String serverIp, int cachePort, int serverPort, boolean isSNW) throws Exception {
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
                        debugWriteToFile("new arg: " + newArg);
                        var newCmd = new Command(cmd, newArg);
                        newClientBehaviorGet(serverIp, serverPort, "cache_fl", newCmd, isSNW);
                        fileOrigin = "server";
                    } else {
                        fileOrigin = "cache";
                    }

                    // Send file.
                    // TODO: Add cache IP to newServerBehaviorCache's params.
                    IProtocol transportProtocol = isSNW ? new snw_transport("localhost", cachePort) : new tcp_transport(outToSocket, inFromSocket);
                    debugWriteToFile("serverBehaviorCache sending file on port " + cachePort);
                    transportProtocol.sendFile(cmdArgPath);

                    // Send feedback.
                    debugWriteToFile("cache printing feedback to client-cache socket");
                    outToSocket.println("Server response: File delivered from " + fileOrigin + ".");
                }
            }
        }
    }

    public static void serverBehaviorServer(int serverPort, boolean isSNW) throws Exception {
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
                    // TODO: Figure out how to get server's ip address here.
                    IProtocol transportProtocol = isSNW ? new snw_transport("localhost", serverPort) : new tcp_transport(outToSocket, inFromSocket);
                    switch (cmd.cmdType) {
                        case "get":
                            File f = new File(cmd.cmdArg);
                            if (!f.exists()) {
                                debugWriteToFile("server didn't find the file");
                                outToSocket.println("Server response: File not found.");
                            } else {
                                debugWriteToFile("serverBehaviorServer sending file on port " + serverPort);
                                transportProtocol.sendFile(cmdArgPath);
                                debugWriteToFile("server printing blank feedback to server-cache socket because cache prints the actual feedback");
                                outToSocket.println("");
                            }
                            break;
                        case "put":
                            var newArg = switchPathBase(cmdArgPath, "server_fl").toString();
                            debugWriteToFile("server created new cmd arg: " + newArg);
                            var newCmdArgPath = convertStringToPath(newArg);
                            transportProtocol.receiveFile(newCmdArgPath);
                            debugWriteToFile("server printing feedback to server-cache socket");
                            outToSocket.println("File successfully uploaded.");
                            break;
                        default:
                            throw new Exception("Impossible!");
                    }
                }
            }
        }
    }

    public static void validateProtocolArg(String protocolArg) {
        if (!(protocolArg.equalsIgnoreCase("tcp") || protocolArg.equalsIgnoreCase("snw"))) {
            throw new IllegalArgumentException("Invalid protocol arg");
        }
    }

    public static String getStringOfFile(String filePath) throws Exception {
        // https://stackoverflow.com/a/4716623/16458003
        int i = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                i++;
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            if (i == 0) {
                debugWriteToFile("Something went wrong with `getStringOfFile`");
            }
            return sb.toString();
        }
    }

    public static Command getCommand(int cachePort) throws Exception {

        try (
                ServerSocket listenSocket = new ServerSocket(cachePort)
        ) {
            try (
                    Socket connectionSocket = listenSocket.accept();
                    BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))
            ) {
                debugWriteToFile("about to read from socket");
                String cmdLine = inFromSocket.readLine();
                debugWriteToFile("cache received line from client-cache socket:\n" + cmdLine);
                return new Command(cmdLine);
            }
        }
    }
}
