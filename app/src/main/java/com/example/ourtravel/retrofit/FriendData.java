package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

public class FriendData {

    @SerializedName("status") // 상태 알려주는 변수
    private String status;

    @SerializedName("message") // 메세지
    private String message;

    @SerializedName("from") // 누가 친구 요청을 보냈는지
    private String from;

    @SerializedName("to") // 누구에게 친구 요청을 보냈는지
    private String to;

    @SerializedName("ok") // 친구 요청 수락 여부
    private int ok;

    @SerializedName("no") // 친구 요청 거절 여부
    private int no;

    @SerializedName("who") // 현재 로그인한 유저
    private String who;

    @SerializedName("with") // 현재 로그인한 유저와 친구인 사람
    private String with;

    @SerializedName("idApplyFriend") // 친구 요청 table id
    private int idApplyFriend;

    @SerializedName("idFriendList") // 친구 리스트 table id
    private int idFriendList;

    @SerializedName("profile") // 친구 프로필 사진
    private String profile;

    @SerializedName("nick") // 닉네임
    private String nick;


    @SerializedName("viewtype") // 뷰타입
    private int viewtype;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public int getIdApplyFriend() {
        return idApplyFriend;
    }

    public void setIdApplyFriend(int idApplyFriend) {
        this.idApplyFriend = idApplyFriend;
    }

    public int getIdFriendList() {
        return idFriendList;
    }

    public void setIdFriendList(int idFriendList) {
        this.idFriendList = idFriendList;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +

                "status=" + status +
                ", message=" + message +
                ", from=" + from +
                ", to=" + to +
                ", ok=" + ok +
                ", no=" + no +
                ", who=" + who +
                ",with =" +with+
                ", profile=" + profile +
                ", nick=" + nick +
                ", viewtype=" + viewtype +
                '}';
    }

}
