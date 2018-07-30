package edu.fsu.cs.mobile.studybuddy;

public class PrivateMessage {

    private String senderId;
    private String receiverId;
    private String message;
    private long sent;

    public PrivateMessage(String senderId, String receiverId, String message, long sent){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.sent = sent;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }
}
