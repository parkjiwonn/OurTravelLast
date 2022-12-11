package com.example.ourtravel.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcountActivity extends AppCompatActivity {

    final String TAG = "AcountActivity";

    private ImageButton btn_chpass, btn_back ; // 비밀번호 변경버튼, 뒤로가기 버튼
    private Button btn_profile, btn_certify; // 프로필 설정 버튼, 전화번호 인증 버튼
    private TextView tx_nick, tx_email,tx_phone; // 닉네임, 이메일, 전화번호
    private ImageView img; // 유저 프로필

    //==============쉐어드, 레트로핏 객체들===================
    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email; // 현재 로그인한 유저 이메일


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        //text_view
        tx_email = findViewById(R.id.tx_email); // 이메일
        tx_nick = findViewById(R.id.tx_nick); // 닉네임
        tx_phone = findViewById(R.id.tx_phone); // 전화 번호

        //button
        btn_profile = findViewById(R.id.btn_profile); // 프로필로 이동 버튼
        btn_back = findViewById(R.id.btn_back); // 뒤로가기 버튼
        btn_chpass = findViewById(R.id.btn_chpass); // 비밀번호 변경 버튼
        btn_certify = findViewById(R.id.btn_certify); // 전화번호 인증 버튼

        //image view
        img = findViewById(R.id.img);

        // 쉐어드 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        // 현재 로그인한 유저 이메일 쉐어드에서 꺼내오기
        user_email = preferenceHelper.getEmail();

        // 레트로핏 객체
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 유저 정보 셋팅하기 위한 서버 통신
        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful())
                {
                    // userdata 객체 = 응답 body
                    UserData result = response.body();

                    // 변수들에 응답 데이터 set 해주기
                    String status = result.getStatus();
                    String user_nick = result.getUser_nick();
                    String user_mail = result.getUser_email();
                    String photo = result.getPhoto();
                    String phone = result.getUser_phone();

                    if (status.equals("true"))
                    {
                        tx_nick.setText(user_nick); // 유저 닉네임
                        tx_email.setText(user_mail); // 유저 이메일

                        // 유저가 전화번호 인증하지 않은경우과 인증한 경우를 나눠야 한다.
                        if(phone.equals("0"))
                        {
                            tx_phone.setText("");
                        }
                        else{
                            tx_phone.setText(phone); // 유저 전화번호
                            btn_certify.setText("인증완료");
                            btn_certify.setTextColor(Color.parseColor("#FFF4F1FB"));
                            btn_certify.setEnabled(false);
                        }




                        if(photo.equals("basic"))
                        {
                            img.setImageResource(R.drawable.profile2); // 유저 프로필 기본 이미지
                        }
                        else{

                            String url = "http://3.39.168.165/userimg/" + photo;
                            Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(AcountActivity.this).load(url).into(img);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "fail");
                    }


                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

        // 전화번호 인증하기 버튼튼
        btn_certify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 폰 번호 인증하는 액티비티로 이동
                Intent intent = new Intent(AcountActivity.this, CertifyPhoneActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // 설정으로 이동하는 버튼
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AcountActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcountActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // 비밀번호 변경
        btn_chpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcountActivity.this, ChangePassActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}