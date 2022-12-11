package com.example.ourtravel.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.Map.MapActivity;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.DiaryAdpater;
import com.example.ourtravel.RecyclerView.RecyclerDiaryData;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.home.RcompanyActivity;
import com.example.ourtravel.retrofit.DiaryData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.setting.SettingActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    private FloatingActionButton btn_write;
    private ImageButton btn_home , btn_chat ,btn_profile, btn_map ;

    Spinner spinnerMenu;


    RecyclerView recyclerView = null; //다이어리 리사이클러뷰
    DiaryAdpater diaryAdpater = null; //다이어리 어댑터
    ArrayList<RecyclerDiaryData> diaryDataArrayList; // 다이어리 데이터 arraylist


    private PreferenceHelper preferenceHelper; // 현재 로그인한 유저 정보 담고있는 shared
    String now_user_email; // 현재 로그인한 유저의 이메일

    private RetrofitClient retrofitClient; // 레트로핏 client
    private RetrofitInterface retrofitInterface; // 레트로핏 interface

    boolean status;
    int diary_filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // 액티비티에서 필요한 버튼들
        btn_home =findViewById(R.id.btn_home);
        btn_profile = findViewById(R.id.btn_profile);
        btn_chat = findViewById(R.id.btn_chat);
        btn_map = findViewById(R.id.btn_map);

        btn_write = findViewById(R.id.btn_write); // 일기 작성 버튼


        // 스피터
        spinnerMenu = (Spinner)findViewById(R.id.spinner);

        final String[] filter = getResources().getStringArray(R.array.my_array);
        ArrayAdapter menuAdapter = ArrayAdapter.createFromResource(this, R.array.my_array, android.R.layout.simple_spinner_item);
        // Spinner 클릭시 DropDown 모양을 설정
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 스피너에 어댑터를 연결
        spinnerMenu.setAdapter(menuAdapter);



        recyclerView = findViewById(R.id.DiaryRecyclerView); // 현재 액티비티 레이아웃에 있는 리사이클러뷰 매칭
        // 리사이클러뷰에 표시할 데이터 리스트 생성하기.
        diaryDataArrayList = new ArrayList<>(); // arraylist 생성
        // 리사이클러뷰에 diaryadapter 객체 지정해주기.
        diaryAdpater = new DiaryAdpater(diaryDataArrayList, getApplicationContext()); // 어댑터에 arraylist 넣기
        recyclerView.setAdapter(diaryAdpater); // 리사이클러뷰에 어댑터 넣기
        // 리사이클러뷰에 linearlayoutmanager 객체 지정해주기.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)); // 리사이클러뷰 레이아웃 매니저로 수직으로 설정하기.



        preferenceHelper = new PreferenceHelper(this); // shared 객체 만들기
        now_user_email = preferenceHelper.getEmail(); // shared에서 이메일 가져오기.

        retrofitClient = RetrofitClient.getInstance(); // 레트로핏 client 인스턴스
        retrofitInterface = RetrofitClient.getRetrofitInterface(); // 레트로핏 인터페이스 가져오기




        // 다이어리 리스트 가져오기.
        retrofitInterface.diarylist(now_user_email).enqueue(new Callback<List<DiaryData>>() {
            @Override
            public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        List<DiaryData> result = response.body();
                        Log.e(TAG, " response body : " + result );

                        for (int i =0; i <result.size(); i++)
                        {
                            DiaryData diaryData = result.get(i);
                            //상위 리사이클러뷰 아이템에 담을 데이터들
                            RecyclerDiaryData recyclerDiaryData = new RecyclerDiaryData(diaryData);
                            Log.e(TAG, "onResponse: recyclerDiaryData :" + recyclerDiaryData );
                            // DTO 데이터 담는 클래스 , Interface, client //
                            // 하위 리사이클러뷰 아이템에 담을 데이터들
                            // 사진 리스트 들이 들어가요.
                            // 레트로핏 인터셉터 넣어서 데이터 어떻게 받아오는지확인할수있다.

                            diaryDataArrayList.add(recyclerDiaryData);
                        }

                        diaryAdpater.notifyDataSetChanged();

                    }
                    else
                    {
                        Log.e(TAG, " response body : 리스트 불러오기 fail " );


                    }
            }

            @Override
            public void onFailure(Call<List<DiaryData>> call, Throwable t) {
                Log.e("onresponse", "에러 다이어리 리스트 : " + t.getMessage());
            }
        });


        diaryAdpater.setOnItemClickListener(new DiaryAdpater.OnItemClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {
                // 좋아요 버튼 눌렀을 때.
                // 여기서 서버 통신으로 좋아요 테이블에서 추가된 정황이 있는데 한번 더 누른거면 좋아요 감소
                // 추가한 정황이 있다면 좋아요 버튼이 selected된 상태여야 함.어떻게?

                if(diaryDataArrayList.get(pos).getMessage().equals("true"))
                {
                    //이미 선택되있다는 의미, 근데 한번 더 누른거니까 좋아요 취소가 되야함.
                    status = false;
                    //좋아요 수 감소해야함.
                    int cnt = diaryDataArrayList.get(pos).getLike();
                    cnt--;
                    Log.e(TAG, "onLikeClick: cnt :" + cnt );
                    diaryDataArrayList.get(pos).setLike(cnt);
                    diaryDataArrayList.get(pos).setMessage("false");
                    diaryAdpater.notifyDataSetChanged();

                    retrofitInterface.clicklike(cnt, diaryDataArrayList.get(pos).getId(), now_user_email, status).enqueue(new Callback<DiaryData>() {
                        @Override
                        public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                DiaryData result = response.body();
                                Log.e(TAG, "onResponse: 좋아요 해제시 result : "+result );
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 좋아요 해제 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<DiaryData> call, Throwable t) {
                            Log.e("onresponse", "에러 좋아요 해제시 서버통신 : " + t.getMessage());
                        }
                    });

                }
                else
                {
                    status = true;
                    //유저가 선택하지 않았다는 의미
                    // 추가한 정황이 없는데 한번 더 누른거면 좋아요 증가되게 해야함.
                    int like_cnt = diaryDataArrayList.get(pos).getLike();
                    like_cnt++;
                    Log.e(TAG, "onLikeClick: likecnt :" + like_cnt );

                    diaryDataArrayList.get(pos).setLike(like_cnt);
                    diaryDataArrayList.get(pos).setMessage("true");
                    diaryAdpater.notifyDataSetChanged();

                    // 좋아요 선택시 증가된 좋아요 갯수 db에 저장하기.
                    retrofitInterface.clicklike(like_cnt, diaryDataArrayList.get(pos).getId(), now_user_email, status).enqueue(new Callback<DiaryData>() {
                        @Override
                        public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                DiaryData result = response.body();
                                Log.e(TAG, "onResponse: like response :"+ result );
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: like response fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<DiaryData> call, Throwable t) {
                            Log.e("onresponse", "에러 좋아요 선택시 서버통신 : " + t.getMessage());
                        }
                    });
                }



            }

            @Override
            public void onImgClick(View v, int pos) {

                String user_email = diaryDataArrayList.get(pos).getUser_email(); // 다이어리 작성한 유저 이메일

                // 프로필 사진 누른 사람과 해당 다이어리 작성자가 같다면 현재 로그인한 사람의 프로필로 이동
                if(user_email.equals(now_user_email))
                {
                    Intent intent = new Intent(DiaryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                // 아니라면 다른 사람의 프로필로 이동.
                else{
                    Intent intent = new Intent(DiaryActivity.this, OtherUserProfileActivity.class);
                    intent.putExtra("user_email", user_email);
                    startActivity(intent);

                }

            }
        });

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView)parent.getChildAt(0)).setTextSize(18);

                if(filter[position].equals("전체"))
                {
                    // 전체 선택
                    // 전체 다이어리 리스트 가져오기.
                    retrofitInterface.diarylist(now_user_email).enqueue(new Callback<List<DiaryData>>() {
                        @Override
                        public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                List<DiaryData> result = response.body();
                                Log.e(TAG, " response body : " + result );

                                for (int i =0; i <result.size(); i++)
                                {
                                    DiaryData diaryData = result.get(i);
                                    //상위 리사이클러뷰 아이템에 담을 데이터들
                                    RecyclerDiaryData recyclerDiaryData = new RecyclerDiaryData(diaryData);
                                    Log.e(TAG, "onResponse: recyclerDiaryData :" + recyclerDiaryData );
                                    // DTO 데이터 담는 클래스 , Interface, client //
                                    // 하위 리사이클러뷰 아이템에 담을 데이터들
                                    // 사진 리스트 들이 들어가요.
                                    // 레트로핏 인터셉터 넣어서 데이터 어떻게 받아오는지확인할수있다.

                                    diaryDataArrayList.add(recyclerDiaryData);
                                }

                                diaryAdpater.notifyDataSetChanged();

                            }
                            else
                            {
                                Log.e(TAG, " response body : 리스트 불러오기 fail " );


                            }
                        }

                        @Override
                        public void onFailure(Call<List<DiaryData>> call, Throwable t) {
                            Log.e("onresponse", "에러 다이어리 리스트 : " + t.getMessage());
                        }
                    });

                }
                else if (filter[position].equals("이웃의 일기"))
                {
                    // 이웃의 일기 선택

                    // 이웃 일기만 갖고 오겠다는 뜻
                    retrofitInterface.FriendDiaryList(now_user_email).enqueue(new Callback<List<DiaryData>>() {
                        @Override
                        public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {

                            Log.e(TAG, "onResponse: 다이어리 원래 size =" + diaryDataArrayList.size() );
                            diaryDataArrayList.clear();


                            List<DiaryData> result = response.body();
                            Log.e(TAG, "이웃 리스트 onResponse: result = " + result );

                            for (int i =0; i <result.size(); i++)
                            {
                                DiaryData diaryData = result.get(i);
                                //상위 리사이클러뷰 아이템에 담을 데이터들
                                RecyclerDiaryData recyclerDiaryData = new RecyclerDiaryData(diaryData);
                                Log.e(TAG, "onResponse: recyclerDiaryData :" + recyclerDiaryData );
                                // DTO 데이터 담는 클래스 , Interface, client //
                                // 하위 리사이클러뷰 아이템에 담을 데이터들
                                // 사진 리스트 들이 들어가요.
                                // 레트로핏 인터셉터 넣어서 데이터 어떻게 받아오는지확인할수있다.

                                diaryDataArrayList.add(recyclerDiaryData);

                            }

                            Log.e(TAG, "onResponse: result.size = " + result.size() );
                            Log.e(TAG, "onResponse: 다이어리 사이즈 = " + diaryDataArrayList.size() );
                            Log.e(TAG, "onResponse: diaryDataArrayList =" + diaryDataArrayList );

                            diaryAdpater = null;
                            diaryAdpater = new DiaryAdpater(diaryDataArrayList, DiaryActivity.this);
                            recyclerView.setAdapter(diaryAdpater);
                            diaryAdpater.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<List<DiaryData>> call, Throwable t) {

                        }
                    });

                }
                else
                {
                    // 서로 이웃의 일기 선택
                    retrofitInterface.BestFriendDiaryList(now_user_email).enqueue(new Callback<List<DiaryData>>() {
                        @Override
                        public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {
                            Log.e(TAG, "onResponse: 다이어리 원래 size =" + diaryDataArrayList.size() );
                            diaryDataArrayList.clear();


                            List<DiaryData> result = response.body();
                            Log.e(TAG, "이웃 리스트 onResponse: result = " + result );

                            for (int i =0; i <result.size(); i++)
                            {
                                DiaryData diaryData = result.get(i);
                                //상위 리사이클러뷰 아이템에 담을 데이터들
                                RecyclerDiaryData recyclerDiaryData = new RecyclerDiaryData(diaryData);
                                Log.e(TAG, "onResponse: recyclerDiaryData :" + recyclerDiaryData );
                                // DTO 데이터 담는 클래스 , Interface, client //
                                // 하위 리사이클러뷰 아이템에 담을 데이터들
                                // 사진 리스트 들이 들어가요.
                                // 레트로핏 인터셉터 넣어서 데이터 어떻게 받아오는지확인할수있다.

                                diaryDataArrayList.add(recyclerDiaryData);

                            }

                            Log.e(TAG, "onResponse: result.size = " + result.size() );
                            Log.e(TAG, "onResponse: 다이어리 사이즈 = " + diaryDataArrayList.size() );
                            Log.e(TAG, "onResponse: diaryDataArrayList =" + diaryDataArrayList );

                            diaryAdpater = null;
                            diaryAdpater = new DiaryAdpater(diaryDataArrayList, DiaryActivity.this);
                            recyclerView.setAdapter(diaryAdpater);
                            diaryAdpater.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<List<DiaryData>> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 채팅 액티비티로 이동하는 버튼
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 지도 액티비티로 이동
                Intent intent = new Intent(DiaryActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        // 프로필 액티비티로 이동하는 버튼
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 홈 액티비티로 이동하는 버튼
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 일기 작성하는 액티비티로 이동하는 버튼
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryActivity.this, WdiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}