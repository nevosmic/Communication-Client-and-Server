//
// Created by nevosmic@wincs.cs.bgu.ac.il on 12/27/18.
//

#ifndef UNTITLED2_SERVERREADER_H
#define UNTITLED2_SERVERREADER_H

#include "connectionHandler.h"
using namespace std;
#include <string>
using std::string;

class ServerReader {
private:
    int* shouldT;
    ConnectionHandler &myConn;
    bool parseMsg(char* result);
    void parseError(char* answer);
    short bytesToShort(char* bytesArr);
public:
    void run();
    ServerReader(ConnectionHandler &con,  int* shouldT);

};


#endif //UNTITLED2_SERVERREADER_H
