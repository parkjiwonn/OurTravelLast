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
    RatingBar mediumRatingBar; // ?????? ?????? rating bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //text view
        tx_nick = findViewById(R.id.tx_nick); // ?????? ?????????
        tx_produce = findViewById(R.id.tx_produce); // ?????? ????????????
        tx_company = findViewById(R.id.tx_company); // ?????????
        tx_diary = findViewById(R.id.tx_diary); // ??????
        tx_phone = findViewById(R.id.tx_phone); // ???????????? ?????? ?????? text
        tx_rate = findViewById(R.id.tx_rate); // ?????? ??????

        //ImageButton - ??? ?????? ?????? ?????????
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_diary = (ImageButton) findViewById(R.id.btn_diary);
        btn_chat = (ImageButton) findViewById(R.id.btn_chat);
        btn_map = (ImageButton) findViewById(R.id.btn_map);
        btn_friend = findViewById(R.id.btn_friend); // ?????? ???????????? ?????? ??????
        //btn_alarm = findViewById(R.id.btn_alarm); // ?????? ???????????? ?????? ??????
        btn_setting = findViewById(R.id.btn_setting);

        //imageview
        profile_img = findViewById(R.id.profile_img); // ?????? ????????? ??????
        img_check = findViewById(R.id.img_check); // ???????????? ?????? ??????
        img_announce = findViewById(R.id.img_announce); // ?????? ?????? info

        //button
        view_company = findViewById(R.id.view_company); // ????????? ????????? ?????? ???
        view_diary = findViewById(R.id.view_diary); // ?????? ????????? ?????? ???

        mediumRatingBar = findViewById(R.id.mediumRatingBar); // ?????? ?????? ????????? rating bar

        //?????? ????????? ????????? ??????
        //text?????? ??????
        view_company.setVisibility(View.VISIBLE); // ????????? ??? ?????? ????????? ??????
        view_diary.setVisibility(View.INVISIBLE); // ?????? ??? ?????? ???????????? ??????


        // shared ?????? ??????
        preferenceHelper = new PreferenceHelper(this);
        user_email = preferenceHelper.getEmail(); // ?????? ???????????? ?????? ?????????

        // ???????????? ?????? ??????
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // ???????????? ????????? ???????????????
        // ???????????? ????????? ??????????????? ???????????? ??????.

        // ?????? ?????? ????????????
        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if(response.isSuccessful())
                {
                    UserData result = response.body();
                    Log.e(TAG, "?????? ?????? ?????? onResponse: result = " + result );

                    String status = result.getStatus();
                    String nick = result.getUser_nick();
                    String produce = result.getUser_produce();
                    String photo = result.getPhoto();
                    String phone = result.getUser_phone();
                    float score = result.getScore();

                    if(status.equals("true")){

                        // ?????? ?????????, ???????????? ????????????
                        tx_nick.setText(nick);
                        tx_produce.setText(produce);

                        // ?????? ????????? ????????????
                        if(photo.equals("basic")) // ????????? ????????????
                        {
                            profile_img.setImageResource(R.drawable.profile2);
                        }
                        else{ // ????????? ??????????????? ?????????

                            String url = "http://3.39.168.165/userimg/" + photo;
                            //Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(ProfileActivity.this).load(url).into(profile_img);
                        }

                        if(phone.equals("0"))
                        {
                            // ???????????? ????????? ?????? ???????????? ???.
                            // ???????????? ????????? ??????????????? text set??? ????????? ???.
                            tx_phone.setVisibility(tx_phone.GONE);
                            img_check.setVisibility(img_check.GONE);
                        }
                        else{
                            // ?????? ?????? ????????? ????????? ???.
                            // ???????????? ????????? ???????????? text set??? ????????? ???.
                            tx_phone.setVisibility(tx_phone.VISIBLE);
                            img_check.setVisibility(img_check.VISIBLE);

                        }

                        tx_rate.setText(String.valueOf(score) + " ???");
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
                Log.e(TAG, "?????? = " + t.getMessage());
            }
        });

        // ?????? ???????????? ???????????? ????????? ???????????????
        FragmentView(Fragment_1);

        // ?????? ????????? ?????? ?????????????????? ????????????
        tx_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_1);
                view_company.setVisibility(View.VISIBLE);
                view_diary.setVisibility(View.INVISIBLE);
            }
        });

        // ????????? ????????? ????????? ???????????????
        tx_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_2);
                view_diary.setVisibility(View.VISIBLE);
                view_company.setVisibility(View.INVISIBLE);
            }
        });

        // ?????? ????????? ????????? ????????? ????????? ????????? ????????? ??? ??? ????????? ?????? ????????? ?????? ?????? ??????.

        img_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dig_announce = new AlertDialog.Builder(ProfileActivity.this);
                dig_announce.setTitle("?????? ??????");
                dig_announce.setMessage("?????? ????????? ?????? ??? ?????? ????????? ?????????\n?????? ????????? ???????????????.\n?????? ??????????????? 2.5??? ?????????.");
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


        // ???????????? ?????? ??????
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ?????? ?????? ??????
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // ?????? ??????
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        //??? ??????
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

        //FragmentTransactiom??? ????????? ?????????????????? ???????????????.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // ?????? ??? ??????????????? ?????? - ?????????
                // ???????????? ????????? ????????? ?????????????????? ????????? ????????? ?????? ?????????????????? ??????.
                retrofitInterface.listofuser(user_email).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {

                        if(response.isSuccessful() && response.body() != null){
                            List<CompanyData> result = response.body();
                            Log.e(TAG, "????????? ????????? response body : " + result );

                            // ???????????? ?????????
                            if(result.size()==0)
                            {
                                // ????????? ?????? ??????????????? ?????????
                                NoneCompany noneCompany = new NoneCompany();
                                transaction.replace(R.id.fragment_container, noneCompany);
                                transaction.commit();

                            }
                            // ???????????? ?????????
                            else{
                                Log.e(TAG, "FragmentView: 1" );
                                DiaryFragment fragment1 = new DiaryFragment();
                                transaction.replace(R.id.fragment_container, fragment1);
                                transaction.commit();
                            }
                        }
                        else{
                            Log.e(TAG, "????????? ????????? onResponse is fail " );
                        }



                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {

                    }
                });


                break;

            case 2:
                // ?????? ??? ??????????????? ?????? - ??????

                // ????????? ????????? ?????? ??????????????? ????????? ????????? ?????? ????????? ??????
                retrofitInterface.diaryofuser(user_email).enqueue(new Callback<List<DiaryData>>() {
                    @Override
                    public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {

                        if(response.isSuccessful() && response.body() != null){

                            List<DiaryData> result = response.body();
                            Log.e(TAG, "???????????? ????????? onResponse: result :" +result );

                            // ??????????????? ?????????
                            if(result.size() == 0)
                            {
                                // ???????????? ?????? ??????????????? ?????????
                                NoneDiary noneDiary = new NoneDiary();
                                transaction.replace(R.id.fragment_container, noneDiary);
                                transaction.commit();

                            }
                            // ??????????????? ?????????
                            else
                            {
                                CompanyFragment fragment2 = new CompanyFragment();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "???????????? ????????? onResponse is fail ");
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