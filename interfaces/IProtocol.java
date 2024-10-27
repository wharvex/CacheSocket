package interfaces;

import java.io.IOException;

public interface IProtocol {
    void serverBehaviorCache();

    void clientBehaviorGet(String cmdArg) throws IOException;

    void sendFile();

    void receiveFile();
}
