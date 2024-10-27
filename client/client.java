package client;

import interfaces.IProtocol;
import tcp.tcp_transport;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static utils.utils.*;

public class client {
    public static void main(String[] args) throws Exception {
        debugWriteToFile("Client starting");
        System.out.println(Path.of(System.getProperty("java.io.tmpdir"), "networking_debug_output.txt"));
        // Validate/store command line args.
        // args: server IP, server port, cache IP, cache port, protocol
        if (args.length != 5) {
            throw new IllegalArgumentException("Run client with exactly 5 arguments.");
        }
        String serverIP = args[0];
        int serverPort = tryParsePort(args[1]);
        String cacheIP = args[2];
        int cachePort = tryParsePort(args[3]);
        String transportProtocolString = args[4];


        // Accept/process user commands.
        Scanner s = new Scanner(System.in);
        String currentLine;
        do {
            // Accept line of user input.
            System.out.print("Enter command: ");
            currentLine = s.nextLine();

            // Get the first word.
            String[] splitLine = currentLine.split(" ", 2);
            String cmdType = splitLine[0];

            // Get second word; validate.
            String cmdArg = splitLine.length > 1 ? splitLine[1] : "";
            if (cmdArg.isEmpty() && !cmdType.equalsIgnoreCase("quit"))
                throw new IllegalArgumentException("Bad user command");

            // Display "awaiting" message.
            if (cmdType.equalsIgnoreCase("get") || cmdType.equalsIgnoreCase("put"))
                System.out.println("Awaiting server response.");

            IProtocol transportProtocol = new tcp_transport(serverPort, cachePort, serverIP, cacheIP);
            debugWriteToFile("File 1 line count (read 1): " + getFile1LineCount());
            // Proceed according to the command type.
            Path path;
            switch (cmdType.toLowerCase()) {
                case "get":
                    transportProtocol.clientBehaviorGet(cmdArg);
                    break;
                case "put":
                    System.out.println("Awaiting server response.");
                    System.out.println("put");
                    path = FileSystems.getDefault().getPath("client_fl", cmdArg);
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
