package com.example.ourtravel.Search_rv;

import com.example.ourtravel.retrofit.ResearchData;

public class ComData {
    // 검색어 리사이클러뷰에 세팅해 줄 data
    private String research;
    private String date;
    private int id;


    public ComData(ResearchData researchData)
    {
        this.research = researchData.getResearch();
        this.date = researchData.getResearch_date();
        this.id = researchData.getId();
    }

    public String getResearch() {
        return research;
    }

    public void setResearch(String research) {
        this.research = research;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
