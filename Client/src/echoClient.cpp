#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/KeyboardReader.h"
#include "../include/ServerReader.h"
#include <thread>
using std::string;
using namespace std;

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    int* shouldTerminate = new int(1);

    KeyboardReader kr (connectionHandler,shouldTerminate);
    ServerReader sr(connectionHandler,shouldTerminate);


    // thread that read from socket
    thread socketReader(&ServerReader::run,&sr);

    //From here we will see the rest of the ehco client implementation:
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
		std::string line(buf);
		int len=line.length();


        unsigned int mySize = kr.whatIsMySize(line);
        char msg[mySize];
        kr.parseMsg(msg,line);
        if (!connectionHandler.sendBytes(msg,mySize)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        while (*shouldTerminate == 0){//the user type logOut(busy wait)

        }

        if(*shouldTerminate == -1 ){//the server return ackLogout
            break;
        }

		// connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.


    }
    socketReader.join();// make sure the socketReader will terminate before the main Thread

    //prevent memory leak
    delete shouldTerminate;
    return 0;
}
