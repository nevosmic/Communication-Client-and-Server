//
// Created by nevosmic@wincs.cs.bgu.ac.il on 12/27/18.
//

#include "../include/KeyboardReader.h"
#include <stdlib.h>

using namespace std;
using std::string;
using std::cin;

//constructor
KeyboardReader ::KeyboardReader(ConnectionHandler &conn, int *shouldT):shouldT(shouldT),myConn(conn) {}//maybe dont need myConn

void KeyboardReader :: parseMsg(char* result, string msg){
//finding the opcode
    unsigned int indexOfGAP = msg.find_first_of(' ');
    string tempMsg = msg.substr(0,indexOfGAP);
    msg = msg.substr(indexOfGAP+1);//cut the first word from the string
    if(tempMsg == "REGISTER"){
         parseRegister(result,msg);
    }
    if(tempMsg == "LOGIN"){
       parseLogIn(result,msg);
    }
    if(tempMsg == "LOGOUT"){
         parseLogOut(result,msg);
    }
    if(tempMsg == "FOLLOW"){
         parseFollow(result,msg);
    }
    if(tempMsg == "POST"){
        parsePost(result,msg);
    }
    if(tempMsg == "PM"){
         parsePM(result,msg);
    }
    if(tempMsg == "USERLIST"){
       parseUserList(result,msg);
    }
    if(tempMsg == "STAT"){
       parseStat(result,msg);
    }

}

int KeyboardReader ::whatIsMySize(string msg) {
    //finding the opcode
    if(msg == "LOGOUT" || msg == "USERLIST"){
        return 2;
    }
    int indexOfSpace = msg.find_first_of(' ');
    string tempMsg = msg.substr(0, indexOfSpace);
   msg = msg.substr(indexOfSpace+1,msg.size());
    if(tempMsg == "REGISTER"){
        return msg.size()+3;
    }
    if(tempMsg == "LOGIN"){
        return msg.size()+3;
    }

    if(tempMsg == "FOLLOW"){
        unsigned int indexOfGap = msg.find_first_of(' ');
       msg = msg.substr(indexOfGap+1,msg.size());
        unsigned int indexOfNextGap = msg.find_first_of(' ');
        msg = msg.substr(indexOfNextGap+1, msg.size());//now the msg contain only the user list

        return msg.size()+6 ;//2- for opcode, 1 - for follow\un 2 - for numOfUsers, 1 - FOR THE LAST ZERO
    }
    if(tempMsg == "POST"){
        return msg.size()+3;
    }
    if(tempMsg == "PM"){
        return msg.size()+3;
    }

    if(tempMsg == "STAT"){
        return msg.size()+3;
    }
}

void KeyboardReader:: parseRegister (char* result, string content){
    char opcode[2];
    shortToBytes(1,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];

    for(unsigned int  i = 0; i<content.size(); i++){
        if(content[i] == ' '){//separated between the username and password
            result[i+2] = '\0';
        }
        else{
            result[i+2] = content[i];
        }
    }
    result[content.size()+2]='\0';

}
void KeyboardReader:: parseLogIn(char* result, string content){
    char opcode[2];
    shortToBytes(2,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];


    for(unsigned int  i = 0; i<content.size(); i++){
        if(content[i] == ' '){//separated between the username and password
            result[i+2] = '\0';
        }
        else{
            result[i+2] = content[i];
        }

    }
    result[content.size()+2]='\0';

}

void KeyboardReader:: parseLogOut(char* result, string content){
    char opcode[2];
    shortToBytes(3,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];
    *shouldT = 0;

}


void KeyboardReader::parseFollow(char* result,string content) {

    char opcode[2];//array for the opcode
    shortToBytes(4,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];

    //follow or unfollow
    result[2] = content[0]-'0';//content[0] == 48/49

    content = content.substr(2,content.size());//cut the follow\unfollow and the gap byetes
    int indexOfGap = content.find_first_of(' ');//find the last index of the "NumOfUsers"
    string numOfUsersString = content.substr(0, indexOfGap);
    short numOfUsersShort = stoi(numOfUsersString);//convert the string to int
    char numOfUsersArry[2];
    shortToBytes(numOfUsersShort,numOfUsersArry);//convert the numOfUsers into bytes (2)
    result[3] = numOfUsersArry[0];
    result[4] = numOfUsersArry[1];
    content = content.substr(indexOfGap+1,content.size());//cut the numOfUsers from the content string

   char userListArry[content.size()];
    for(unsigned int i = 0; i<content.size(); i++){//convert the userLis into bytes arry
        if(content[i] == ' '){
            userListArry[i] = '\0';
        }
        else{
            userListArry[i]  = content[i];
        }
    }

    for(unsigned int i = 0; i < content.size();i++){
        result[i+5] = userListArry[i];
    }
    result[5+content.size()] = '\0';//the zero after the last person

}
void KeyboardReader::parsePost(char* result,string content) {
    shortToBytes(5,result);// put the opcode at the first 2 cells
    for(unsigned int i = 0; i<content.size(); i++){//convert the content of the post to the arry of char
      result[i+2] = content[i];
    }
    result[content.size()+2] = '\0';

}

void KeyboardReader::parsePM(char* result,string content) {
    char opcode[2];
    shortToBytes(6,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];
    int contentS = content.size();
    unsigned int indexOfGap = content.find_first_of(' ');
    string username = content.substr(0,indexOfGap);//save the user name in a diffrent string
    for(unsigned int i = 0; i<username.size();i++){//put the username at the arry
        result[2+i] = username[i];
    }
    result[2+username.size()] = '\0';//put a "0" after the username
    string Pm = content.substr(indexOfGap+1,content.size());//save the content of the pm
    for(unsigned int i = 0;i < Pm.size(); i++){
        result[i+3+username.size()] = Pm[i];
    }
    result[contentS+2] = '\0'; //put a zero at the last cell

}

void KeyboardReader::parseUserList(char* result,string content) {
    char opcode[2];
    shortToBytes(7,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];

}

void KeyboardReader::parseStat(char* result,string content) {
    char opcode[2];
    shortToBytes(8,opcode);
    result[0] = opcode[0];
    result[1] = opcode[1];
    for(unsigned int i = 0; i < content.size();i++){
        result[i+2] = content[i];
    }
    result[content.size()+2] = '\0';

}


void KeyboardReader:: shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
