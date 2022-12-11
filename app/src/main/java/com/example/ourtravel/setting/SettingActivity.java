package com.example.ourtravel.setting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.diary.WdiaryActivity;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.userinfo.LoginActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.example.ourtravel.R;

public class SettingActivity extends AppCompatActivity {

    private TextView tx_logout, tx_account;
    private PreferenceHelper preferenceHelper;
    private ImageButton btn_home ,btn_diary , btn_chat ,btn_profile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 하단의 메뉴 버튼들
        btn_home =findViewById(R.id.btn_home);
        btn_diary = findViewById(R.id.btn_diary);
        btn_profile = findViewById(R.id.btn_profile);
        btn_chat = findViewById(R.id.btn_chat);

        // 채팅 액티비티로 이동하는 버튼
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        // 다이어리 액티비티로 이동하는 버튼
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // 동행글 액티비티로 이동하는 버튼
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 프로필 액티비티로 이동하는 버튼
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);

        //계정 / 정보 관리 - 프로필 수정 / 휴대폰 인증까지
        tx_account = findViewById(R.id.tx_account);
        tx_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingActivity.this, AcountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 로그아웃하고 로그인 액티비티로 이동.
        tx_logout = findViewById(R.id.tx_logout);
        tx_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        preferenceHelper.clear();

                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                builder.show();

            }
        });
    }
}