package edu.fsu.cs.mobile.studybuddy;

public class PrivateChat {

    private String friend_id;
    private String friend_name;

    public PrivateChat(){}

    public PrivateChat(String friend_id, String friend_name){
        this.friend_id = friend_id;
        this.friend_name = friend_name;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }
}
