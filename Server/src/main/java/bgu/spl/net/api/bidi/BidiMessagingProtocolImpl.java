package bgu.spl.net.api.bidi;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private boolean shouldT;
    private Connections <Message> myCon;
    private int connectionId;
    private DataBase data;
    private String username = " ";//if a user want to a act before login
    private boolean LogedIn =false;// to prevent more then 1  users to logIn


    public BidiMessagingProtocolImpl(DataBase data){
        shouldT = false;
        this.data = data;
    }

    public void start(int connectionId, Connections<Message> connections){// initialize a specific cH's protocol
        this.connectionId = connectionId;
        this.myCon = connections;

    }

    public void process(Message message){

        message.act(this);

    }
    public boolean shouldTerminate(){

        return shouldT;
    }

    public String getUserName() {

        return username;
    }

    public void register (String username,short opcode, String password){
        if (data.isRegister(username))//already registered
        {
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId,error);
        }
        else if(!data.register(username, password)) {//double check
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{// not registered, so he is new
                Message ack = new ACKMsg(opcode);
                myCon.send(connectionId, ack);
            }
    }

    public void logIn(short opcode, String password, String myUsername) {

        if(!(data.isRegister(myUsername))){
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId,error);
        }
        else if(LogedIn){
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId,error);
        }
        else if(!(data.isPasswordTheSame(myUsername,password))){
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId,error);
        }
        else if(data.isLoggedIn(myUsername)){
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId,error);
        }
        else if(!(data.addToLoginMap(myUsername))){
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{
            this.username = myUsername;//saves client's name
            LogedIn = true;
            data.getUser(username).setConnectionId(connectionId);//ask someone
            ConcurrentLinkedQueue<Message> myWaitingMsgList =  data.getUser(username).getWaitingList();
            if(!myWaitingMsgList.isEmpty()) {//send the message that the client receive while he was log out
                for (Message m : myWaitingMsgList) {
                    myCon.send(connectionId, m);
                }
                data.getUser(username).clearWaitingList();
            }
            Message ack = new ACKMsg(opcode);
            myCon.send(connectionId,ack);
        }
    }

    public void logOut(short opcode) {
        // if the user do logout we dont ant anybody to send him a message (will be added to waiting list)
           if (!(data.isRegister(username))) {
               Message error = new ERRORMsg(opcode);
               myCon.send(connectionId, error);
           } else if (!(data.isLoggedIn(username))) {
               Message error = new ERRORMsg(opcode);
               myCon.send(connectionId, error);
           } else {//logged in
               synchronized (data.getUser(username)) {
                   LogedIn=false;
                   data.logOut(username);
                   Message ack = new ACKMsg(opcode);
                   myCon.send(connectionId, ack);
               }

           }

    }

    public void follow(short opcode, int followUnfollow, Vector usersNameList, short numOfUsers) {
        if (!(data.isRegister(username))) {//userName of the Msg sender
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (!(data.isLoggedIn(username))) {//check if the user is log in
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (followUnfollow == 0) {//follow
            Vector<String> succes = data.addToFollowingMap(username, usersNameList);
            if (succes.size() == 0) { //no succes at all
                Message error = new ERRORMsg(opcode);
                myCon.send(connectionId, error);
            } else {
                Message ack = new AckFollowMsg((short) succes.size(), succes);
                myCon.send(connectionId,ack);
            }
        }
        else if (followUnfollow == 1){//unfollow
            Vector<String> succes = data.removeFromFollowing(username,usersNameList);
           if(succes.size() == 0){
               Message error = new ERRORMsg(opcode);
               myCon.send(connectionId, error);
           }
           else{
               Message ack = new AckFollowMsg((short) succes.size(), succes);
               myCon.send(connectionId,ack);
           }
        }
    }

    public void post(short opcode, String  content, Vector<String> tagedPeople){
        if (!(data.isRegister(username))) {//userName of the Msg sender
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (!(data.isLoggedIn(username))) {//check if the user is log in
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{
            data.addPost(username,content);
            Message ack = new ACKMsg(opcode);
            myCon.send(connectionId,ack);
            ConcurrentLinkedQueue<String> myFollowers = data.getFollowers(username);
            //sending notification to all my followers
            for(String currentUser : myFollowers){
                    Message notic = new NOTIFICATIONMsg('1', username, content);
                    synchronized (data.getUser(currentUser)){// sync with the logout
                        if(data.isLoggedIn(currentUser)){//if the currentUser is logged in

                            int userConId =  data.getUser(currentUser).getConnectionId();
                            myCon.send(userConId,notic);
                        }
                        else{
                            data.getUser(currentUser).addToWAtingList(notic);
                        }
                    }




            }
            //sending notification to all tagged people
            for(String currentUser: tagedPeople){
                if(!(data.getUser(username).isHeFollowingMe(currentUser))) {
                    if (data.isRegister(currentUser)) {
                        Message notific = new NOTIFICATIONMsg('1', username, content);
                        synchronized (data.getUser(currentUser)) {
                            if (data.isLoggedIn(currentUser)) {//if the currentUser is logged in
                                int userConId = data.getUser(currentUser).getConnectionId();
                                myCon.send(userConId, notific);
                            } else {
                                data.getUser(currentUser).addToWAtingList(notific);
                            }
                        }
                    }
                }
            }
        }
    }
    public void pm(short opcode,  String recivingUser, String content){
        if (!(data.isRegister(username))||!(data.isRegister(recivingUser))) {//if one of them is not register
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (!(data.isLoggedIn(username))) {//check if the sending user is logged in
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{
            Message ack = new ACKMsg(opcode);
            myCon.send(connectionId,ack);
            data.getUser(username).addToPm(content);
            Message notific = new NOTIFICATIONMsg('0',username,content);
            synchronized (data.getUser(recivingUser)) {
                if (data.isLoggedIn(recivingUser)) {
                    myCon.send(data.getUser(recivingUser).getConnectionId(), notific);
                } else {//reciving user is not logged in
                    data.getUser(recivingUser).addToWAtingList(notific);
                }
            }
        }
    }

    public void usersList(short opcode){
        if (!(data.isRegister(username))) {
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (!(data.isLoggedIn(username))) {//check if the user is log in
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{
            Message usersList = new AckUserListMsg(data.getUserList());
            myCon.send(connectionId,usersList);
        }

    }
    public void stat(short opcode, String certianUser){
        if (!(data.isRegister(username))) {
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else if (!(data.isLoggedIn(username))) {//check if the user is log in
            Message error = new ERRORMsg(opcode);
            myCon.send(connectionId, error);
        }
        else{
            User theOneIwantToStock = data.getUser(certianUser);
            if(theOneIwantToStock == null){
                Message error = new ERRORMsg(opcode);
                myCon.send(connectionId, error);
            }
            else{
                Message stat = new AckStatMsg(theOneIwantToStock.numOfPosts(),theOneIwantToStock.numOfFollowers(),theOneIwantToStock.numOfFollowing());
                myCon.send(connectionId,stat);
            }

        }
    }


}
