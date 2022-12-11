package com.example.ourtravel.retrofit;

import com.google.gson.annotations.SerializedName;

// 검색과 관련된 DTO
public class ResearchData {

    @SerializedName("research") // 유저가 검색한 검색어
    private String research;

    @SerializedName("research_date") // 검색한 날짜
    private String research_date;

    @SerializedName("status") // 검색한 날짜
    private String status;

    @SerializedName("id") // 검색어 id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResearch() {
        return research;
    }

    public void setResearch(String research) {
        this.research = research;
    }

    public String getResearch_date() {
        return research_date;
    }

    public void setResearch_date(String research_date) {
        this.research_date = research_date;
    }


    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "{" +
                "research=" + research +
                ", research_date=" + research_date +
                ", status=" + status +
                ", id=" + id +

                '}';
    }

}
