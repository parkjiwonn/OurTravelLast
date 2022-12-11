package com.example.ourtravel.RecyclerView;

import com.example.ourtravel.retrofit.DiaryData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RecyclerDiaryData {

    private int id; // 게시글 id
    private String content; // 게시글 내용
    private int like ; //좋아요 수
    private int comment ; // 댓글 수
    private long date ; // 업로드 시간
    private String user_email; // 작성한 유저의 이메일
    private String nick;
    private String profile;
    private String message;


    // 뷰페이저에 들어갈 데이터 arraylist 를 선언해준다.
    private ArrayList<String> subItemList;


    // this. 현재 클래스의 인스턴스의 특정 필드를 지정할 때 사용.
    // RecyclerDiaryData 의 특정 필드들의 값을 DiaryData 객체를 가져와서 값을 지정해준다.
    public RecyclerDiaryData(DiaryData diaryData)
    {
        this.id = diaryData.getId();
        this.nick = diaryData.getNick();
        this.profile = diaryData.getProfile();
        this.content = diaryData.getContent();
        this.like = diaryData.getLike();
        this.comment = diaryData.getComment();
        this.date = diaryData.getDate();
        this.message = diaryData.getMessage();
        this.user_email = diaryData.getUser_email();
        // 하위 리사이클러뷰
        this.subItemList = diaryData.getPhoto_list();

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public ArrayList<String> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(ArrayList<String> subItemList) {
        this.subItemList = subItemList;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {

        this.date = date;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

}
