package edu.fsu.cs.mobile.studybuddy;

public class Messages {

    private String id;
    private String libraryId;
    private String classId;
    private String senderId;
    private String message;
    private long sent;

    public Messages(String id, String libraryId, String classId,String senderID, String message, long sent){
        this.id = id;
        this.libraryId = libraryId;
        this.classId = classId;
        this.senderId = senderID;
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
