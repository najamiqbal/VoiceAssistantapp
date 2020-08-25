package com.example.voiceassistantapp.models;

public class MyTextSaved {
    private String MyText;
    private String NameMyText;
    private String DateMyText;
    private Boolean CheckTextSaved = false;


    public MyTextSaved(String myText, String nameMyText, String dateMyText) {
        MyText = myText;
        NameMyText = nameMyText;
        DateMyText = dateMyText;
        CheckTextSaved = false;
    }

    public MyTextSaved() {
        CheckTextSaved = false;

    }

    public String getMyText() {
        return MyText;
    }

    public void setMyText(String myText) {
        MyText = myText;
    }

    public String getNameMyText() {
        return NameMyText;
    }

    public void setNameMyText(String nameMyText) {
        NameMyText = nameMyText;
    }

    public String getDateMyText() {
        return DateMyText;
    }

    public void setDateMyText(String dateMyText) {
        DateMyText = dateMyText;
    }

    public Boolean getCheckTextSaved() {
        return CheckTextSaved;
    }

    public void setCheckTextSaved(Boolean checkTextSaved) {
        CheckTextSaved = checkTextSaved;
    }
}
