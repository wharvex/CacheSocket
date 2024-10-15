package client;
import tcp.tcp_transport;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        System.out.println("Hello from client!");
        tcp_transport x = new tcp_transport();
        x.announce();
        if (args.length != 5) {
            throw new IllegalArgumentException("Run client with exactly 5 arguments.");
        }
        String serverIP = args[0];
        String serverPort = args[1];
        String cacheIP = args[2];
        String cachePort = args[3];
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
