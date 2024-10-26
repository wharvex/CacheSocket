package cache;

import interfaces.IProtocol;
import tcp.tcp_transport;

import static utils.utils.tryParsePort;

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
        String transportProtocolString = args[3];

        IProtocol transportProtocol = new tcp_transport(serverPort, cachePort, serverIP);
        transportProtocol.serverBehaviorCache();

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
