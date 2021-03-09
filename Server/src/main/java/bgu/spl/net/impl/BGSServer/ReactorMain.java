package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.Server;

public class ReactorMain {

        public static void main(String args[]){
            DataBase data = new DataBase();
            int port =Integer.parseInt(args[0]);
            int numOfThreads = Integer.parseInt(args[1]);
            Server s2 = Server.reactor(numOfThreads,port,()->new BidiMessagingProtocolImpl(data),()-> new MessageEncoderDecoderImpl<Message>());
            s2.serve();

        }

    }

