package com.example.ourtravel.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.ourtravel.Map.MapActivity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.ChatListAdapter;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.retrofit.ChatRoomData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Main_Activity extends AppCompatActivity {

    final String TAG = "Chat_Main_Activity";
    // 채팅 메인 하단 부분 - 액티비티 이동
    private ImageButton btn_home, btn_diary, btn_profile, btn_map;

    // 유저 이메일 가져오기 위한 shared
    private PreferenceHelper preferenceHelper;
    // 레트로핏 통신 위한 레트로핏 객체들
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 리사이클러뷰
    RecyclerView recyclerView = null; // 채팅 리스트 리사이클러뷰
    ChatListAdapter chatListAdapter = null; // 채팅 리스트 어댑터
    ArrayList<ChatRoomData> chatRoomDataArrayList; // 채팅 데이터 arraylist


    // 현재 로그인한 유저의 이메일
    String user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);


        // ImageButton
        btn_home = findViewById(R.id.btn_home); // 동행글
        btn_diary = findViewById(R.id.btn_diary); // 다이어리
        btn_profile = findViewById(R.id.btn_profile); // 프로필
        btn_map = findViewById(R.id.btn_map);


        // 리사이클러뷰 객체 생성
        recyclerView = findViewById(R.id.rv_chatlist);// 현재 액티비티 레이아웃에 있는 리사이클러뷰 매칭
        // 리사이클러뷰에 표시할 데이터 리스트 생성하기.
        chatRoomDataArrayList = new ArrayList<>();// arraylist 생성
        // 리사이클러뷰에 diaryadapter 객체 지정해주기.
        chatListAdapter = new ChatListAdapter(chatRoomDataArrayList, getApplicationContext());// 어댑터에 arraylist 넣기
        recyclerView.setAdapter(chatListAdapter);// 리사이클러뷰에 어댑터 넣기
        // 리사이클러뷰에 linearlayoutmanager 객체 지정해주기.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)); // 리사이클러뷰 레이아웃 매니저로 수직으로 설정하기.

        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);

        // shared에서 현재 로그인한 유저 이메일 가져오기
        user_email = preferenceHelper.getEmail();

        // 레트로핏 통신 위한 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        Log.e(TAG, "onCreate: user_email : " + user_email );
        // 현재 로그인한 유저가 참여하고 있는 채팅방 리스트 가져오기
        retrofitInterface.chat_room_list(user_email).enqueue(new Callback<List<ChatRoomData>>() {
            @Override
            public void onResponse(Call<List<ChatRoomData>> call, Response<List<ChatRoomData>> response) {
                    if(response.isSuccessful() && response != null)
                    {
                        List<ChatRoomData> result = response.body();
                        Log.e(TAG, "onResponse: 채팅방 list result :" + result );

                        // 불러온 결과 for문으로 돌려서 arraylist에 넣기
                        for(int i=0; i < result.size(); i++)
                        {
                            chatRoomDataArrayList.add(result.get(i));
                        }

                        chatListAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onFailure(Call<List<ChatRoomData>> call, Throwable t) {

            }
        });

        // 액티비티 이동
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 지도 액티비티로 이동
                Intent intent = new Intent(Chat_Main_Activity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        // 동행글 이동
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Main_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 다이어리 이동
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Main_Activity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 프로필 이동
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Main_Activity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}