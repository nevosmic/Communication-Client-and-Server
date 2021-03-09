//
// Created by nevosmic@wincs.cs.bgu.ac.il on 12/27/18.
//
#include "connectionHandler.h"




#ifndef UNTITLED2_KEYBOARDREADER_H
#define UNTITLED2_KEYBOARDREADER_H
#include <string>
using std::string;


class KeyboardReader {
private:
    bool stop;
    ConnectionHandler &myConn;
    int* shouldT;


    void parseRegister(char* m,string content);
    void parseLogIn(char* m,string content);

    void parseLogOut(char* m,string content);
    void parseFollow(char* m,string content);
    void parsePost(char* m,string content);
    void parsePM(char* m,string content);
    void parseUserList(char* m,string content);
    void parseStat(char* m,string content);

    void shortToBytes(short num, char* bytesArr);

public:
    void parseMsg(char*m,string msg);
    int whatIsMySize(string msg);
    KeyboardReader(ConnectionHandler &Con, int* shouldT);

};


#endif //UNTITLED2_KEYBOARDREADER_H
