//
// Created by nevosmic@wincs.cs.bgu.ac.il on 12/27/18.
//

#include "../include/ServerReader.h"
using namespace std;
using std::string;

ServerReader ::ServerReader(ConnectionHandler &con,  int* shouldT) : shouldT(shouldT), myConn(con){}

void ServerReader::run() {
    while (1) {


        char* answer = new char;
        if(shouldT == 0){
            cout<<"zerp"<<endl;
            myConn.sendBytes(answer,2);
        }
        if (!(myConn.getBytes(answer,2))) {//get the opcode
            cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
       if (!parseMsg(answer)) {
           *shouldT = -1;//let the keyboardReader know that we receive a logoutAck so it WILL STOP
           delete(answer);//prevent memory leak
           break;



       }

    }


}


bool ServerReader ::parseMsg(char* result) {
    bool stop = true;
    char opcodeArry[2];
    opcodeArry[0] = result[0];
    opcodeArry[1] = result[1];
    short opcode = bytesToShort(opcodeArry);//find what is my opcode


    if(opcode == 11 ){//error
        myConn.getBytes(&result[2],2);
        char messageOpcodeArry[2];
        messageOpcodeArry[0] = result[2];
        messageOpcodeArry[1] = result[3];
        short messageOpcode = bytesToShort(messageOpcodeArry);
        if(messageOpcode == 3){//if we recive a error for the LOGOUT commend
            *shouldT = 1;
        }
        cout << "ERROR " + to_string(messageOpcode) <<endl;

    }
    if (opcode == 10){//ack
        myConn.getBytes(&result[2],2);
        char messageOpcodeArry[2];
        messageOpcodeArry[0] = result[2];
        messageOpcodeArry[1] = result[3];
        short messageOpcode = bytesToShort(messageOpcodeArry);

        if(messageOpcode == 3){//logout
            cout << "ACK "+ to_string(messageOpcode) <<endl;
            stop = false;
        }
        if(messageOpcode == 1 || messageOpcode == 2||messageOpcode == 5|| messageOpcode == 6){//regular ack
            cout << "ACK "+ to_string(messageOpcode) <<endl;
        }
        if(messageOpcode == 4){//follow
            myConn.getBytes(&result[4],2);//?
            char numOfUsersArry[2];
            numOfUsersArry[0] = result[4];
            numOfUsersArry[1] = result[5];
            unsigned short numOfUsers = bytesToShort(numOfUsersArry);
            string userList;
            unsigned int counter = 0;
            while(counter<numOfUsers) {
                myConn.getFrameAscii(userList, '\0');//convert the userList into string
                counter++;
            }
            for(unsigned int i = 0; i<userList.size();i++){
                if(userList[i] == '\000'){
                    userList[i] = ' ';
                }
            }
            cout << "ACK " + to_string(messageOpcode) + ' ' + to_string(numOfUsers) + ' ' + userList<<endl;
        }
        if(messageOpcode == 7){//USERLIST
            myConn.getBytes(&result[4],2);
            char numOfUsersArry[2];
            numOfUsersArry[0] = result[4];
            numOfUsersArry[1] = result[5];
            unsigned short numOfUsers = bytesToShort(numOfUsersArry);
            string userList;
           // string person;
            unsigned int counter = 0;
            while(counter<numOfUsers) {
                myConn.getFrameAscii(userList, 0);//convert the userList into string
             //  userList.append(1,' ');
                counter++;
            }
            for(unsigned int i = 0; i<userList.size();i++){
                if(userList[i] == '\000'){
                    userList[i] = ' ';
                }
            }
            //myConn.getFrameAscii(userList,'\0');//convert the userList into string
            cout << "ACK " + to_string(messageOpcode) + ' ' +to_string(numOfUsers) + ' ' + userList <<endl;
        }
        if(messageOpcode == 8){//Stat
            myConn.getBytes(&result[4],2);
            char numOfPostsArray[2];
            numOfPostsArray[0] = result[4];
            numOfPostsArray[1] = result[5];
            short numOfPosts = bytesToShort(numOfPostsArray);
            myConn.getBytes(&result[6],2);
            char numOFollowersArray[2];
            numOFollowersArray[0] = result[6];
            numOFollowersArray[1] = result[7];
            short numOFollowers = bytesToShort(numOFollowersArray);
            myConn.getBytes(&result[8],2);
            char numOFollowingArray[2];
            numOFollowingArray[0] = result[8];
            numOFollowingArray[1] = result[9];
            short numOFollowing = bytesToShort(numOFollowingArray);


            cout<< "ACK "+ to_string(numOfPosts) +' '+ to_string(numOFollowers)+ ' '+to_string(numOFollowing) <<endl;
        }
    }
    if(opcode == 9){//NOTIFICATION
        myConn.getBytes(&result[2],1);//post\pm
        char postOrPmArry[1];
        postOrPmArry[0] = result[2];
        string postOrPm;
        if(postOrPmArry[0]== 0) {
            postOrPm = "PM ";
        }
        else if (postOrPmArry[0]== 1){
            postOrPm = "Public ";
        }

        string postingUser;
        myConn.getFrameAscii(postingUser,'\0');
        postingUser = postingUser.substr(0, postingUser.size()-1);
        string content;
        myConn.getFrameAscii(content,'\0');
       content = content.substr(0, content.size()-1);
        for(unsigned int i = 0; i<content.size();i++){
            if(content[i] == '\000'){
                content[i] = ' ';
            }
        }
        cout<< "NOTIFICATION " + postOrPm + postingUser + ' ' + content<<endl;
        }
    return stop;
    }



short ServerReader::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
