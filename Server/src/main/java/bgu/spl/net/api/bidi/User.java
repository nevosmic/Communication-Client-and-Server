package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String name;
    private String password;
    private int connectionId;
    private ConcurrentLinkedQueue<String> followers;
    private ConcurrentLinkedQueue<String> following;
    private ConcurrentLinkedQueue<String> posts;
    private ConcurrentLinkedQueue<String> Pm;
    private ConcurrentLinkedQueue<Message> watingMessages;

    public User(String username, String password){
        this.name = username;
        this.password = password;

        followers = new ConcurrentLinkedQueue<>();
        following = new ConcurrentLinkedQueue<>();
        posts = new ConcurrentLinkedQueue<>();
        Pm = new ConcurrentLinkedQueue<>();
        watingMessages = new ConcurrentLinkedQueue<>();
    }

    public String getPassword() {
        return password;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getName() {
        return name;
    }
    public void setConnectionId(int connectionId){
        this.connectionId = connectionId;
    }
    public boolean isHeFollowingMe(String username){
        return followers.contains(username);
    }

    public boolean amIFollowHim(String username){
        return following.contains(username);
    }
    public void addToFollowers(String username){
        followers.add(username);
    }
    public  void addToFollowing(String username){
        following.add(username);
    }
    public  ConcurrentLinkedQueue<String>  getFollowers(){
        return followers;
    }
    public void removeFollower(String username){
        followers.remove(username);

    }
    public void removeFromFollowing(String username){
        following.remove(username);
    }
    public ConcurrentLinkedQueue<String> getFollowers(String username){
        return followers;
    }

    public  void addToWAtingList(Message message){
        watingMessages.add(message);
    }
    public ConcurrentLinkedQueue<Message> getWaitingList(){
        return watingMessages;
    }
    public void addToPosts(String post){
        posts.add(post);
    }
    public void addToPm(String pm){
        Pm.add(pm);
    }
    public short numOfPosts(){
        return (short) posts.size();
    }
    public short numOfFollowing(){
        return (short) following.size();
    }
    public short numOfFollowers(){
        return (short) followers.size();
    }

    public void clearWaitingList(){
        watingMessages.clear();
    }
}
