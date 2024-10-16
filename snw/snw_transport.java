package snw;

import interfaces.IProtocol;
import utils.SNWFile;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.util.Random;

import static utils.utils.debugWriteToFile;

/**
 * Stop-and-wait reliability over UDP:
 * <p>
 * This version of your project will implement the text file exchange using stop-and-wait
 * reliability over UDP.
 * <p>
 * As a reminder, UDP provides best-effort packet delivery service;
 * you will have to implement reliability checks on top of UDP to ensure that your data is
 * successfully transmitted between the sender and the receiver.
 * <p>
 * Since we are working at the application level, we will implement our stop and
 * wait reliability at the level of message chunks.
 * <p>
 * We will discuss this functionality in terms of sender and receiver.
 * <p>
 * Note that depending on whether you are performing get or put your sender and receiver will
 * switch places in the client-server architecture.
 * <p>
 * Furthermore, depending on whether the server or the cache is delivering a file, the sender will change
 * accordingly.
 * <p>
 * The reliable data transfer should function identically for both get and put.
 * <p>
 * *******************YOUR RELIABLE PROTOCOL WILL FUNCTION AS FOLLOWS****************************
 * <p>
 * FIRST, the sender calculates the amount of data to be transmitted and sends a
 * “length” message to the receiver, letting them know how many bytes of data to expect.
 * <p>
 * The length message should contain the string LEN:Bytes.
 * <p>
 * SECOND, the sender splits the data into equal chunks of 1000 bytes each, and
 * proceeds to send the data one chunk at a time.
 * <p>
 * Note that the last chunk might be smaller than 1000 Bytes and that is OK.
 * <p>
 * Your programs should be able to handle arbitrary file sizes.
 * <p>
 * After transmitting each chunk, the sender stops and waits for an acknowledgement from the
 * receiver – this underpins the stop-and-wait name of the protocol you are implementing.
 * <p>
 * Specifically, upon receiving a chunk of the expected file, the receiver has to craft and
 * send a special message containing the string ACK, before a new packet can be sent.
 * <p>
 * Finally, once the receiver receives all expected bytes (as per the LEN message),
 * the receiver will craft a special message containing the string FIN.
 * This message will trigger connection termination.
 * <p>
 * Timeouts. Note that there are a few points in the sender-receiver interaction
 * where a timeout might occur. The below description specifies how your program
 * should behave in a timeout.
 * <p>
 * Timeout after LEN message. If no data arrives at the receiver within one
 * second from the reception of a LEN message, the receiver program
 * should terminate, displaying “Did not receive data. Terminating.”
 * <p>
 * Timeout after a data packet. If no ACK is received by the sender within
 * one second from transmitting a data packet, the sender will terminate,
 * displaying “Did not receive ACK. Terminating.”
 * <p>
 * Timeout after ACK. If no data is received by the receiver within one
 * second of issuing an ACK, the receiver will terminate, displaying “Data
 * transmission terminated prematurely.”.
 */
public class snw_transport implements IProtocol {
    InetAddress ip;
    int port;

    public snw_transport(String ip, int port) throws Exception {
        this.ip = InetAddress.getByName(ip);
        Random r = new Random();
        int low = 20005;
        int high = 24000;
        this.port = r.nextInt(high - low) + low;
    }

    @Override
    public void sendFile(Path path) throws Exception {
        debugWriteToFile("Creating DatagramSocket on port " + port);
        try (DatagramSocket ds = new DatagramSocket(port)) {
            // Set timeout length to 1 second.
            ds.setSoTimeout(1000);

            // Setup.
            SNWFile snwFile = new SNWFile(path.toString());

            // Send length message
            String lenMsg = "LEN:" + snwFile.fileLenInBytes;
            byte[] buf = lenMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
            ds.send(packet);

            // Receive acknowledgement.
            buf = getNewBuf();
            packet = new DatagramPacket(buf, buf.length);
            tryReceiveMessage(packet, ds, "Did not receive ACK. Terminating.");
            System.out.println(new String(packet.getData()));
        }
    }

    @Override
    public void receiveFile(Path path) throws Exception {
        try (DatagramSocket ds = new DatagramSocket(port)) {
            // Set timeout length to 1 second.
            ds.setSoTimeout(1000);

            // Create bytes buffer.
            byte[] buf = new byte[1000];

            // Receive length message.
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            tryReceiveMessage(packet, ds, "Did not receive data. Terminating.");
            System.out.println(new String(packet.getData()));

            // Send acknowledgement.
            sendAcknowledgement(ds);
        }
    }

    private void sendAcknowledgement(DatagramSocket socket) throws Exception {
        String ackMsg = "ACK";
        byte[] ackMsgBytes = ackMsg.getBytes();
        DatagramPacket p = new DatagramPacket(ackMsgBytes, ackMsgBytes.length, ip, port);
        socket.send(p);
    }

    private void tryReceiveMessage(DatagramPacket p, DatagramSocket s, String errMsg) throws IOException {
        try {
            s.receive(p);
        } catch (SocketTimeoutException ste) {
            System.out.println(errMsg);
            System.exit(-1);
        }
    }

    private byte[] getNewBuf() {
        return new byte[1000];
    }
}
