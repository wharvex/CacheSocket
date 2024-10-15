package cache;

import java.io.*;
import java.net.*;
import tcp.tcp_transport;
import java.util.Scanner;
import static utils.utils.tryParsePort;
import java.nio.file.*;
import java.nio.charset.*;

public class cache {
    public static void main(String[] args) {
        // Validate/store command line args.
        // args: cache port, server ip, server port, protocol
        if (args.length != 4) {
            throw new IllegalArgumentException("Run cache with exactly 4 arguments.");
        }
        int cachePort = tryParsePort(args[0]);
        String serverIP = args[1];
        int serverPort = tryParsePort(args[2]);
        String transportProtocol = args[3];

        // Testing.
        System.out.println("\ncache says: cache port: " + cachePort);
        System.out.println("cache says: server IP: " + serverIP);
        System.out.println("cache says: server port: " + serverPort);
        System.out.println("cache says: protocol: " + transportProtocol + "\n");

        ServerSocket welcomeSocket = new ServerSocket(cachePort);
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            outToClient.writeBytes(capitalizedSentence);
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
