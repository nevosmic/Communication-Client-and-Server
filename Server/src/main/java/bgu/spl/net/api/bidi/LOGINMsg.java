package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LOGINMsg implements Message {
    private short opcode =2;
    private String username;
    private String password;

    public LOGINMsg(byte[] byteArr){
        int index1=0;
        boolean stop = false;
        for (int i=0 ; i<byteArr.length; i=i+1){
            if(byteArr[i]==0 & !stop){
                index1 = i;
                stop = true;
            }
        }
        this.username = new String(byteArr,0,index1, StandardCharsets.UTF_8);
        byte[] tempArray = Arrays.copyOfRange(byteArr,index1+1,byteArr.length-1);
        this.password = new String(tempArray,0,tempArray.length);

    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public short getOpcode(){
        return opcode;
    }
    @Override
    public byte[] getByte() {
        return new byte[0];
    }

    @Override
    public void act(BidiMessagingProtocolImpl myProtocol) {

        myProtocol.logIn(this.opcode, this.password,this.username);
    }


}
