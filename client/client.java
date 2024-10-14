package client;

import java.io.*;
import java.net.*;
import tcp.tcp_transport;
import java.util.Scanner;
import static utils.utils.tryParsePort;

public class client {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello from client!");
        tcp_transport x = new tcp_transport();
        x.announce();
        if (args.length != 5) {
            throw new IllegalArgumentException("Run client with exactly 5 arguments.");
        }
        String serverIP = args[0];
        int serverPort = tryParsePort(args[1]);
        String cacheIP = args[2];
        int cachePort = tryParsePort(args[3]);
        String transportProtocol = args[4];
        System.out.println(serverIP + serverPort + cacheIP + cachePort + transportProtocol);
        Scanner s = new Scanner(System.in);
        String currentLine;
        do {
            currentLine = s.nextLine();
            String splitLine[] = currentLine.split(" ", 2);
            String cmdType = splitLine[0];
            switch (cmdType) {
            case "get":
                System.out.println("this is a get command");
                String sentence;
                String modifiedSentence;
                BufferedReader inFromUser = new BufferedReader(
                    new InputStreamReader(System.in));
                Socket clientSocket = new Socket(serverIP, serverPort);
                DataOutputStream outToServer = new DataOutputStream(
                    clientSocket.getOutputStream());
                BufferedReader inFromServer =
                new BufferedReader(new InputStreamReader(
                                       clientSocket.getInputStream()));
                sentence = inFromUser.readLine();
                outToServer.writeBytes(sentence + '\n');
                modifiedSentence = inFromServer.readLine();
                System.out.println("FROM SERVER: " +
                                   modifiedSentence);
                clientSocket.close();
                break;
            case "put":
                System.out.println("this is a put command");
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
