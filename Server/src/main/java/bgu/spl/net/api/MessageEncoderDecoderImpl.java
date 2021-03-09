package bgu.spl.net.api;

import bgu.spl.net.api.bidi.*;

import java.util.Arrays;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1024];
    private int len =0;
    private int index=0;
    private int numOfZero = 0;
    private short opcode;
    private short numOfU =0;
public MessageEncoderDecoderImpl(){}
   public Message decodeNextByte(byte nextByte){

      if(index < 2){
           if(nextByte == 7 || nextByte == 3){
               pushByte(nextByte);
               byte[] myByets = Arrays.copyOfRange(bytes,0,2);//short is 2 bytes
               opcode = bytesToShort(myByets);
               if(opcode == 7 | opcode == 3){
                   return popT();
               }
           }
       }
       if(index == 2){
           byte[] myByets = Arrays.copyOfRange(bytes,0,2);//short is 2 bytes
           opcode = bytesToShort(myByets);


       }
       if(index > 2 ){
           if(opcode == 4){//we need to read one more for the num of users
               if(index > 4){
                   byte[] myNumOfUByets = Arrays.copyOfRange(bytes,3,5);
                   numOfU = bytesToShort(myNumOfUByets);
                   if(nextByte == '\0'){
                       numOfZero++;
                       if(numOfU == 1){
                           pushByte(nextByte);
                           return popT();
                       }
                       else if(numOfZero == numOfU){
                           pushByte(nextByte);
                           return popT();
                   }

                       }
                   }

               }



           if(opcode == 5| opcode == 8){//only zero at the end
               if(nextByte =='\0'){
                   return popT();
               }
           }
           if(opcode == 1| opcode == 2| opcode == 6| opcode ==9){//two pair of zeros
               if(nextByte == '\0'){
                   numOfZero++;
                   if(numOfZero == 2){
                       return popT();
                   }
               }
           }

       }


        pushByte(nextByte);// accumulating message
        index++;
        return null;//not a msg yet
    }
    private void pushByte( byte nextByte){
       if(len >=bytes.length){
           bytes = Arrays.copyOf(bytes, len*2);
       }
       bytes[len++]= nextByte;
    }

    public byte[] encode(Message message){
        return  message.getByte();
    }

    private Message popT(){
       Message result = null;
       byte[] myByets = Arrays.copyOfRange(bytes,0,2);//short is 2 bytes
       short shorOpcode = bytesToShort(myByets);
       byte[] copyByets = Arrays.copyOfRange(bytes,2,len);

       if(shorOpcode == 1){
          result = new REGISTERMsg(copyByets);
       }

       if (shorOpcode ==2){
           result = new LOGINMsg(copyByets);
       }
       if(shorOpcode == 3){
           result = new LOGOUTMsg();
       }
       if(shorOpcode == 4){
           result = new FOLLOWMsg(copyByets);
       }
        if(shorOpcode == 5){
            result = new POSTMsg(copyByets);
        }
        if(shorOpcode == 6){
            result = new PMMsg(copyByets);
        }
        if(shorOpcode == 7){
            result = new USERLISTMsg();
        }
        if(shorOpcode == 8){
            result = new STATMsg(copyByets);
        }

        //reset fields
            len = 0;
            index = 0;
            opcode = 0;
            numOfU = 0;
            numOfZero = 0;
           return result;
    }

    private short bytesToShort(byte[] byteArr){
       short result = (short)((byteArr[0] & 0xff)<<8);
       result += (short)(byteArr[1]&0xff);
       return result;
    }
}
