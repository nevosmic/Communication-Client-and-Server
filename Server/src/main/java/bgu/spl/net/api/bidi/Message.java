package bgu.spl.net.api.bidi;

public interface Message {

     short getOpcode();
     byte[] getByte();
     void act(BidiMessagingProtocolImpl myProtocol);
}
