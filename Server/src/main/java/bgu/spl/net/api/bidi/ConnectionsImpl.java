package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;
import java.util.Vector;

public class ConnectionsImpl <T> implements Connections <T> {
    HashMap<Integer, ConnectionHandler> myMap;


    public ConnectionsImpl (){

        myMap = new HashMap<>();
    }


    public boolean send( int connId, T msg){

        myMap.get(connId).send(msg);
        return true;


    }
    public void broadcast(T msg){
        for(ConnectionHandler<T> cH : myMap.values()){
            cH.send(msg);

        }

    }
    public void disconnect(int connectionId){
        myMap.remove(connectionId);

    }
    public void addToMap (int connId ,ConnectionHandler cH){
        myMap.putIfAbsent(connId,cH);

    }

}
