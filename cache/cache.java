package cache;

import java.io.*;
import java.net.*;
import tcp.tcp_transport;
import java.util.Scanner;
import static utils.utils.tryParsePort;
import java.nio.file.*;
import java.nio.charset.*;

public class cache {
    public static void main(String[] args) throws Exception {
        // Validate/store command line args.
        // args: cache port, server ip, server port, protocol
        if (args.length != 4) {
            throw new IllegalArgumentException("Run cache with exactly 4 arguments.");
        }
        int cachePort = tryParsePort(args[0]);
        String serverIP = args[1];
        int serverPort = tryParsePort(args[2]);
        String transportProtocol = args[3];

        ServerSocket listenSocket = new ServerSocket(cachePort);
        while(true) {
            Socket connectionSocket = listenSocket.accept();
            BufferedReader brClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream dosClient = new DataOutputStream(connectionSocket.getOutputStream());
            String clientHeader = brClient.readLine();
            System.out.println("cache says: client header: " + clientHeader);
            dosClient.writeBytes("blah\n");
        }

        // System.out.println(port + transportProtocol);
        // String clientSentence;
        // String capitalizedSentence;
        // ServerSocket welcomeSocket = new ServerSocket(port);
        // while(true) {
        //     Socket connectionSocket = welcomeSocket.accept();
        //     BufferedReader inFromClient =
        //         new BufferedReader(new InputStreamReader(
        //                                connectionSocket.getInputStream()));
        //     DataOutputStream outToClient =
        //         new DataOutputStream(
        //         connectionSocket.getOutputStream());
        //     clientSentence = inFromClient.readLine();
        //     capitalizedSentence =
        //         clientSentence.toUpperCase() + '\n';
        //     outToClient.writeBytes(capitalizedSentence);
        // }
    }
}
