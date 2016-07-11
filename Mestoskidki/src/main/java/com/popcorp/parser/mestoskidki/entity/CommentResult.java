package com.popcorp.parser.mestoskidki.entity;

public class CommentResult extends Result {

    private String date;
    private String time;
    private long dateTime;

    public CommentResult(boolean result, String message, String date, String time, long dateTime) {
        super(result, message);
        this.date = date;
        this.time = time;
        this.dateTime = dateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
