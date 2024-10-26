package tcp;

import interfaces.IProtocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static utils.utils.*;

public class tcp_transport implements IProtocol {
    int serverPort;
    int cachePort;
    String serverIP;
    String cacheIP;
    boolean isCache;

    // Client constructor.
    public tcp_transport(int serverPort, int cachePort, String serverIP, String cacheIP) {
        this.serverPort = serverPort;
        this.cachePort = cachePort;
        this.serverIP = serverIP;
        this.cacheIP = cacheIP;
        isCache = false;
    }

    // Cache constructor.
    public tcp_transport(int serverPort, int cachePort, String serverIP) {
        this.serverPort = serverPort;
        this.cachePort = cachePort;
        this.serverIP = serverIP;
        isCache = true;
    }

    public void serverBehaviorCache() {
        try (
                ServerSocket listenSocket = new ServerSocket(cachePort)
        ) {
            while (true) {
                Socket connectionSocket = listenSocket.accept();
                PrintWriter outToSocket = new PrintWriter(connectionSocket.getOutputStream(), true);
                BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                waitTilReady(inFromSocket, "server behavior cache");
                String cmdArg = inFromSocket.readLine();
                debugWriteToFile("cache received line from client-cache socket:\n" + cmdArg);
                Path cmdArgPath = FileSystems.getDefault().getPath(cmdArg);
                File f = new File(cmdArg);
                if (!f.exists()) {
                    debugWriteToFile("cache getting file from server");
                    clientBehaviorGet(switchPathBase(cmdArgPath, "server_fl").toString());
                }

                debugWriteToFile("cache printing announcement to client-cache socket");
                outToSocket.println("message back");
                try (
                        BufferedReader brInFromFile = Files.newBufferedReader(cmdArgPath, StandardCharsets.UTF_8)
                ) {
                    String ln;
                    while ((ln = brInFromFile.readLine()) != null) {
                        debugWriteToFile("cache printing file line to client-cache socket");
                        outToSocket.println(ln);
                    }
                    outToSocket.println("file over");
                }
            }
        } catch (IOException e) {
            System.out.println("IO issue encountered RE: port " + cachePort);
            System.out.println(e.getMessage());
        }
    }

    public void clientBehaviorGet(String cmdArg) throws IOException {
        String ip = isCache ? serverIP : cacheIP;
        int port = isCache ? serverPort : cachePort;
        // Adapted from: https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KnockKnockClient.java
        try (
                Socket sock = new Socket(ip, port);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter pwOutToFile = new PrintWriter(cmdArg)
        ) {
            pwOutToSock.println(cmdArg);

            Path cmdArgPath = FileSystems.getDefault().getPath(cmdArg);
            Path outFilePath = switchPathBase(cmdArgPath, "client_fl");
            String ln;
            int i = 0;
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
