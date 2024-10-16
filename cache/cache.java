package cache;

import static utils.utils.*;

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
        validateProtocolArg(transportProtocolString);

        newServerBehaviorCache(serverIP, cachePort, serverPort, transportProtocolString.equalsIgnoreCase("snw"));
    }
}
