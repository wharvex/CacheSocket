package tcp;

import interfaces.IProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static utils.utils.waitTilReady;

public class tcp_transport implements IProtocol {
    int serverPort;
    int cachePort;
    String serverIP;
    String cacheIP;
    boolean isCache;
    String cmdArg;

    // Client constructor.
    public tcp_transport(int serverPort, int cachePort, String serverIP, String cacheIP, String cmdArg) {
        this.serverPort = serverPort;
        this.cachePort = cachePort;
        this.serverIP = serverIP;
        this.cacheIP = cacheIP;
        this.cmdArg = cmdArg;
        isCache = false;
    }

    public void announce() {
        System.out.println("hello from tcp");
    }

    public void serverBehavior() {
    }

    public void clientBehaviorGet() throws IOException {
        String destIP = isCache ? serverIP : cacheIP;
        int destPort = isCache ? serverPort : cachePort;
        // Adapted from: https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KnockKnockClient.java
        try (
                Socket sock = new Socket(destIP, destPort);
                PrintWriter pwOutToSock = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader brInFromSock = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter pwOutToFile = new PrintWriter(cmdArg)
        ) {
            pwOutToSock.println(cmdArg);
            String ln;
            while (true) {
                waitTilReady(brInFromSock);
                ln = brInFromSock.readLine();
                if (ln == null)
                    break;
                pwOutToFile.println(ln);
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + destIP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O issue with host: " + destIP);
            System.exit(1);
        }
    }
}
