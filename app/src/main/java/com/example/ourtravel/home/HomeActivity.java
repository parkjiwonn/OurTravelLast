package com.example.ourtravel.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.Map.MapActivity;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;

import com.example.ourtravel.Adapter.HomeAdapter;
import com.example.ourtravel.RecyclerView.HomeData;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    final String TAG = "HomeActivity";
    //                    검색 버튼     필터 버튼     일기버튼   채팅 버튼    프로필 버튼  지도 버튼
    private ImageButton btn_search, btn_filter, btn_diary, btn_chat, btn_profile, btn_map;
    private FloatingActionButton btn_write ; // 동행글 작성버튼
    private TextView tx_filter; // 동행글 필터 타이틀

    RecyclerView recyclerView = null;
    HomeAdapter adapter = null;
    ArrayList<HomeData> list ;

//    NestedScrollView nestedScrollView; // 동행글 리스트 감싸고 있는 스크롤 뷰
//    ProgressBar progressBar; // 페이징 처리시 프로그래스 바
//
//    // 페이징 처리시
//    // 1페이지에 3개씩 데이터를 불러온다
//    int page = 1, limit = 3;

    private long backBtnTime = 0;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email;
    int filter ;
    Dialog dialog01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 동행글 작성 시간 가져오기
        // 현재 시간 가져오기
        long now = System.currentTimeMillis();
        // Date 형식으로 고치기
        Date mdate = new Date(now);
        // 날짜, 시간을 가져오고 싶은 형태로 가져오기
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getTime = simpleDate.format(mdate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mdate);

        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "onCreate: 요일 : " + dayOfWeekNumber );
        String str_week ="";

        switch (dayOfWeekNumber){
            case 1:
                str_week ="일요일";
                break;

            case 2 :
                str_week ="월요일";
                break;

            case 3:
                str_week ="화요일";
                break;

            case 4:
                str_week ="수요일";
                break;

            case 5:
                str_week ="목요일";
                break;

            case 6:
                str_week ="금요일";
                break;

            case 7:
                str_week ="토요일";
                break;
        }

        Log.e(TAG, "onCreate: getTime :"+ getTime);
        String upload_date = getTime +" "+ str_week;
        Log.e(TAG, "onClick: upload_date : " + upload_date );



        // 홈화면 필터 버튼클릭시 생기는 다이얼로그
        dialog01 = new Dialog(HomeActivity.this);
        dialog01.setContentView(R.layout.dialog_filter);

        // 리사이클러뷰 준비
        recyclerView = findViewById(R.id.HomeRecyclerView);
        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

//        // 페이징 처리 준비
//        nestedScrollView = findViewById(R.id.scroll_view);
//        progressBar = findViewById(R.id.progress_bar);



        // if user_email에 해당하는 값이 없으면 false 있으면 true 갖고오고 true 이면 각각 아이템에 set 하기.
        preferenceHelper = new PreferenceHelper(this);

        user_email = preferenceHelper.getEmail();



        // 현재 db에 저장되어 있는 모든 동행글들 불러와서 리사이클러뷰에 뿌려준다.
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 현재 시간 비교해서 동행 상황 변경해주기
        retrofitInterface.CompanyFinish(user_email).enqueue(new Callback<List<CompanyData>>() {
            @Override
            public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {

                List<CompanyData> result = response.body();
                Log.e(TAG, "시간 onResponse: result = " + result );
            }

            @Override
            public void onFailure(Call<List<CompanyData>> call, Throwable t) {

            }
        });

        filter = 1;
        retrofitInterface.list(filter).enqueue(new Callback<List<CompanyData>>() {
            @Override
            public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                if(response.isSuccessful())
                {
                        List<CompanyData> result = response.body();



                        for (int i=0; i < result.size(); i++) {

                            //응답이 companydata로 온다. list형태로
                            //근데 adapter에 담아야하는 데이터는 home data이다.
                            //이 둘을 같이 묶어줘야 한다.
                           CompanyData companyData = result.get(i);
                           HomeData homeData = new HomeData(companyData);
                           list.add(homeData);

                        }
                        // 데이터 새로고침해서 아이템 쌓기
                    adapter.notifyDataSetChanged();

                        Log.e(TAG, " response body : " + result );


                }
                else
                {
                    Log.e(TAG, "onResponse: response false2" );
                }
            }

            @Override
            public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });


        //btn_alarm = (ImageButton) findViewById(R.id.btn_alarm); // 알림 버튼
        btn_search = (ImageButton) findViewById(R.id.btn_search); // 검색 버튼
        btn_filter = (ImageButton) findViewById(R.id.btn_filter); // 필터 버튼
        btn_diary = (ImageButton) findViewById(R.id.btn_diary); // 일기 버튼
        btn_chat = (ImageButton) findViewById(R.id.btn_chat); // 채팅 버튼
        btn_profile = (ImageButton) findViewById(R.id.btn_profile); // 프로필 버튼
        btn_map = findViewById(R.id.btn_map); // 지도 버튼
        btn_write = (FloatingActionButton) findViewById(R.id.btn_write); // 동행글 작성 버튼

        tx_filter = findViewById(R.id.tx_filter); // 동행글 필터 타이틀

        //필터버튼
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 필터버튼 클릭시 필터에 해당하는 다이얼로그 띄워지도록 메서드 호출
                showFilterDialog();
            }
        });

        // 검색 버튼
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 검색창 액티비티로 이동
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 채팅 이동
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // 일기 이동 버튼
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 일기 액티비티로 이동
                Intent intent = new Intent(HomeActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 지도 액티비티로 이동
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        //동행글 작성 버튼 ( 동행글 작성 1 화면으로 이동 )
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 동행글 작성 액티비티로 이동
                Intent intent = new Intent(HomeActivity.this, Wcompany1Activity.class);
                startActivity(intent);
                finish();
            }
        });


        // 프로필 버튼
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 프로필 액티비티로 이동.
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    public void onBackPressed(){
        // 뒤로가기 했을 때 한번 더 뒤로가기를 한다면 앱이 종료된다.

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        // 2초의 시간 차이를 둔다.
        if (0 <= gapTime && 2000 >= gapTime)
        {
            super.onBackPressed();
        }
        else
        {
            backBtnTime = curTime;
            // 토스트 메시지를 띄워 유저에게 알린다.
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 필터 관련 다이얼로그 띄우는 메서드
    public void showFilterDialog(){

        dialog01.show();

        // 전체 보기 클릭
        // 모든 동행글 리스트 출력
        Button btn_all = dialog01.findViewById(R.id.btn_all);
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 각 필터에 고유 정수를 부여한다.
                // 전체 동행글 보기 - filter = 1

                // 동행글 필터 타이틀을 바꿔줘야 함.
                tx_filter.setText("전체 동행글");

                filter = 1;
                retrofitInterface.list(filter).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                        if(response.isSuccessful())
                        {
                            // filter 1에 해당하는 데이터들을 모두 불러온다.
                            List<CompanyData> result = response.body();

                            // 이전에 띄워져 있던 리사이클러뷰를 모두 clear() 시킨후 다시 data를
                            // list에 넣는다.
                            list.clear();
                            for (int i=0; i < result.size(); i++) {

                                //응답이 companydata로 온다. list형태로
                                //근데 adapter에 담아야하는 데이터는 home data이다.
                                //이 둘을 같이 묶어줘야 한다.
                                CompanyData companyData = result.get(i);
                                HomeData homeData = new HomeData(companyData);
                                list.add(homeData);

                            }
                            // 데이터 새로고침해서 아이템 쌓기
                            dialog01.dismiss();

                            Log.e(TAG, " all 1" );
                            // 가져온 데이터들을 어댑터에 넣는다.
                            adapter = new HomeAdapter(list, getApplicationContext());
                            Log.e(TAG, " all 2" );
                            // 어댑터 세팅해준다.
                            recyclerView.setAdapter(adapter);

                            Log.e(TAG, " all 3" );
                            // 리사이클러뷰 전체 업데이트를 해준다.
                            adapter.notifyDataSetChanged();
                            Log.e(TAG, " all 4" );

                            Log.e(TAG, "전체 버튼 클릭 response body : " + result );
                            // response body는 잘 들어오는거 확인함.

                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 전체버튼 클릭 response false2" );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });

            }
        });

        // 국내 동행글 보기 클릭
        // 국내 동행글만 출력
        Button btn_ko = dialog01.findViewById(R.id.btn_ko);
        btn_ko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 동행글 필터 타이틀 - 국내 동행글로 바꿔줘야 함.
                tx_filter.setText("국내 동행글");

                filter = 2;
                retrofitInterface.list(filter).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                        if(response.isSuccessful())
                        {
                            List<CompanyData> result = response.body();


                            list.clear();
                            for (int i=0; i < result.size(); i++) {

                                //응답이 companydata로 온다. list형태로
                                //근데 adapter에 담아야하는 데이터는 home data이다.
                                //이 둘을 같이 묶어줘야 한다.
                                CompanyData companyData = result.get(i);
                                HomeData homeData = new HomeData(companyData);
                                list.add(homeData);

                            }
                            // 데이터 새로고침해서 아이템 쌓기
                            dialog01.dismiss();

                            Log.e(TAG, " all 1" );

                            adapter = new HomeAdapter(list, getApplicationContext());
                            Log.e(TAG, " all 2" );
                            recyclerView.setAdapter(adapter);

                            Log.e(TAG, " all 3" );
                            adapter.notifyDataSetChanged();
                            Log.e(TAG, " all 4" );

                            Log.e(TAG, "국내여행 버튼 클릭 response body : " + result );
                            // response body는 잘 들어오는거 확인함.

                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 국내여행 클릭 response false2" );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });

            }
        });

        // 해외 동행글 보기 클릭
        Button btn_glo = dialog01.findViewById(R.id.btn_glo);
        btn_glo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 동행글 필터 - 해외 동행글로 바꿔줘야 함.
                tx_filter.setText("해외 동행글");

                filter = 3;
                retrofitInterface.list(filter).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                        if(response.isSuccessful())
                        {
                            List<CompanyData> result = response.body();


                            list.clear();
                            for (int i=0; i < result.size(); i++) {

                                //응답이 companydata로 온다. list형태로
                                //근데 adapter에 담아야하는 데이터는 home data이다.
                                //이 둘을 같이 묶어줘야 한다.
                                CompanyData companyData = result.get(i);
                                HomeData homeData = new HomeData(companyData);
                                list.add(homeData);

                            }
                            // 데이터 새로고침해서 아이템 쌓기
                            dialog01.dismiss();

                            Log.e(TAG, " all 1" );

                            adapter = new HomeAdapter(list, getApplicationContext());
                            Log.e(TAG, " all 2" );
                            recyclerView.setAdapter(adapter);

                            Log.e(TAG, " all 3" );

                            adapter.notifyDataSetChanged();
                            Log.e(TAG, " all 4" );

                            Log.e(TAG, "해외여행 버튼 클릭 response body : " + result );
                            // response body는 잘 들어오는거 확인함.

                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 해외여행 클릭 response false2" );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });

            }
        });

        // 북마크한 동행글 보기 클릭
        Button btn_bookmark = dialog01.findViewById(R.id.btn_bookmark);
        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 동행글 필터 - 북마크한 동행글로 바꿔줘야 함.
                tx_filter.setText("북마크한 동행글");

                retrofitInterface.checkbookmark(user_email).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                        if(response.isSuccessful()&& response.body() != null)
                        {
                            List<CompanyData> result = response.body();

                            if(result.isEmpty())
                            {
                                AlertDialog.Builder book = new AlertDialog.Builder(HomeActivity.this);
                                book.setMessage("현재 북마크한 동행글이 없습니다!");
                                book.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialog01.dismiss();

                                    }
                                });
                                book.show();
                            }
                            else
                            {
                                list.clear();

                                for (int i=0; i < result.size(); i++) {

                                    //응답이 companydata로 온다. list형태로
                                    //근데 adapter에 담아야하는 데이터는 home data이다.
                                    //이 둘을 같이 묶어줘야 한다.
                                    CompanyData companyData = result.get(i);
                                    HomeData homeData = new HomeData(companyData);
                                    list.add(homeData);

                                }
                                // 데이터 새로고침해서 아이템 쌓기
                                dialog01.dismiss();


                                Log.e(TAG, " 1" );
                                //recyclerView.removeAllViews();
                                adapter = new HomeAdapter(list, getApplicationContext());
                                Log.e(TAG, " 2" );
                                recyclerView.setAdapter(adapter);
                                Log.e(TAG, " 3" );

                                adapter.notifyDataSetChanged();
                                Log.e(TAG, " 4" );


                                Log.e(TAG, "북마크 response body : " + result );
                                // response body는 잘 들어오는거 확인함.

                            }



                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 북마크 false" );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });

            }
        });

        // 취소 버튼
        Button btn_cancel = dialog01.findViewById(R.id.btn_cancle);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog01.dismiss();
                // 취소 버튼 누를 시에 다이얼로그 dismiss 됨.
            }
        });


    }



}