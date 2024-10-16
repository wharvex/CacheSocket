package snw;

import interfaces.IProtocol;

import java.nio.file.Path;

public class snw_transport implements IProtocol {
    public snw_transport() {
    }

    @Override
    public void sendFile(Path path) throws Exception {
        System.out.println("snw send");
    }

    @Override
    public void receiveFile(Path path) throws Exception {
        System.out.println("snw receive");
    }
}
