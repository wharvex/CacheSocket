package interfaces;

import java.io.IOException;

public interface IProtocol {
    void serverBehavior();

    void clientBehaviorGet() throws IOException;
}
