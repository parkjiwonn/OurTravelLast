package com.example.ourtravel.userinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaCas;

public class PreferenceHelper {



    private final String INTRO = "intro";
    private final String EMAIL = "email"; // 유저 이메일
    private final String NICK = "nick"; // 유저 닉네임
    private final String PROFILE = "profile"; // 유저 프로필 사진
    private final String PHONE = "phone"; // 유저 휴대폰 번호
    private final String SCORE = "score"; // 유저 동행 점수


    private SharedPreferences app_prefs;
    // 변수 선언
    private Context context;

    public PreferenceHelper(Context context)
    {
        app_prefs = context.getSharedPreferences("shared", 0);
        // 초기화
        this.context = context;
    }

    public void putIsLogin(boolean loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(INTRO, loginOrOut);
        edit.apply();
        // 저장
    }


    public void putEmail(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(EMAIL, loginOrOut);
        edit.apply();
    }

    public String getEmail()
    {
        return app_prefs.getString(EMAIL, "");
    }

    public void putNick(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(NICK, loginOrOut);
        edit.apply();
    }

    public String getNick()
    {
        return app_prefs.getString(NICK, "");
        // 불러오기.
    }

    public void putPROFILE(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PROFILE, loginOrOut);
        edit.apply();
    }

    public String getPROFILE() {
        return app_prefs.getString(PROFILE, "");
    }

    public void clear(){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.clear();
        edit.commit();
    }

    public void putPHONE(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PHONE, loginOrOut);
        edit.apply();
    }

    public String getPHONE()
    {
        return app_prefs.getString(PHONE, "");
    }

    public void putSCORE(String loginOrOut)
    {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(SCORE, loginOrOut);
        edit.apply();
    }

    public String getSCORE()
    {
        return app_prefs.getString(SCORE, "");
    }




}
