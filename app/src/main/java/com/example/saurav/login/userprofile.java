package com.example.saurav.login;

/**
 * Created by Saurav on 15-03-2018.
 */

public class userprofile {
    public String name;
    public String email;
    public String image_url;

    public userprofile(){

    }

    public userprofile(String name,String email,String image_url){
        this.name=name;
        this.email=email;
        this.image_url=image_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
