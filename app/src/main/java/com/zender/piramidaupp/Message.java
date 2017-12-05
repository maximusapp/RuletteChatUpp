package com.zender.piramidaupp;

/**
 * Created by ОрганизациЯ on 10.10.2017.
 */

public class Message {

    private String message;
    private String type;
    private String from;


    public Message(String message, String from, String type) {
        this.message = message;
        this.from = from;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Message(){}
}
