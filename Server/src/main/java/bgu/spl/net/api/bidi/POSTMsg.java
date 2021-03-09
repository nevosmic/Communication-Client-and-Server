package bgu.spl.net.api.bidi;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class POSTMsg implements Message {
    private short opcode = 5;
    private String content;
    private Vector<String> peopleThatTaged;

    public POSTMsg(byte[] byteArry){

        this.content = new String(byteArry,0,byteArry.length, StandardCharsets.UTF_8);
        this.peopleThatTaged = findTheShtroodel();

    }
    private Vector<String> findTheShtroodel(){
        String contentTEMP = this.content;
        peopleThatTaged = new Vector<>();
        boolean contiNue = true ;
        while (contiNue) {
            int index1 = contentTEMP.indexOf('@');
            if(index1 == -1){//in a case nobody is taged so there is no @
                contiNue = false;
            }
            else{
                int index2 = contentTEMP.indexOf(' ', index1);
                if (index2 == -1){//this is the last word
                    String person = contentTEMP.substring(index1+1, contentTEMP.length());
                    if (!peopleThatTaged.contains(person)) {
                        peopleThatTaged.add(person);
                    }
                    contiNue = false;
                }
                else{
                    String person = contentTEMP.substring(index1+1, index2);
                    if(!peopleThatTaged.contains(person)) {
                        peopleThatTaged.add(person);
                    }
                    contentTEMP = contentTEMP.substring(index2 + 1);
                }
            }
        }

        return  peopleThatTaged;
    }

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

        myProtocol.post(opcode,content,peopleThatTaged);
    }
}
