package bgu.spl.net.api.bidi;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {

    private ConcurrentHashMap<String,String> RegisterMap;
    private ConcurrentHashMap <String,User> usersMap;
    private ConcurrentLinkedQueue<String> LogedInUsers;

    public DataBase(){

        RegisterMap = new ConcurrentHashMap<>();
        usersMap = new ConcurrentHashMap<>();
        LogedInUsers = new ConcurrentLinkedQueue<>();

    }

    public boolean register(String userName, String password){
        boolean ans =false;
        //creating new user
       User myUser = new User(userName,password);//waitingMsg is created
       //adding this user to register Map
       if(RegisterMap.putIfAbsent(userName,password) == null){
           usersMap.putIfAbsent(userName,myUser); //adding this user to the users Map
           ans = true;
       }
    return ans;
    }

    public boolean isRegister(String userName){
        if(RegisterMap.get(userName)==null){
            return  false;
        }
        return RegisterMap.containsKey(userName);
    }
    public boolean isPasswordTheSame(String userName, String password){
        return RegisterMap.get(userName).equals(password);
    }

    public boolean isLoggedIn(String userName){

        return LogedInUsers.contains(userName);
    }

    public  boolean addToLoginMap(String userName) {
        boolean ans = false;
        synchronized (usersMap.get(userName)) {
            if (!LogedInUsers.contains(userName)) {
                LogedInUsers.add(userName);
                ans = true;
            }
        }
            return ans;
    }


    public void logOut(String userName){

       LogedInUsers.remove(userName);
    }

    public Vector<String> addToFollowingMap(String userName,Vector<String> newFollowing ) {//new pips to follow
        User myUser = usersMap.get(userName);
        Vector<String> successfully = new Vector<>();
        for(int i = 0;i < newFollowing.size(); i++ ){
            String currentPerson = newFollowing.get(i);
            // if this person not at the user list yet
            if(!(myUser.amIFollowHim(currentPerson))){
                //check if this person registered
                if(isRegister(currentPerson)){
                    usersMap.get(currentPerson).addToFollowers(userName);
                    //add to following list
                    myUser.addToFollowing(currentPerson);
                    successfully.add(currentPerson);
                }
            }
        }
        return successfully;

    }
    public Vector<String> removeFromFollowing(String userName, Vector<String> newUnFollowing) {
        User myUser = usersMap.get(userName);
        Vector<String> successfully = new Vector<>();
        for (int i = 0; i < newUnFollowing.size(); i++) {
            String currentPerson = newUnFollowing.get(i);
            //check if the current person is at my list
            if (myUser.amIFollowHim(currentPerson)) {
                // remove the user from the followers list
                usersMap.get(currentPerson).removeFollower(userName);
                // remove the person from my following list
                myUser.removeFromFollowing(currentPerson);
                successfully.add(currentPerson);
            }
        }
        return successfully;
    }

    public void addPost(String username, String content){
        usersMap.get(username).addToPosts(content);
    }


    public ConcurrentLinkedQueue<String> getFollowers(String username){
        return usersMap.get(username).getFollowers();
    }
    public User getUser(String username){
        if(usersMap.get(username) == null){
            return null;
        }
        return usersMap.get(username);
    }

    public Set<String> getUserList(){
        return new LinkedHashSet<>(RegisterMap.keySet());//return set of users
    }
    public int getNumOfUsers() {
        return RegisterMap.size();
    }




}
