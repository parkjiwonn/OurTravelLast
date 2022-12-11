package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiaryData {

    @SerializedName("status")
    private String status;

    @SerializedName("nick")
    private String nick;


    @SerializedName("profile")
    private String profile;

    @SerializedName("message")
    private String message;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private long date;

    @SerializedName("like")
    private int like;

    @SerializedName("comment")
    private int comment;

    // diary id
    @SerializedName("id")
    private int id;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("photo")
    private String photo;

    @SerializedName("photo_list")
    private ArrayList<String> photo_list;

    public ArrayList<String> getPhoto_list() {
        return photo_list;
    }

    public void setPhoto_list(ArrayList<String> photo_list) {
        this.photo_list = photo_list;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }




    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "content=" + content +
                ", status=" + status +
                ", message=" + message +
                ", nick=" + nick +
                ", profile=" + profile +
                ", like=" + like +
                ", comment=" + comment +
                ",photo=" + photo +
                ",photo_list =" +photo_list+
                ", user_email=" + user_email +
                ", id=" + id +
                ", date=" + date +
                '}';
    }



}
