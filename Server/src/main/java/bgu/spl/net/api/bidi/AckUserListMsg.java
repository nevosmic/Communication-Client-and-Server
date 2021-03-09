package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.Vector;


public class AckUserListMsg implements Message {
    private short opcode = 10;
    private short userlistopcode = 7;
    private short numOfUsers;
    private Vector<String> userNameList;

    public AckUserListMsg( Set<String> userNameList){
        this.numOfUsers = (short)userNameList.size();
        this.userNameList = new Vector<>();
        for(String s :userNameList){
            this.userNameList.add(s);
        }


    }
    @Override
    public short getOpcode() {
        return opcode;
    }

    public short getUserlistopcode() {
        return userlistopcode;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public Vector<String> getUserNameList() {
        return userNameList;
    }

    public byte[] getByte(){
        byte[] temp1 = shortToBytes(opcode);
        byte[] temp2 = shortToBytes(userlistopcode);
        byte[] temp3 = shortToBytes(numOfUsers);
        String userList = "";
        for(int i =0; i< userNameList.size(); i = i+1){
            userList = userList + userNameList.get(i)+ "\0";//are we sure ?>
        }
        byte[] temp4 = userList.getBytes();

        byte[] result = new byte[temp1.length+temp2.length+temp3.length+temp4.length+1];

        result[0] = temp1[0];
        result[1] = temp1[1];
        result[2] = temp2[0];
        result[3] = temp2[1];
        result[4] = temp3[0];
        result[5] = temp3[1];

        for (int i = 0; i < temp4.length; i++){
            result[6+i] = temp4[i];
        }

      //  result[result.length-1] = '\0';

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
        myProtocol.usersList(opcode);
    }
}
