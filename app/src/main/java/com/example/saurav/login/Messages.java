package com.example.saurav.login;

/**
 * Created by Saurav on 24-03-2018.
 */

public class Messages {
    private String message,type;
    private boolean seen;
    private long time;

    public Messages(String from,String to,String msg_key) {
        this.from = from;
        this.to=to;
        this.msg_key=msg_key;
    }

    private String from;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    private String to;
    private String msg_key;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public Messages(){

    }


    public Messages(String message, String type, boolean seen, long time) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
