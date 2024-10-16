package interfaces;

import java.nio.file.Path;

public interface IProtocol {
    void sendFile(Path path) throws Exception;

    void receiveFile(Path path) throws Exception;

    int getConnectionPort();

    void setConnectionPort(int connectionPort);

    int getPort();
}
