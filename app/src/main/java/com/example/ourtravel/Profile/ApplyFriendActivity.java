package com.example.ourtravel.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.FriendData;
import com.example.ourtravel.retrofit.NeighborData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyFriendActivity extends AppCompatActivity {

    private TextView tx_cancle ,tx_apply, tx_nick;
    private ImageView img;

    // 이웃 신청인지 서로 이웃 신청인지 체크하는 라디오 버튼 그룹
    RadioGroup radioGroup;

    int apply; // 이웃신청했는지 서로이웃 신청했는지 구분하기 위한 정수
    public String nick;



    private PreferenceHelper preferenceHelper;
    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String now_user_email; // 현재 로그인한 유저 이메일
    String user_email; // 이웃 신청하려는 유저의 이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_friend);


        // 인텐트 선언부
        Intent intent = getIntent();

        // 프로필 유저 이메일 받아오기
        user_email = (String)intent.getExtras().get("user_email");

        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저 이메일

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // text view
        tx_cancle = findViewById(R.id.tx_cancle); // 신청 취소
        tx_apply = findViewById(R.id.tx_apply); // 이웃 신청
        tx_nick = findViewById(R.id.tx_nick); // 이웃 신청할 유저 닉네임

        // image view
        img = findViewById(R.id.img); // 이웃 신청할 유저 프로필 사진

        // 일기 공개범위 정하는 라디오버튼 그룹
        radioGroup = findViewById(R.id.radio_group);

        // 이웃 신청하려는 유저의 정보 가져오기
        retrofitInterface.SetUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    UserData result = response.body();
                    Log.e(TAG, "onResponse: result =" + result );
                    nick = result.getUser_nick();
                    String photo = result.getPhoto();

                    tx_nick.setText(nick + " 님 ");

                    // 유저 프로필 셋팅하기
                    if(photo.equals("basic")) // 프로필 기본일때
                    {
                        img.setImageResource(R.drawable.profile2);
                    }
                    else{ // 프로필 기본이미지 아닐때

                        String url = "http://3.39.168.165/userimg/" + photo;
                        //Log.e(TAG, "onResponse: url : " + url );
                        Glide.with(ApplyFriendActivity.this).load(url).into(img);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

        // apply = 0 그냥 이웃
        // apply = 1 서로 이웃

        // 이웃인지 서로이웃인지 정하기
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.radio_button_neighbor:
                        apply = 0;
                        break;
                    case R.id.radio_button_bf:
                        apply = 1;
                        break;


                }
            }
        });

        // 이웃 신청 확인 버튼 클릭 이벤트
        tx_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 단순 이웃 추가인지 , 서로이웃 신청인지 먼저 확인해야 한다.
                if(apply == 0)
                {
                    // 단순히 이웃 추가를 누른 상태
                    // 바로 친구 리스트에 들어가야 한다.
                    // border = 0 그냥 이웃
                    // border = 1 서로 이웃인 상태
                    retrofitInterface.ApplyNeighbor(now_user_email, user_email, 0).enqueue(new Callback<NeighborData>() {
                        @Override
                        public void onResponse(Call<NeighborData> call, Response<NeighborData> response) {

                            if(response.isSuccessful() && response.body() != null)
                            {
                                NeighborData result = response.body();
                                Log.e(TAG, "onResponse: 단순 이웃 추가 result = " + result );

                                showSuccessApply();
                            }
                        }

                        @Override
                        public void onFailure(Call<NeighborData> call, Throwable t) {

                        }
                    });


                }
                else {
                    // 서로 이웃 신청을 누른 상태
                    // 친구 요청으로 들어가야 한다.
                    retrofitInterface.ApplyFriend(now_user_email, user_email).enqueue(new Callback<FriendData>() {
                        @Override
                        public void onResponse(Call<FriendData> call, Response<FriendData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                FriendData result = response.body();
                                Log.e(TAG, "onResponse: 서로 이웃 신청 result = " + result );
                                showApplyBf();

                            }
                        }

                        @Override
                        public void onFailure(Call<FriendData> call, Throwable t) {

                        }
                    });

                }
            }
        });

        // 취소 버튼 누름 -> 이전 유저의 프로필로 다시 돌아가야 한다.
        tx_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 유저의 프로필로 이동
                Intent intent = new Intent(ApplyFriendActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("user_email", user_email); // 인텐트로 유저의 이메일을 다시 보내줘야 한다.
                startActivity(intent);
                finish();

            }
        });



    }

    // 서로이웃 요청에 성공했다는 뜻
    private void showApplyBf() {
        AlertDialog.Builder dig_apply = new AlertDialog.Builder(ApplyFriendActivity.this);
        dig_apply.setMessage(nick + " 님에게 서로이웃 신청을 했습니다.");
        dig_apply.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 이전 유저의 프로필로 이동
                Intent intent = new Intent(ApplyFriendActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("user_email", user_email); // 인텐트로 유저의 이메일을 다시 보내줘야 한다.
                startActivity(intent);
                finish();
            }
        });
        dig_apply.show();
    }

    // 단순 이웃 추가 성공 했을 때 나오는 다이얼로그
    private void showSuccessApply() {

        AlertDialog.Builder dig_success = new AlertDialog.Builder(ApplyFriendActivity.this);
        dig_success.setMessage(nick + " 님을 이웃으로 추가하였습니다.");
        dig_success.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 이전 유저의 프로필로 이동
                Intent intent = new Intent(ApplyFriendActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("user_email", user_email); // 인텐트로 유저의 이메일을 다시 보내줘야 한다.
                startActivity(intent);
                finish();
            }
        });
        dig_success.show();
    }
}