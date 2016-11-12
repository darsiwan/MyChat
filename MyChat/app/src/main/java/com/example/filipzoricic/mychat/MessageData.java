package com.example.filipzoricic.mychat;

import android.graphics.Bitmap;

/**
 * Created by filipzoricic on 10/19/16.
 */
public class MessageData {
    String data;
    String from;
    String to;
    String type;
    int status;
    Bitmap bitmap;

    public MessageData(String data, String from, String to, int status, String type){
        this.data=data;
        this.from=from;
        this.to=to;
        this.status=status;
        this.type=type;

    }

    public MessageData(){
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getData() {
        return data;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
