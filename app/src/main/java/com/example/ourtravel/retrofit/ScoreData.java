package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// 동행 점수 남기 위한 리스트를 리사이클러뷰에 세팅하기 전
// 서버에서 동행글 정보와 유저의 점수를 같이 받아와야 하는데 그때 필요한 DTO
public class ScoreData {

    @SerializedName("status")
    private String status;

    @SerializedName("room_array")
    private ArrayList<Integer> room_array;

    @SerializedName("message")
    private int message;

    @SerializedName("title")
    private String title;

    @SerializedName("start")
    private String start;

    @SerializedName("finish")
    private String finish;

    @SerializedName("score")
    private Float score;

    @SerializedName("room_num")
    private int room_num;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public int getRoom_num() {
        return room_num;
    }

    public void setRoom_num(int room_num) {
        this.room_num = room_num;
    }


    public ArrayList<Integer> getRoom_array() {
        return room_array;
    }

    public void setRoom_array(ArrayList<Integer> room_array) {
        this.room_array = room_array;
    }


    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return  "{" +
                "status=" + status +
                ", message=" + message +
                ", title=" + title +
                ", start=" + start +
                ", finish=" + finish +
                ", score=" + score +
                ", room_num=" + room_num +
                ", room_array=" + room_array +

                '}';
    }
}
