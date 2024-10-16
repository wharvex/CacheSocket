package utils;

import java.util.ArrayList;
import java.util.List;

import static utils.utils.getStringOfFile;

public class SNWFile {
    public String fileString;
    public byte[] fileByteArr;
    public long fileLenInBytes;
    public List<byte[]> byteChunks;

    public SNWFile(String filePath) throws Exception {
        fileString = getStringOfFile(filePath);
        fileByteArr = fileString.getBytes();
        fileLenInBytes = fileByteArr.length;
        byteChunks = new ArrayList<>();
        byte[] buf = new byte[1000];
        int j = 0;
        for (int i = 0; i < fileLenInBytes; i++) {
            if (i != 0 && i % 1000 == 0) {
                byteChunks.add(buf);
                j = 0;
                buf = new byte[1000];
            }
            buf[j++] = fileByteArr[i];
        }
    }
}
