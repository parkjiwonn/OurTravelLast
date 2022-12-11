package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

public class ChatUser {
    @SerializedName("status") // 데이터 확인용
    private String status;

    @SerializedName("message") // 데이터 응답 확인용
    private String message;

    @SerializedName("confirm") // 동행 참여인원 & 모집 인원 비교 후 결과
    private String confirm;


    @SerializedName("user_email") // 채팅 참여중인 유저 이메일
    private String user_email;

    @SerializedName("roomNum") // 채팅방 구분 숫자
    private int roomNum;

    @SerializedName("profile") // 유저 프로필
    private String profile;

    @SerializedName("nick") // 유저 닉네임
    private String nick;

    @SerializedName("manager") // 채팅방 방장
    private String manager;

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }


    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
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

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", message =" + message +
                ", user_email =" + user_email +
                ", roomNum =" + roomNum +
                ", profile =" + profile +
                ", nick =" + nick +
                ", manager =" + manager +
                ", confirm =" + confirm +

                '}';
    }
}
