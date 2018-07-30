package edu.fsu.cs.mobile.studybuddy;

public class Messages {

    private String id;
    private String libraryId;
    private String className;
    private String senderId;
    private String senderName;
    private String message;
    private long sent;

    public Messages(String id, String libraryId, String className,String senderID, String senderName, String message, long sent){
        this.id = id;
        this.libraryId = libraryId;
        this.className = className;
        this.senderId = senderID;
        this.senderName = senderName;
        this.message = message;
        this.sent = sent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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
