package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("user_nick")
    private String user_nick;

    @SerializedName("photo")
    private String photo;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_produce")
    private String user_produce;

    @SerializedName("user_phone")
    private String user_phone;

    @SerializedName("score")
    private float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_produce() {
        return user_produce;
    }

    public void setUser_produce(String user_produce) {
        this.user_produce = user_produce;
    }


    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return  "{" +
                "photo=" + photo +
                ", user_id=" + user_id +
                ", status=" + status +
                ", user_email=" + user_email +
                ", user_nick=" + user_nick +
                ", user_produce=" + user_produce +
                ", user_phone=" + user_phone +
                ", score=" + score +
                '}';
    }
}
