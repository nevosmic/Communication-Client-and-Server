package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String args[]) {
        DataBase data = new DataBase();
        int port = Integer.parseInt(args[0]);
        Server s = Server.threadPerClient(port, () -> new BidiMessagingProtocolImpl(data), () -> new MessageEncoderDecoderImpl<Message>());
        s.serve();

    }
}
