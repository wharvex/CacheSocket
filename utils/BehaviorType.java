package utils;

public enum BehaviorType {
    CLIENT_GET(20005),
    CLIENT_PUT(20010),
    SERVER_CACHE(20015),
    SERVER_SERVER(20020);

    public final int port;

    BehaviorType(int port) {
        this.port = port;
    }
}
