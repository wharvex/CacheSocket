package client;

import java.io.*;
import java.net.*;
import tcp.tcp_transport;
import java.util.Scanner;
import static utils.utils.tryParsePort;
import java.nio.file.*;
import java.nio.charset.*;

public class client {
    public static void main(String[] args) throws Exception {
        // Testing.
        tcp_transport x = new tcp_transport();
        x.announce();

        // Validate/store command line args.
        // args: server IP, server port, cache IP, cache port, protocol
        if (args.length != 5) {
            throw new IllegalArgumentException("Run client with exactly 5 arguments.");
        }
        String serverIP = args[0];
        int serverPort = tryParsePort(args[1]);
        String cacheIP = args[2];
        int cachePort = tryParsePort(args[3]);
        String transportProtocol = args[4];

        // Testing.
        System.out.println("\nclient says: server IP: " + serverIP);
        System.out.println("client says: server port: " + serverPort);
        System.out.println("client says: cache port: " + cachePort);
        System.out.println("client says: cache IP: " + cacheIP);
        System.out.println("client says: protocol: " + transportProtocol + "\n");

        // Accept/process user commands.
        Scanner s = new Scanner(System.in);
        String currentLine;
        do {
            // Accept line of user input.
            currentLine = s.nextLine();

            // Get the first word.
            String splitLine[] = currentLine.split(" ", 2);
            String cmdType = splitLine[0];

            // TODO: handle this better
            String cmdArg = splitLine.length > 1 ? splitLine[1] : "";

            // Proceed according to the command type.
            Path path;
            switch (cmdType) {
            case "get":
                // Announce operation.
                System.out.println("GET file: " + cmdArg);

                // Setup socket and streams.
                Socket clientSocket = new Socket(cacheIP, cachePort);
                DataOutputStream toCache = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader fromCache = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                path = FileSystems.getDefault().getPath("client_fl", splitLine[1]);
                BufferedWriter toFile = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

                // Send line to Cache with file name to get.
                toCache.writeBytes(cmdArg + '\n');

                // Receive announcement line from Cache and display it.
                String cacheAnnouncement = fromCache.readLine();
                System.out.println(cacheAnnouncement);

                // Receive the file and write it out.
                // https://stackoverflow.com/a/17623638
                String l;
                while ((l = fromCache.readLine()) != null) {
                    toFile.write(l);
                    toFile.newLine();
                }

                // Close the socket.
                clientSocket.close();
                break;
            case "put":
                System.out.println("put");
                path = FileSystems.getDefault().getPath("client_fl", splitLine[1]);
                BufferedReader reader = Files.newBufferedReader(path,
                                        StandardCharsets.UTF_8);
                break;
            case "quit":
                System.out.println("Exiting program!");
                break;
            default:
                System.out.println("unrecognized command type");
                break;
            }
        } while (!currentLine.equals("quit"));
    }
}
