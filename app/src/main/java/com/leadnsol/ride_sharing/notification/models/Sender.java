package com.leadnsol.ride_sharing.notification.models;

public class Sender {


    private Data data;
    private String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Sender() {
    }

    public Data getData() {
        return data;
    }

    public String getTo() {
        return to;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
