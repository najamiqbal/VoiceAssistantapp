package com.example.voiceassistantapp.utils;

public class MyDictionary {
    private String YouSay;
    private String AppUnderstand;

    public MyDictionary() {
    }

    public MyDictionary(String youSay, String appUnderstand) {
        YouSay = youSay;
        AppUnderstand = appUnderstand;
    }

    public String getYouSay() {
        return YouSay;
    }

    public void setYouSay(String youSay) {
        YouSay = youSay;
    }

    public String getAppUnderstand() {
        return AppUnderstand;
    }

    public void setAppUnderstand(String appUnderstand) {
        AppUnderstand = appUnderstand;
    }
}
