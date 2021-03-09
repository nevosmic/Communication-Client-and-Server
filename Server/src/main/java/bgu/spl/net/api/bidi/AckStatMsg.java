package bgu.spl.net.api.bidi;

import java.util.Arrays;

public class AckStatMsg implements Message{
    private short opcode=10;
    private short statopcode=8;
    private short numPosts;
    private short numFollowers;
    private short numFollowing;

    public AckStatMsg(short numPosts, short numFollowers, short numFollowing){
        this.numPosts = numPosts;
        this.numFollowing = numFollowing;
        this.numFollowers = numFollowers;
    }

    public short getNumFollowers() {
        return numFollowers;
    }

    public short getNumPosts() {
        return numPosts;
    }

    public short getNumFollowing() {
        return numFollowing;
    }

    @Override
    public short getOpcode() {
        return opcode;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] getByte (){
        byte[] temp1 = shortToBytes(opcode);
        byte[] temp2 = shortToBytes(statopcode);
        byte[] temp3 = shortToBytes(numPosts);
        byte[] temp4 = shortToBytes(numFollowers);
        byte[] temp5 = shortToBytes(numFollowing);

        byte[] result = new byte[10];

        result[0] = temp1[0];
        result[1] = temp1[1];
        result[2] = temp2[0];
        result[3] = temp2[1];
        result[4] = temp3[0];
        result[5] = temp3[1];
        result[6] = temp4[0];
        result[7] = temp4[1];
        result[8] = temp5[0];
        result[9] = temp5[1];

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
