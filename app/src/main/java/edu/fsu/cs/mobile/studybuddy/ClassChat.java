package edu.fsu.cs.mobile.studybuddy;

public class ClassChat {

    private String class_id;
    private String class_name;
    private String student_name;
    private String active;

    public ClassChat(){

    }

    public ClassChat(String class_id, String class_name, String student_name, String active){
        this.class_id = class_id;
        this.class_name = class_name;
        this.student_name = student_name;
        this.active = active;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name =  student_name;
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
