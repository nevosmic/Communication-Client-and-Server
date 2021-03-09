package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.BlockingConnectionHandler;

public class LOGOUTMsg implements Message{
    private short opcode=3;

    public LOGOUTMsg(){}

    @Override
    public short getOpcode() {

        return opcode;
    }
    @Override
    public byte[] getByte() {

        return new byte[0];
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {
        myProtocol.logOut(this.opcode);
    }

}
