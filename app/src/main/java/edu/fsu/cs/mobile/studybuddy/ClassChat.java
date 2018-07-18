package edu.fsu.cs.mobile.studybuddy;

public class ClassChat {

    private String class_id;
    private String class_name;

    public ClassChat(){

    }

    public ClassChat(String class_id, String class_name){
        this.class_id = class_id;
        this.class_name = class_name;
    }

    public String getID() {
        return class_id;
    }

    public String getName() {
        return class_name;
    }

    public void setID(String lib_id) {
        this.class_id = lib_id;
    }

    public void setName(String lib_name) {
        this.class_name = lib_name;
    }
}
