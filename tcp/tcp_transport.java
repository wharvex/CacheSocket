package tcp;

import interfaces.IProtocol;

import java.io.PrintWriter;

public class tcp_transport implements IProtocol {
    PrintWriter pw;

    public tcp_transport(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public void sendFile() {

    }

    @Override
    public void receiveFile() {

    }
}
