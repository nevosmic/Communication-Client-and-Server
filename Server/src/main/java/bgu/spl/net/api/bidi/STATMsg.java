package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;

public class STATMsg implements Message {
    private short opcode = 8;
    private String userName;

    public STATMsg(byte[] bytesArr){

        this.userName = new String(bytesArr,0,bytesArr.length, StandardCharsets.UTF_8);
    }

    @Override
    public short getOpcode() {
        return opcode;
    }

    public String getUserName() {
        return userName;
    }
    @Override
    public byte[] getByte() {
        return new byte[0];
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {
        myProtocol.stat(opcode,userName);

    }
}
