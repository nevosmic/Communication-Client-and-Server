package bgu.spl.net.api.bidi;

public class ACKMsg implements Message {
    private short otherOpcode = 3;
    private short opcode = 10;

    public ACKMsg(short otherOpcode){
        this.otherOpcode = otherOpcode;
    }

    @Override
    public short getOpcode() {

        return opcode;
    }

    public short getOtherOpcode() {

        return otherOpcode;
    }

    public byte[] getByte(){
        byte[] result = new byte[4];
        byte[] temp1 = shortToBytes(opcode);
        byte[] temp2 = shortToBytes(otherOpcode);

        result[0] = temp1[0];
        result[1] = temp1[1];
        result[2] = temp2[0];
        result[3] = temp2[1];
        return result;

    }
    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {

    }
}
