package bgu.spl.net.api.bidi;

public class USERLISTMsg implements Message {
    private short opcode = 7;

    public USERLISTMsg(){}
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
        myProtocol.usersList(opcode);

    }
}
