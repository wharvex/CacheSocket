package tcp;

import interfaces.IProtocol;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class tcp_transport implements IProtocol {
    PrintWriter pw;

    public tcp_transport(PrintWriter pw) {
        this.pw = pw;
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
    public void receiveFile() {

    }
}
