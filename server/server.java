package server;

import static utils.utils.tryParsePort;
import java.io.*;
import java.net.*;

public class server {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello from server!");
        if (args.length != 2) {
            throw new IllegalArgumentException("Run server with exactly 2 arguments.");
        }
        String portStr = args[0];
        int port = tryParsePort(portStr);
        String transportProtocol = args[1];
        System.out.println(port + transportProtocol);
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(port);
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
            new BufferedReader(new InputStreamReader(
                                   connectionSocket.getInputStream()));
            DataOutputStream outToClient =
            new DataOutputStream(
                connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            capitalizedSentence =
            clientSentence.toUpperCase() + '\n';
            outToClient.writeBytes(capitalizedSentence);
        }
    }
}
