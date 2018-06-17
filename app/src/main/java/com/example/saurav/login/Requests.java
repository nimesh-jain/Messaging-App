package com.example.saurav.login;

/**
 * Created by Saurav on 29-03-2018.
 */

public class Requests {
    private String Request_Type;
    public Requests(){

    }

    public Requests(String request_Type) {
        Request_Type = request_Type;
    }

    public String getRequest_Type() {
        return Request_Type;
    }

    public void setRequest_Type(String request_Type) {
        Request_Type = request_Type;
    }
}
