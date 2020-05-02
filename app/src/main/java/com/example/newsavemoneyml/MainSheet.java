package com.example.newsavemoneyml;

public class MainSheet {
    private String Date;
    private String Time;
    private String Head;
    private String Body;

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getHead() {
        return Head;
    }

    public String getBody() {
        return Body;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setHead(String head) {
        Head = head;
    }

    public void setBody(String body) {
        Body = body;
    }

    @Override
    public String toString() {
        return "MainSheet{" +
                "Date = " + Date +
                ", Time = " + Time +
                ", Head = " + Head +
                ", Body =" + Body+
                '}';
    }
}
