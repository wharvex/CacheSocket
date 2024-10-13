package server;
import static utils.utils.tryParsePort;

public class server {
    public static void main(String[] args) {
        System.out.println("Hello from server!");
        if (args.length != 2) {
            throw new IllegalArgumentException("Run server with exactly 2 arguments.");
        }
        String portStr = args[0];
        int port = tryParsePort(portStr);
        String transportProtocol = args[1];
        System.out.println(port + transportProtocol);
    }
}
