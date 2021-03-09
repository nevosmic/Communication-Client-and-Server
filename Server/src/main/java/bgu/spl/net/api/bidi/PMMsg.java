package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PMMsg implements Message {
    private short opcode =6;
    private String userName;
    private String content;

    public PMMsg(byte[] byteArry){
        int index1=0;
        boolean stop = false;
        for (int i=0 ; i<byteArry.length; i=i+1){
            if(byteArry[i]==0 & !stop){
                index1 = i;
                stop = true;
            }
        }
     this.userName = new String(byteArry,0,index1, StandardCharsets.UTF_8);
        byte[] tempArray = Arrays.copyOfRange(byteArry,index1+1,byteArry.length);
        this.content = new String(tempArray,0,tempArray.length);

    }
    @Override
    public short getOpcode() {
        return opcode;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }
    @Override
    public byte[] getByte() {
        return new byte[0];
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {
        myProtocol.pm(opcode,userName,content);
    }
}
