package tcp;

import interfaces.IProtocol;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static utils.utils.debugWriteToFile;
import static utils.utils.writeToFile;

public class tcp_transport implements IProtocol {
    PrintWriter pw;
    BufferedReader br;

    public tcp_transport(PrintWriter pw, BufferedReader br) {
        this.pw = pw;
        this.br = br;
    }

    @Override
    public void sendFile(Path path) throws Exception {
        try (
                Stream<String> stream = Files.lines(path)
        ) {
            stream.forEach(pw::println);
            pw.println("file over");
        }
    }

    @Override
    public void receiveFile(Path path) throws Exception {
        int i = 0;
        String ln;
        while (true) {
            debugWriteToFile("tcp_transport about to read line from sock br " + (++i));
            ln = br.readLine();
            debugWriteToFile("tcp_transport read line: " + ln);
            if (ln.equals("file over")) {
                debugWriteToFile("file is over...");
                break;
            }
            writeToFile(ln, path);
        }
    }

    @Override
    public int getConnectionPort() {
        return 0;
    }

    @Override
    public void setConnectionPort(int connectionPort) {

    }

    @Override
    public int getPort() {
        return 0;
    }
}
