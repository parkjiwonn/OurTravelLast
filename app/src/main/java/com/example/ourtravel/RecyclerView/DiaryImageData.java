package com.example.ourtravel.RecyclerView;

import com.example.ourtravel.retrofit.DiaryData;

public class DiaryImageData {

    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DiaryImageData(DiaryData diaryData)
    {
        this.photo = diaryData.getPhoto();

    }


}
