package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.REGISTERMsg;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private Connections<T> allCon;
    private int conHandlerId;


    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, Connections<T> con, int conHId) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.allCon = con;
        this.conHandlerId = conHId;

    }

    @Override
    public void run() {
        protocol.start(conHandlerId,allCon);//protocol's constructor
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {//once a client sends a msg the server automatically sends a notification

        try {
            byte[] msgBytes = encdec.encode(msg);
            synchronized (this){//needed in case 2 threads trying to write to the same client - one finish and than the other start
                out.write(msgBytes);
            }

            out.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
