package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

public class NeighborData {

    @SerializedName("status") // 상태 알려주는 변수
    private String status;

    @SerializedName("message") // 단순 메세지
    private String message;

    @SerializedName("who") // 누가
    private String who;

    @SerializedName("friend") // 누구에게 이웃신청, 서로이웃 신청을 했는지
    private String friend;

    @SerializedName("border") // 이웃신청인지 서로이웃신청인지 구분하기 위함
    private int border;

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

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "status=" + status +
                ", message=" + message +
                ", who=" + who +
                ", friend=" + friend +
                ", border=" + border +
                '}';
    }
}
