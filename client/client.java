package client;

import utils.Command;

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

            // Create Command object and validate.
            Command cmd = new Command(currentLine);
            if (!cmd.isValid) {
                System.out.println("Bad command.");
                continue;
            }

            // Display "awaiting" message.
            if (cmd.cmdType.equalsIgnoreCase("get") || cmd.cmdType.equalsIgnoreCase("put"))
                System.out.println("Awaiting server response.");

            // Proceed according to the command type.
            Path path;
            switch (cmd.cmdType.toLowerCase()) {
                case "get":
                    newClientBehaviorGet(cacheIP, cachePort, "client_fl", cmd);
                    break;
                case "put":
                    System.out.println("Awaiting server response.");
                    System.out.println("put");
                    path = FileSystems.getDefault().getPath("client_fl", cmd.cmdArg);
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
