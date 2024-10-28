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

    public tcp_transport(PrintWriter pw) {
        this.pw = pw;
    }

    public tcp_transport(BufferedReader br) {
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
            debugWriteToFile("client-behaving party about to read from sock br " + (++i));
            ln = br.readLine();
            debugWriteToFile("client-behaving party receives line: " + ln);
            if (ln.equals("file over")) {
                debugWriteToFile("file is over...");
                break;
            }
            writeToFile(ln, path);
        }
    }
}
