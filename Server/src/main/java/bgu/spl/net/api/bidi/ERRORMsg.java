package bgu.spl.net.api.bidi;

import java.util.Arrays;

public class ERRORMsg implements Message {
    private short opcode = 11;
    private short msgOpcode;

    public ERRORMsg(short otherOpcode){
        this.msgOpcode = otherOpcode;
    }

    @Override
    public short getOpcode() {

        return opcode;
    }

    public short getMsgOpcode() {

        return msgOpcode;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] getByte (){
        byte[] result = new byte[4];
        byte[] temp1 = shortToBytes(opcode);
        byte[] temp2 = shortToBytes(msgOpcode);
        result[0] = temp1[0];
        result[1] = temp1[1];
        result[2] = temp2[0];
        result[3] = temp2[1];
        return result;
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {

    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}

