package com.example.ourtravel.Object;

import java.util.ArrayList;

public class Chat {

    private String now_user_email;
    private String time;
    private String content;
    private String now_user_nick;
    private String now_user_profile;
    private String chat_date;
    private String pre_date;
    private int roomNum;
    private ArrayList<String> photolist = new ArrayList<>();
    private String unitime;

    public Chat(String now_user_email, String time, String content, String now_user_nick, String now_user_profile, String chat_date, String pre_date, int roomNum, ArrayList<String> photolist, String unitime)
    {
        this.now_user_email = now_user_email;
        this.time = time;
        this.content = content;
        this.now_user_nick = now_user_nick;
        this.now_user_profile = now_user_profile;
        this.chat_date = chat_date;
        this.pre_date = pre_date;
        this.roomNum = roomNum;
        this.photolist = photolist;
        this.unitime = unitime;

    }


    public String getNow_user_email() {
        return now_user_email;
    }

    public void setNow_user_email(String now_user_email) {
        this.now_user_email = now_user_email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNow_user_nick() {
        return now_user_nick;
    }

    public void setNow_user_nick(String now_user_nick) {
        this.now_user_nick = now_user_nick;
    }

    public String getNow_user_profile() {
        return now_user_profile;
    }

    public void setNow_user_profile(String now_user_profile) {
        this.now_user_profile = now_user_profile;
    }

    public String getChat_date() {
        return chat_date;
    }

    public void setChat_date(String chat_date) {
        this.chat_date = chat_date;
    }

    public String getPre_date() {
        return pre_date;
    }

    public void setPre_date(String pre_date) {
        this.pre_date = pre_date;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public ArrayList<String> getPhotolist() {
        return photolist;
    }

    public void setPhotolist(ArrayList<String> photolist) {
        this.photolist = photolist;
    }


}
