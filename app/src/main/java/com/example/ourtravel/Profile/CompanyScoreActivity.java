package com.example.ourtravel.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.text.AllCapsTransformationMethod;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ourtravel.Adapter.CompanyScoreAdapter;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.ScoreData;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.sun.mail.imap.protocol.FLAGS;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyScoreActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    //=================리사이클러뷰 관련=============
    RecyclerView recyclerView = null;
    CompanyScoreAdapter scoreAdapter = null;
    ArrayList<ScoreData> scoreDataArrayList;
    //=============================================

    private RetrofitInterface retrofitInterface; // 레트로핏 interface
    private PreferenceHelper preferenceHelper; // 쉐어드 객체

    private ImageButton btn_back; // 뒤로가기 버튼
    private Button btn_score;

    ArrayList<Integer> room_array;
    String user_email; // 나와 동행했던 유저
    String now_user_email; // 현재 로그인한 유저

    public float user_score; // 유저의 원래 점수
    public int room_num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_score);

        // 레트로핏 객체 생성
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저 이메일

        // image button
        btn_back = findViewById(R.id.btn_back);

        // button
        btn_score = findViewById(R.id.btn_score);

        //===========리사이클러뷰 관련=========================
        recyclerView = findViewById(R.id.rv_company);// 현재 액티비티 레이아웃에 있는 리사이클러뷰 매칭
        // 리사이클러뷰에 표시할 데이터 리스트 생성하기.
        scoreDataArrayList = new ArrayList<>();// arraylist 생성
        // 리사이클러뷰에 diaryadapter 객체 지정해주기.
        scoreAdapter = new CompanyScoreAdapter(scoreDataArrayList, getApplicationContext());// 어댑터에 arraylist 넣기
        recyclerView.setAdapter(scoreAdapter); // 리사이클러뷰에 어댑터 넣기
        // 리사이클러뷰에 linearlayoutmanager 객체 지정해주기.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)); // 리사이클러뷰 레이아웃 매니저로 수직으로 설정하기.

        //==================================================

        // 채팅방 정보 받아오기
        // other profile에서 인텐트로 array, user_email 보낸것 받기

        Intent intent = getIntent();
        room_array = (ArrayList<Integer>) intent.getExtras().get("room_array");
        user_email = (String) intent.getExtras().get("user_email");

        Log.e(TAG, "onCreate: room_array =" + room_array);
        Log.e(TAG, "onCreate: user_email =" + user_email);
        // intent로 array, email 잘 받아오는거 확인

        // 같이 참여했지만 동행이 종료된 동행글 리스트 불러오기
        retrofitInterface.FinishCompanyList(room_array, user_email).enqueue(new Callback<List<ScoreData>>() {
            @Override
            public void onResponse(Call<List<ScoreData>> call, Response<List<ScoreData>> response) {

                List<ScoreData> result = response.body();

                Log.e(TAG, "onResponse: result =" + result);
                // 서버로 보낸 arraylist가 php에서 잘 받은거 확인
                // 동행글 정보, 유저의 점수 잘 받은거 확인

                for (int i = 0; i < result.size(); i++) {

                    scoreDataArrayList.add(result.get(i));
                    user_score = result.get(i).getScore(); // 동행 점수 남길 유저의 원래 점수
                    room_num = result.get(i).getRoom_num();
                    scoreDataArrayList.get(i).setScore((float)0); // 각 동행의 점수는 0.0 으로 세팅한다.


                }

                scoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ScoreData>> call, Throwable t) {

            }
        });

        scoreAdapter.setOnItemChangeListerner(new CompanyScoreAdapter.OnItemChangeListerner() {

            // 별점 선택 변화 감지하는 리스너
            @Override
            public void onScoreChange(float v, int pos) {
                Log.e(TAG, "onScoreChange: 별점 변함");
                Log.e(TAG, "onScoreChange: " + pos + "의 별점 = " + v);

                scoreDataArrayList.get(pos).setScore(v); // 해당 리사이클러뷰의 점수를 별점으로 set 해두고
                CompanyScoreAdapter.rate_score = v;

                scoreDataArrayList.get(pos).setMessage(2);
                scoreAdapter.notifyDataSetChanged();


            }

            // 별점 저장 버튼
            // 제일 낮은 별점은 0.5점, 0.5점을 주는 것이 제일 낮은 점수를 주는 것.
            // 별점을 누르고 확인 버튼을 눌러야 별점이 저장되는 형식으로 진행 하기

            @Override
            public void onCheckClick(View v, int pos) {

                AlertDialog.Builder dig_check = new AlertDialog.Builder(CompanyScoreActivity.this);
                dig_check.setMessage("한번 입력한 동행 점수는 수정할 수 없습니다.\n 진행하시겠습니까?");
                dig_check.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


//                        scoreDataArrayList.get(pos).setMessage(1);
                        // 서버로 연결해서 동행점수 남겼다는 것을 표시해주기
                        // 확인 버튼 누르면 완료로 바뀌고 버튼 비활성화 해놓기
                        // 1. 누가 누구에게 어느 동행글 동행 점수를 남겼다고 db 저장해놓기
                        // 2. 입력한 별점 서버에 연결해서 합산해서 평균값 저장하기

                        int roomnum = scoreDataArrayList.get(pos).getRoom_num();
                        float score = scoreDataArrayList.get(pos).getScore();

                        Log.e(TAG, "onClick: roomnum = "+ roomnum );
                        Log.e(TAG, "onClick: score = " + score );

                        // 유저의 기본 동행 점수 불러오기
                        retrofitInterface.BasicScore(user_email).enqueue(new Callback<UserData>() {
                            @Override
                            public void onResponse(Call<UserData> call, Response<UserData> response) {
                                UserData result = response.body();
                                Log.e(TAG, "유저 동행 점수 불러오기 onResponse: result =" + result );

                                float basicScore = result.getScore(); // 유저의 원래 동행 점수

                                // 원래 동행 점수와 현재 별점을 더해서 평균을 내야 한다.
                                BigDecimal f_val_BD1 = new BigDecimal(String.valueOf(basicScore)); // 원래 점수
                                BigDecimal f_val_BD2 = new BigDecimal(String.valueOf(score)); // 추가한 동행 점수

                                f_val_BD1 = f_val_BD1.add(f_val_BD2);

                                float result_add = f_val_BD1.floatValue();

                                float average = result_add/2 ;
                                Log.e(TAG, "onScoreChange: averege = " + average);
                                Log.e(TAG, "onScoreChange: strinf format = " + String.format("%.1f", average)); // 소숫점 첫째자리까지 반올림해서 표현
                                float last_score = Float.parseFloat(String.format("%.1f", average)); // 최종 점수 결과



                                // 최종적으로 동행 점수 update 해야 하며
                                // 누가 누구에게 어떤 동행에서 점수를 남겼는지 insert 해야 한다.
                                retrofitInterface.UpdateScore(now_user_email, user_email, roomnum,last_score ).enqueue(new Callback<ScoreData>() {
                                    @Override
                                    public void onResponse(Call<ScoreData> call, Response<ScoreData> response) {
                                        ScoreData result= response.body();

                                        Log.e(TAG, "점수 확인 onResponse: result = " + result );

                                        String status = result.getStatus();

                                        if(status.equals("true"))
                                        {
                                            scoreDataArrayList.get(pos).setMessage(1);

                                            scoreAdapter.notifyDataSetChanged();

                                            // 버튼 완료 상태로 바뀌게 두어야 한다.
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ScoreData> call, Throwable t) {

                                    }
                                });


                            }

                            @Override
                            public void onFailure(Call<UserData> call, Throwable t) {


                            }
                        });



                    }
                });
                dig_check.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig_check.show();

            }


        });

        // 점수 최종 제출
        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 확인 버튼 누르면 점수 남긴 유저의 프로필로 이동 -> 별점 적용된 유저의 정보가 업데이트 된것을 확인할 수 있도록 하기.
                Intent intent1 = new Intent(CompanyScoreActivity.this, OtherUserProfileActivity.class);
                intent1.putExtra("user_email", user_email);
                startActivity(intent1);

            }


        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CompanyScoreActivity.this, OtherUserProfileActivity.class);
                intent1.putExtra("user_email", user_email);
                startActivity(intent1);
            }
        });






    }
}