package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatData {

    @SerializedName("status") // 상태
    private String status;

    @SerializedName("message") // 메세지 - 아무거나
    private String message;

    @SerializedName("profile") // 수신자 프로필
    private String profile;

    @SerializedName("nick") // 수신자 닉네임
    private String nick;

    @SerializedName("photo_list")
    private ArrayList<String> photo_list;


    @SerializedName("chat_message") // 채팅 내용
    private String chat_message;

    @SerializedName("chat_time") // 채팅 시간
    private String chat_time;

    @SerializedName("viewType") // 채팅 뷰 타입
    private int viewType;

    @SerializedName("NowLoginUser") // 현재 로그인한 유저
    private String NowLoginUser;

    @SerializedName("chatUser") // 채팅 보낸 유저
    private String chatUser;

    @SerializedName("roomNum") // 채팅방
    private int roomNum;

    @SerializedName("chat_date") // 채팅 보낸 최초시간 - 년월일
    private String chat_date;



    public ChatData(String chatUser, String chat_message, String chat_time, int roomNum, String nick, String profile, String chat_date, int viewType)
    {
        this.chatUser =chatUser;
        this.chat_message =chat_message;
        this.chat_time =chat_time;
        this.roomNum = roomNum;
        this.nick = nick;
        this.profile = profile;
        this.chat_date = chat_date;
        this.viewType = viewType;

    }

    public ArrayList<String> getPhoto_list() {
        return photo_list;
    }

    public void setPhoto_list(ArrayList<String> photo_list) {
        this.photo_list = photo_list;
    }


    public String getChat_date() {
        return chat_date;
    }

    public void setChat_date(String chat_date) {
        this.chat_date = chat_date;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNowLoginUser() {
        return NowLoginUser;
    }

    public void setNowLoginUser(String nowLoginUser) {
        NowLoginUser = nowLoginUser;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chat_message) {
        this.chat_message = chat_message;
    }

    public String getChat_time() {
        return chat_time;
    }

    public void setChat_time(String chat_time) {
        this.chat_time = chat_time;
    }


    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "status =" + status + // 상태
                ", message =" + message + // 메세지 - 아무거나
                ", roomNum =" + roomNum + // 채팅방 구분
                ", profile =" + profile + // 수신자 프로필
                ", nick =" + nick + // 수신자 프로필
                ", chat_message =" + chat_message + // 채팅 내용
                ", chat_time =" + chat_time + // 채팅 시간
                ", viewType =" + viewType + // 뷰 타입
                ", NowLoginUser =" + NowLoginUser + // 현재 로그인한 유저
                ", chatUser =" + chatUser + // 채팅보낸 유저
                ", chat_date =" + chat_date + // 채팅보낸 최초시간 - 년월일일
                ", photo_list =" + photo_list + // 채팅 이미지 리스트

               '}';
    }

}
