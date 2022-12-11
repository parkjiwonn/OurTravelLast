package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

public class ChatRoomData {

    @SerializedName("status") // 데이터 확인용
    private String status;

    @SerializedName("room_id") // 채팅방 id
    private int room_id;

    @SerializedName("room_num") // 채팅방 구분 -> 동행 게시글 id
    private int room_num;

    @SerializedName("room_name") // 채팅방 이름
    private String room_name;

    @SerializedName("last_msg") // 마지막으로 보낸 메세지
    private String last_msg;

    @SerializedName("last_msgtime") // 마지막으로 메세지 보낸 시간
    private String last_msgtime;

    @SerializedName("photo_num") // 사진 갯수
    private int photo_num;



    @SerializedName("people") // 참여인원
    private int people;

    @SerializedName("made_time") // 동행글 생성 시간
    private String made_time;

    @SerializedName("manager") // 동행글 작성자
    private String manager;

    @SerializedName("photo") // 채팅방 사진
    private String photo;

    @SerializedName("completion") // 채팅방 모집 완료 여부
    private int completion;

    @SerializedName("participant") // 채팅방 참여 인원
    private int participant;

    public int getParticipant() {
        return participant;
    }

    public void setParticipant(int participant) {
        this.participant = participant;
    }

    public int getPhoto_num() {
        return photo_num;
    }

    public void setPhoto_num(int photo_num) {
        this.photo_num = photo_num;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getLast_msgtime() {
        return last_msgtime;
    }

    public void setLast_msgtime(String last_msgtime) {
        this.last_msgtime = last_msgtime;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getRoom_num() {
        return room_num;
    }

    public void setRoom_num(int room_num) {
        this.room_num = room_num;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getMade_time() {
        return made_time;
    }

    public void setMade_time(String made_time) {
        this.made_time = made_time;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "room_id=" + room_id + // row의 순수 id
                ", room_num =" + room_num + // 채팅 방 숫자 = 동행글 id
                ", room_name =" + room_name + // 채탕 방 이름 = 동행글 제목
                ", people =" + people + // 채팅 최대 인원
                ", made_time =" + made_time + // 채팅방 만들어진 시간
                ", manager =" + manager + // 채팅방 방장
                ", photo =" + photo + // 채팅방 사진 = 동행글 배너
                ", status =" + status + // 상태 여부 확인 - true, false
                ", last_msg =" + last_msg + // 마지막 메세지
                ", last_msgtime =" + last_msgtime + // 마지막 메세지 보낸 시간
                ", completion =" + completion +  // 동행 모집 완료 여부
                ",  photo_num=" + photo_num +  // 사진 갯수
                ",  participant=" + participant +  // 채팅방 참여 인원

                '}';
    }

}
