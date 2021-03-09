package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NOTIFICATIONMsg implements Message {
    private short opcode = 9;
    private char notificationType;
    private String postingUser;
    private String content;

    public NOTIFICATIONMsg(char notificationType, String postingUser, String content  ){
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }

    @Override
    public short getOpcode() {
        return 0;
    }

    public String getContent() {

        return content;
    }

    public char getNotificationType() {

        return notificationType;
    }
    public byte[] getByte (){

        byte[] temp1 = shortToBytes(opcode);
        byte[] temp3 = postingUser.getBytes();
        byte[] temp4 = content.getBytes();
        byte[] result = new byte[temp1.length+1+temp3.length+temp4.length + 2];
        result[0] = temp1[0];
        result[1] = temp1[1];
        if(notificationType == '1'){
            result[2] = 1;
        }
        else{
            result[2] = 0;
        }

        for(int i = 0; i<temp3.length;i++){// add the postingUser
            result[3+i] = temp3[i];

        }
        int index = 3+temp3.length;
        result[index+1] = '\0'; // add the zero
        index = index+1;

        for(int i = 0; i<temp4.length; i++){//add the content
            result[index+i] = temp4[i];
        }

        index = index+temp4.length;
        result[index] = '\0'; // add the zero

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
