package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CompanyData {
    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("id")
    private int id;

    @SerializedName("mainpos")
    private int mainpos;

    @SerializedName("subpos")
    private int subpos;

    @SerializedName("upload_date")
    private String upload_date;

    @SerializedName("completion")
    private int completion;


    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("time") // 채팅방 생성되었습니다 - 시간
    private String time;


    @SerializedName("photo")
    private String photo;

    @SerializedName("type")
    private String type;

    @SerializedName("startdate")
    private String startdate;

    @SerializedName("finishdate")
    private String finishdate;

    @SerializedName("mainspot")
    private String mainspot;



    @SerializedName("company")
    private String company;


    @SerializedName("subspot")
    private String subspot;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("attend")
    private String attend;


    @SerializedName("room_array")
    private ArrayList<Integer> room_array;

    public ArrayList<Integer> getRoom_array() {
        return room_array;
    }

    public void setRoom_array(ArrayList<Integer> room_array) {
        this.room_array = room_array;
    }

    public CompanyData(String bannerurl , String title , String startdate , String finishdate , String mainspot , String subspot , String people, String type , String content, String upload_date )
    {
        this.photo  =bannerurl ;
        this.title  =title ;
        this.startdate  =startdate ;
        this.finishdate = finishdate;
        this.mainspot = mainspot;
        this.subspot  = subspot;
        this.company = people;
        this.type  = type ;
        this.content  = content ;
        this.upload_date  = upload_date ;

    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }


    public String getAttend() {
        return attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMainpos() {
        return mainpos;
    }

    public void setMainpos(int mainpos) {
        this.mainpos = mainpos;
    }

    public int getSubpos() {
        return subpos;
    }

    public void setSubpos(int subpos) {
        this.subpos = subpos;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getFinishdate() {
        return finishdate;
    }

    public void setFinishdate(String finishdate) {
        this.finishdate = finishdate;
    }

    public String getMainspot() {
        return mainspot;
    }

    public void setMainspot(String mainspot) {
        this.mainspot = mainspot;
    }

    public String getSubspot() {
        return subspot;
    }

    public void setSubspot(String subspot) {
        this.subspot = subspot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "photo=" + photo +
                ", type=" + type +
                ", room_array=" + room_array +
                ", message=" + message +
                ", time=" + time +
                ", upload_date=" + upload_date +
                ", mainpos=" + mainpos +
                ", subpos=" + subpos +
                ", user_email=" + user_email +
                ", id=" + id +
                ", startdate=" + startdate +
                ", finishdate=" + finishdate +
                ", company=" + company +
                ", mainspot=" + mainspot +
                ", subspot=" + subspot +
                ", title=" + title +
                ", content=" + content +
                ", attend=" + attend +
                ", completion=" + completion +
                '}';
    }
}
