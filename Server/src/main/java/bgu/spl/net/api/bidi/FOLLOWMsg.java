package bgu.spl.net.api.bidi;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

public class FOLLOWMsg implements Message {
    private short opcode = 4;
    private int followUnfollow;
    private short numOfUsres;
    private Vector<String> usersNameList;

    public FOLLOWMsg(byte[] byteArry) {
        this.followUnfollow = 1;
        if(byteArry[0] == 0){
            this.followUnfollow = 0;
        }
        byte[] tempArry= new byte[2];
        tempArry[0]=byteArry[1];
        tempArry[1]=byteArry[2];
        this.numOfUsres = bytesToShort(tempArry);
        this.usersNameList = new Vector<String>(numOfUsres);
        byte[] tempArr2 = Arrays.copyOfRange(byteArry, 3, byteArry.length);
        String userNameList =  new String(tempArr2,0,tempArr2.length, StandardCharsets.UTF_8);

        int counter = 0;
        while (counter < numOfUsres){
           int indexOfZero = userNameList.indexOf(0);
           String s = userNameList.substring(0,indexOfZero);//CUT THE PERSON WE JUST ADD
           userNameList = userNameList.substring(indexOfZero+1);
           this.usersNameList.add(s);
           counter++;
        }




    }

    @Override
    public short getOpcode() {
        return opcode;
    }

    public int getFollowUnfollow() {
        return followUnfollow;
    }

    public int getNumOfUsers() {
        return numOfUsres;
    }

    public Vector<String> getUsersNameList() {
        return usersNameList;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    @Override
    public byte[] getByte() {
        return new byte[0];
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {
        myProtocol.follow(this.opcode, this.followUnfollow, this.usersNameList,this.numOfUsres);

    }
}
