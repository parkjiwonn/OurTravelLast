package com.example.ourtravel.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.Fragment.CompanyFragment;
import com.example.ourtravel.Fragment.DiaryFragment;
import com.example.ourtravel.MainActivity;
import com.example.ourtravel.Map.MapActivity;
import com.example.ourtravel.None.NoneCompany;
import com.example.ourtravel.None.NoneDiary;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.DiaryData;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.example.ourtravel.R;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.setting.SettingActivity;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;
    final String TAG = "ProfileActivity";

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email;


    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;



    private TextView tx_nick, tx_produce, tx_company, tx_diary, tx_phone,tx_rate;
    private ImageButton btn_home, btn_diary, btn_chat, btn_map ;
    private ImageButton btn_friend, btn_alarm, btn_setting;
    private Button view_company, view_diary ;
    private ImageView profile_img, img_check,img_announce;
    RatingBar mediumRatingBar; // 동행 점수 rating bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //text view
        tx_nick = findViewById(R.id.tx_nick); // 유저 닉네임
        tx_produce = findViewById(R.id.tx_produce); // 유저 자기소개
        tx_company = findViewById(R.id.tx_company); // 동행글
        tx_diary = findViewById(R.id.tx_diary); // 일기
        tx_phone = findViewById(R.id.tx_phone); // 휴대전화 인증 완료 text
        tx_rate = findViewById(R.id.tx_rate); // 동행 점수

        //ImageButton - 밑 메뉴 이동 버튼들
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_diary = (ImageButton) findViewById(R.id.btn_diary);
        btn_chat = (ImageButton) findViewById(R.id.btn_chat);
        btn_map = (ImageButton) findViewById(R.id.btn_map);
        btn_friend = findViewById(R.id.btn_friend); // 친구 액티비티 이동 버튼
        //btn_alarm = findViewById(R.id.btn_alarm); // 알람 액티비티 이동 버튼
        btn_setting = findViewById(R.id.btn_setting);

        //imageview
        profile_img = findViewById(R.id.profile_img); // 유저 프로필 사진
        img_check = findViewById(R.id.img_check); // 전화번호 인증 마크
        img_announce = findViewById(R.id.img_announce); // 동행 점수 info

        //button
        view_company = findViewById(R.id.view_company); // 동행글 선택시 표시 선
        view_diary = findViewById(R.id.view_diary); // 일기 선택시 표시 선

        mediumRatingBar = findViewById(R.id.mediumRatingBar); // 동행 점수 남기기 rating bar

        //처음 셋팅은 동행글 선택
        //text밑에 표시
        view_company.setVisibility(View.VISIBLE); // 동행글 밑 표시 보이게 하기
        view_diary.setVisibility(View.INVISIBLE); // 일기 밑 표시 안보이게 하기


        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저 이메일

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 전화번호 인증을 진행했으니
        // 전화번호 인증을 완료했는지 확인해야 한다.

        // 유저 정보 세팅하기
        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if(response.isSuccessful())
                {
                    UserData result = response.body();
                    Log.e(TAG, "유저 정보 셋팅 onResponse: result = " + result );

                    String status = result.getStatus();
                    String nick = result.getUser_nick();
                    String produce = result.getUser_produce();
                    String photo = result.getPhoto();
                    String phone = result.getUser_phone();
                    float score = result.getScore();

                    if(status.equals("true")){

                        // 유저 닉네임, 자기소개 세팅하기
                        tx_nick.setText(nick);
                        tx_produce.setText(produce);

                        // 유저 프로필 셋팅하기
                        if(photo.equals("basic")) // 프로필 기본일때
                        {
                            profile_img.setImageResource(R.drawable.profile2);
                        }
                        else{ // 프로필 기본이미지 아닐때

                            String url = "http://3.39.168.165/userimg/" + photo;
                            //Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(ProfileActivity.this).load(url).into(profile_img);
                        }

                        if(phone.equals("0"))
                        {
                            // 전화번호 인증을 하지 않았다는 것.
                            // 전화번호 인증이 안되있다는 text set을 해줘야 함.
                            tx_phone.setVisibility(tx_phone.GONE);
                            img_check.setVisibility(img_check.GONE);
                        }
                        else{
                            // 전화 번호 인증을 했다는 것.
                            // 전화번호 인증이 되었다는 text set을 해줘야 함.
                            tx_phone.setVisibility(tx_phone.VISIBLE);
                            img_check.setVisibility(img_check.VISIBLE);

                        }

                        tx_rate.setText(String.valueOf(score) + " 점");
                        mediumRatingBar.setRating(score);



                    }
                    else
                    {
                        Log.e(TAG, "fail");
                    }
                }
                else
                {
                    Log.e(TAG, "onResponse fail");
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

        // 처음 동행글이 선택되서 동행글 프레그먼트
        FragmentView(Fragment_1);

        // 일기 선택시 일기 프레그먼트만 보이게끔
        tx_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_1);
                view_company.setVisibility(View.VISIBLE);
                view_diary.setVisibility(View.INVISIBLE);
            }
        });

        // 동행글 선택시 동행글 프레그먼트
        tx_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_2);
                view_diary.setVisibility(View.VISIBLE);
                view_company.setVisibility(View.INVISIBLE);
            }
        });

        // 현재 프로필 유저와 동행한 이력이 있는지 확인을 한 후 버튼의 생성 유무를 결정 해야 한다.

        img_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dig_announce = new AlertDialog.Builder(ProfileActivity.this);
                dig_announce.setTitle("동행 점수");
                dig_announce.setMessage("동행 일정이 끝난 후 남긴 평가를 합산해\n동행 점수가 계산됩니다.\n기본 동행점수는 2.5점 입니다.");
                dig_announce.show();
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // 다이어리 이동 버튼
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 채팅 이동 버튼
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // 설정 버튼
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        //홈 버튼
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FriendActivity.class);
                startActivity(intent);

            }
        });







    }



    private void FragmentView(int fragment){

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // 첫번 째 프래그먼트 호출 - 동행글
                // 동행글이 있다면 동행글 프레그먼트가 나오고 없다면 다른 프레그먼트를 넣기.
                retrofitInterface.listofuser(user_email).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {

                        if(response.isSuccessful() && response.body() != null){
                            List<CompanyData> result = response.body();
                            Log.e(TAG, "동행글 리스트 response body : " + result );

                            // 동행글이 없다면
                            if(result.size()==0)
                            {
                                // 동행글 없는 프레그먼트 띄우기
                                NoneCompany noneCompany = new NoneCompany();
                                transaction.replace(R.id.fragment_container, noneCompany);
                                transaction.commit();

                            }
                            // 동행글이 있다면
                            else{
                                Log.e(TAG, "FragmentView: 1" );
                                DiaryFragment fragment1 = new DiaryFragment();
                                transaction.replace(R.id.fragment_container, fragment1);
                                transaction.commit();
                            }
                        }
                        else{
                            Log.e(TAG, "동행글 리스트 onResponse is fail " );
                        }



                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {

                    }
                });


                break;

            case 2:
                // 두번 째 프래그먼트 호출 - 일기

                // 일기가 없다면 다른 프레그먼트 일기가 있다면 일기 프레그 먼트
                retrofitInterface.diaryofuser(user_email).enqueue(new Callback<List<DiaryData>>() {
                    @Override
                    public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {

                        if(response.isSuccessful() && response.body() != null){

                            List<DiaryData> result = response.body();
                            Log.e(TAG, "다이어리 리스트 onResponse: result :" +result );

                            // 다이어리가 없다면
                            if(result.size() == 0)
                            {
                                // 다이어리 없는 프래그먼트 띄우기
                                NoneDiary noneDiary = new NoneDiary();
                                transaction.replace(R.id.fragment_container, noneDiary);
                                transaction.commit();

                            }
                            // 다이어리가 있다면
                            else
                            {
                                CompanyFragment fragment2 = new CompanyFragment();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "다이어리 리스트 onResponse is fail ");
                        }

                    }

                    @Override
                    public void onFailure(Call<List<DiaryData>> call, Throwable t) {

                    }
                });

                break;
        }




    }

}