CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system  -pthread 

all: EchoClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/KeyboardReader.o bin/ServerReader.o $(LDFLAGS) 

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/KeyboardReader.o bin/ServerReader.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
bin/KeyboardReader.o: src/KeyboardReader.cpp
	g++ $(CFLAGS) -o bin/KeyboardReader.o src/KeyboardReader.cpp
	
bin/ServerReader.o: src/ServerReader.cpp
	g++ $(CFLAGS) -o bin/ServerReader.o src/ServerReader.cpp
	
.PHONY: clean
clean:
	rm -f bin/*


