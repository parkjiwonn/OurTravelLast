package com.example.ourtravel.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Wcompany1Activity extends AppCompatActivity {

    private Button btn_datePicker, btn_ko, btn_glo, btn_next, btn_photo, btn_basicphoto;
    private ImageButton btn_add, btn_minus, back_btn;
    private EditText et_title, et_content;
    private TextView tv_count;
    private TextView typeconfirm , dateconfirm , photoconfirm, titleconfirm, contentconfirm;
    private ImageView banner;
    private Calendar calendar, minDate, maxDate;
    // 참여인원 count 변수
    private int count = 2;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email;

    final String TAG = "Wcompany1Activity";

    // 여행 기간
    String dateString1;
    String dateString2;
    String finishdate; // 동행 종료 시간

    // 사진
    int check = 1;


    String type;
    String mainspot;
    String subspot;
    String start;
    String finish;
    String title;
    String content;
    String people;

    int mainpos;
    int subpos;

    // 여행 지역
    String MainSpot;
    String SubSpot;
    Bitmap bitmap = null;
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 1;
    static final int PERMISSIONS_REQUEST2 = 2;

    // 카메라 촬영시 사용되는 intent
    Intent intent;
    final int CAMERA = 100; // 카메라 선택시 인텐트로 보내는 값
    final int GALLERY = 101; // 갤러리 선택 시 인텐트로 보내는 값

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcompany1);


        preferenceHelper = new PreferenceHelper(this);

        user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        //TextView
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(count));

        //confirm 문
        typeconfirm = findViewById(R.id.typeconfirm);
        dateconfirm = findViewById(R.id.dateconfirm);
        photoconfirm = findViewById(R.id.photoconfirm);
        titleconfirm = findViewById(R.id.titleconfirm);
        contentconfirm= findViewById(R.id.contentconfirm);

        //confirm 문들 초반에는 GONE 상태로 지정
        typeconfirm.setVisibility(typeconfirm.GONE);
        dateconfirm.setVisibility(dateconfirm.GONE);
        photoconfirm.setVisibility(photoconfirm.GONE);
        titleconfirm.setVisibility(titleconfirm.GONE);
        contentconfirm.setVisibility(contentconfirm.GONE);




        //EditText
        et_title = (EditText)findViewById(R.id.et_title);
        et_content = (EditText)findViewById(R.id.et_content);

        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
               titleconfirm.setVisibility(View.GONE);
            }// afterTextChanged()..
        });

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                contentconfirm.setVisibility(View.GONE);
            }// afterTextChanged()..
        });

        //ImageView
        banner = (ImageView)findViewById(R.id.imageView);

        //calender
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        // 여행지 주소 - Spinner 구현
        // 1. 메인 스피너
        Spinner spinnerMenu = (Spinner)findViewById(R.id.Mainspinner);
        // 2. 서브 스피너(메인 스피너에서 장소선택시 생기게 구현)
        Spinner spinnerSub = (Spinner)findViewById(R.id.Subspinner);

        // 국내여행/ 해외여행 버튼 선택 전 spinner 상태는 GONE 선택되면 VISIBLE 상태로 전환.
        spinnerMenu.setVisibility(spinnerMenu.GONE);
        spinnerSub.setVisibility(spinnerSub.GONE);

        // 국내 장소

        // final 로 변수 설정할 때 대문자로 변수 설정하는 것.

        final String[] ko_spot = getResources().getStringArray(R.array.ko_array);
        final String[] jeju_array = getResources().getStringArray(R.array.jeju_array);
        final String[] seoul_array = getResources().getStringArray(R.array.seoul_array);
        final String[] kyonggi_array = getResources().getStringArray(R.array.kyonggi_array);
        final String[] incheon_array = getResources().getStringArray(R.array.incheon_array);
        final String[] kang_array = getResources().getStringArray(R.array.kang_array);
        final String[] chungN_array = getResources().getStringArray(R.array.chungN_array);
        final String[] chungS_array = getResources().getStringArray(R.array.chungS_array);
        final String[] dae_array = getResources().getStringArray(R.array.dae_array);
        final String[] gwang_array = getResources().getStringArray(R.array.gwang_array);
        final String[] junN_array = getResources().getStringArray(R.array.junN_array);
        final String[] junS_array = getResources().getStringArray(R.array.junS_array);
        final String[] kyongN_array = getResources().getStringArray(R.array.kyongN_array);
        final String[] kyongS_array = getResources().getStringArray(R.array.kyongS_array);
        final String[] busan_array = getResources().getStringArray(R.array.busan_array);
        final String[] daegu_array = getResources().getStringArray(R.array.daegu_array);

        // 해외 장소
        final String[] glo_spot = getResources().getStringArray(R.array.glo_array);
        final String[] dongnamah_array = getResources().getStringArray(R.array.dongnamah_array);
        final String[] europe_array = getResources().getStringArray(R.array.europe_array);
        final String[] gwam_array = getResources().getStringArray(R.array.gwam_array);
        final String[] america_array = getResources().getStringArray(R.array.america_array);
        final String[] china_array = getResources().getStringArray(R.array.china_array);
        final String[] japan_array = getResources().getStringArray(R.array.japan_array);


        // createFromResource는 onClickListner안에서 지원되지 않아서 밖에서 어댑터 선언을 해줘야 한다.
        // 한국 array
        ArrayAdapter komenuAdapter = ArrayAdapter.createFromResource(this, R.array.ko_array, android.R.layout.simple_spinner_item);
        ArrayAdapter jejuAdapter = ArrayAdapter.createFromResource(this, R.array.jeju_array, android.R.layout.simple_spinner_item);
        ArrayAdapter seoulAdapter = ArrayAdapter.createFromResource(this, R.array.seoul_array, android.R.layout.simple_spinner_item);
        ArrayAdapter kyonggiAdapter = ArrayAdapter.createFromResource(this, R.array.kyonggi_array, android.R.layout.simple_spinner_item);
        ArrayAdapter incheonAdapter = ArrayAdapter.createFromResource(this, R.array.incheon_array, android.R.layout.simple_spinner_item);
        ArrayAdapter kangAdapter = ArrayAdapter.createFromResource(this, R.array.kang_array, android.R.layout.simple_spinner_item);
        ArrayAdapter chungNAdapter = ArrayAdapter.createFromResource(this, R.array.chungN_array, android.R.layout.simple_spinner_item);
        ArrayAdapter chungSAdapter = ArrayAdapter.createFromResource(this, R.array.chungS_array, android.R.layout.simple_spinner_item);
        ArrayAdapter daeAdapter = ArrayAdapter.createFromResource(this, R.array.dae_array, android.R.layout.simple_spinner_item);
        ArrayAdapter gwangAdapter = ArrayAdapter.createFromResource(this, R.array.gwang_array, android.R.layout.simple_spinner_item);
        ArrayAdapter junNAdapter = ArrayAdapter.createFromResource(this, R.array.junN_array, android.R.layout.simple_spinner_item);
        ArrayAdapter junSAdapter = ArrayAdapter.createFromResource(this, R.array.junS_array, android.R.layout.simple_spinner_item);
        ArrayAdapter kyongNAdapter = ArrayAdapter.createFromResource(this, R.array.kyongN_array, android.R.layout.simple_spinner_item);
        ArrayAdapter kyongSAdapter = ArrayAdapter.createFromResource(this, R.array.kyongS_array, android.R.layout.simple_spinner_item);
        ArrayAdapter busandapter = ArrayAdapter.createFromResource(this, R.array.busan_array, android.R.layout.simple_spinner_item);
        ArrayAdapter daeguAdapter = ArrayAdapter.createFromResource(this, R.array.daegu_array, android.R.layout.simple_spinner_item);


        // 세계 array
        // spinner 클릭시 dropdown 모양을 설정
        ArrayAdapter glomenuAdapter = ArrayAdapter.createFromResource(this, R.array.glo_array, android.R.layout.simple_spinner_item);
        ArrayAdapter dongnamahAdapter = ArrayAdapter.createFromResource(this, R.array.dongnamah_array, android.R.layout.simple_spinner_item);
        ArrayAdapter europeAdapter = ArrayAdapter.createFromResource(this, R.array.europe_array, android.R.layout.simple_spinner_item);
        ArrayAdapter gwamAdapter = ArrayAdapter.createFromResource(this, R.array.gwam_array, android.R.layout.simple_spinner_item);
        ArrayAdapter americaAdapter = ArrayAdapter.createFromResource(this, R.array.america_array, android.R.layout.simple_spinner_item);
        ArrayAdapter chinaAdapter = ArrayAdapter.createFromResource(this, R.array.china_array, android.R.layout.simple_spinner_item);
        ArrayAdapter japanAdapter = ArrayAdapter.createFromResource(this, R.array.japan_array, android.R.layout.simple_spinner_item);




        //button
        btn_datePicker = (Button) findViewById(R.id.btn_datePicker);
        btn_add  = (ImageButton) findViewById(R.id.btn_add);
        btn_minus  = (ImageButton) findViewById(R.id.btn_minus);

        btn_minus.setEnabled(false);
        //맨처음 참여인원이 2로 시작하니까 마이너스 버튼 비활성화 시켜야함.

        btn_ko = (Button)findViewById(R.id.btn_ko);
        btn_glo = (Button)findViewById(R.id.btn_glo);
        btn_next = (Button)findViewById(R.id.btn_next);
        back_btn = (ImageButton)findViewById(R.id.back_btn);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_basicphoto = (Button) findViewById(R.id.btn_basicphoto);


        // 동행글 작성 첫페이지 뒤로가기 버튼 클릭시 홈 액티비티로 이동.
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Wcompany1Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 기본 이미지 설정.
        btn_basicphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                banner.setImageResource(R.drawable.img);

                check = 2;
                photoconfirm.setVisibility(View.GONE);

            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                makeDialog();
                check = 3;
                photoconfirm.setVisibility(View.GONE);

            }
        });


        // 글 작성 완료 버튼
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                people = tv_count.getText().toString();
                Log.e("people", people);
                Log.e("check", String.valueOf(check));

                // 여행 종류
                if(btn_ko.isSelected()){
                    type = btn_ko.getText().toString();
                    Log.e("ko_type", type);
                    mainspot = MainSpot;
                    Log.e("mainSpot", mainspot);
                    subspot = SubSpot;
                    Log.e("subspot", subspot);
                }
                else if(btn_glo.isSelected())
                {
                    type = btn_glo.getText().toString();
                    Log.e("glo_type", type);
                    mainspot = MainSpot;
                    Log.e("mainSpot", mainspot);
                    subspot = SubSpot;
                    Log.e("subspot", subspot);
                }

                if(!btn_ko.isSelected() && !btn_glo.isSelected())
                {
                    typeconfirm.setVisibility(View.VISIBLE);
                }

                // 기간 선택
                if(btn_datePicker.getText().toString().equals("기간선택"))
                {
                    dateconfirm.setVisibility(View.VISIBLE);
                }
                else
                {
                    start = dateString1;
                    finish = dateString2;
                    Log.e("start", start);
                    Log.e("finish", finish);
                }

                // 제목
                if(et_title.getText().toString().equals(""))
                {
                    titleconfirm.setVisibility(view.VISIBLE);
                }
                else{
                    // 저장
                    title = et_title.getText().toString();
                    Log.e("title", title);
                }

                // 내용
                if(et_content.getText().toString().equals(""))
                {
                    contentconfirm.setVisibility(View.VISIBLE);
                }
                else
                {
                    content = et_content.getText().toString();
                    Log.e("content", content);
                }

                if(check == 1)
                {
                    photoconfirm.setVisibility(View.VISIBLE);
                }
                else
                {
                    // 사진을 어떻게 담아야 할까.
                    // 게시판에 이미지 업로드..
                }

                Log.e(TAG, "onClick: mainpos" + mainpos );
                Log.e(TAG, "onClick: subpos" + subpos );


                if(typeconfirm.getVisibility() == View.GONE  && photoconfirm.getVisibility() == View.GONE && dateconfirm.getVisibility() == View.GONE &&
                         titleconfirm.getVisibility() == View.GONE && contentconfirm.getVisibility() == View.GONE)
                {
                    // 동행글 작성된 일자 가져오기 - ex) 2022년 몇월 몇일
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


                    //==========================동행글이 작성된 시간==============================================
                    // 한국 표준시 가져오기
                    TimeZone tz;
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("hh : mm aa", Locale.KOREA);
                    // dateformat은 dateformat 클래스에서 추상 클래스 이므로 simpledateformat와 같은 서브 클래스를 사용해 날짜/ 시간 형식화, 구문 분석등의 목적으로 주로 사용한다.

                    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
                    tz = TimeZone.getTimeZone("Asia/Seoul");
                    dateFormat.setTimeZone(tz);
                    String time = dateFormat.format(date);
                    Log.e(TAG, "onCreate: 시간 타입 :"+ dateFormat.format(date).getClass().getName() );
                    Log.e(TAG, "onCreate: date : " + dateFormat.format(date) );
                    //===========================================================================================



                    // 이미지 선택 여부에 따라서 레트로핏 통신 다르게 하기.
                    if(check == 3) {

                        // 이미지 선택++ int값으로 저장
                        upload(type, upload_date, time, people, mainspot, subspot, start, finish, title, content, mainpos, subpos);
                    }
                    else if(check == 2)
                    {
                        // 기본 이미지 선택
                        String basic = "basic";
                        withoutpic(type, upload_date, time, people, mainspot, subspot, start, finish, title, content, basic, mainpos, subpos);

                    }


                }
                else{

                }








            }
        });



        // 국내여행 선택
        btn_ko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_ko.setSelected(true);
                btn_glo.setSelected(false);
                // 버튼이 select 됐을 때 그때 버튼정보를 가져와야 함.
                typeconfirm.setVisibility(typeconfirm.GONE);

                spinnerMenu.setVisibility(spinnerMenu.VISIBLE);
                spinnerSub.setVisibility(spinnerSub.VISIBLE);

                //Log.e("ko", btn_ko.getText().toString());


                komenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // 스피너에 어댑터 연결
                spinnerMenu.setAdapter(komenuAdapter);

                spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        Log.e("선택 값", ko_spot[position]);
                        Log.e(TAG, "onItemSelected: position " + position );
                        if(ko_spot[position].equals("제주도"))
                        {
                            jejuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(jejuAdapter);
                            MainSpot = ko_spot[position];
                            mainpos = position;
                            Log.e("선택 값1", ko_spot[position]);

                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = jeju_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", jeju_array[position2]);
                                    Log.e(TAG, "onItemSelected: position2 " + position2 );
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });




                        }
                        else if(ko_spot[position].equals("서울"))
                        {
                            seoulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(seoulAdapter);
                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = seoul_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", seoul_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });


                        }
                        else if(ko_spot[position].equals("경기도"))
                        {
                            kyonggiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(kyonggiAdapter);
                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = kyonggi_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", kyonggi_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });

                        }
                        else if(ko_spot[position].equals("인천"))
                        {
                            incheonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(incheonAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = incheon_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", incheon_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("강원도"))
                        {
                            kangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(kangAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = kang_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", kang_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("충청북도"))
                        {
                            chungNAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(chungNAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = chungN_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", chungN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });

                        }
                        else if(ko_spot[position].equals("충청남도"))
                        {
                            chungSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(chungSAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = chungS_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", chungS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("대전"))
                        {
                            daeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(daeAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = dae_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", dae_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("광주"))
                        {
                            gwangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(gwangAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = gwang_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", gwang_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("전라북도"))
                        {
                            junNAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(junNAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = junN_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", junN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("전라남도"))
                        {
                            junSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(junSAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = junS_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", junS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("경상북도"))
                        {
                            kyongNAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(kyongNAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = kyongN_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", kyongN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("경상남도/울산"))
                        {
                            kyongSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(kyongSAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = kyongS_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", kyongS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("부산"))
                        {
                            busandapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(busandapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = busan_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", busan_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("대구"))
                        {
                            daeguAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(daeguAdapter);

                            MainSpot = ko_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = daegu_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", daegu_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        // 해외여행 선택
        btn_glo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_ko.setSelected(false);
                btn_glo.setSelected(true);

                typeconfirm.setVisibility(typeconfirm.GONE);

                spinnerMenu.setVisibility(spinnerMenu.VISIBLE);
                spinnerSub.setVisibility(spinnerSub.VISIBLE);

                Log.e("glo", btn_glo.getText().toString());

                glomenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // 스피너에 어댑터 연결
                spinnerMenu.setAdapter(glomenuAdapter);

                spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        if(glo_spot[position].equals("동남아/대만/서남아"))
                        {
                            dongnamahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(dongnamahAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = dongnamah_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", dongnamah_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("유럽/아프리카"))
                        {
                            europeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(europeAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = europe_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", europe_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("괌/사이판/호주/뉴질랜드"))
                        {
                            gwamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(gwamAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = gwam_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", gwam_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("미주/중남미/하와이"))
                        {
                            americaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(americaAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = america_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", america_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("중국/홍콩/몽골"))
                        {
                            chinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(chinaAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = china_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", china_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("일본"))
                        {
                            japanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(japanAdapter);

                            MainSpot = glo_spot[position];
                            mainpos = position;
                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = japan_array[position2];
                                    subpos = position2;
                                    Log.e("선택 값2", japan_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("선택 값2", "전체22");
                                }
                            });
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        });



        // 참여인원이 10명일때 + 버튼 비활성화
        // 참여인원 +
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                tv_count.setText(String.valueOf(count));

                if(tv_count.getText().toString().equals("10"))
                {
                    btn_add.setEnabled(false);
                    btn_minus.setEnabled(true);
                }
                else if (tv_count.getText().toString().equals("3")){
                    btn_minus.setEnabled(true);
                }
                // +버튼은 숫자 10이 됐을 때 비활성화되고 minus true지만 +버튼을 눌러 3이 되는 순간 -버튼이 true가 되어서 인원감소가 되는것.

            }
        });

        // 참여인원이 2명일때 - 버튼 비활성화
        // 참여인원 -
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                tv_count.setText(String.valueOf(count));

                if(tv_count.getText().toString().equals("2"))
                {
                    btn_add.setEnabled(true);
                    btn_minus.setEnabled(false);
                }
                else if (tv_count.getText().toString().equals("9")){
                    btn_add.setEnabled(true);
                }



            }
        });



        // 여행 일정 기간 버튼
        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("여행 기간 선택") ;

                //기간 이미 선택되게 하는 구문.
                //builder.setSelection(androidx.core.util.Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));

                MaterialDatePicker materialDatePicker = builder.build();


                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<androidx.core.util.Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(androidx.core.util.Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);




                        // dateString1 시작 날짜 dateString2 마지막 날짜
                        dateString1 = simpleDateFormat.format(date1);
                        dateString2 = simpleDateFormat.format(date2);

                        btn_datePicker.setText(dateString1 + " ~ " + dateString2);

                        if(!btn_datePicker.getText().toString().equals("기간선택"))
                        {

                            dateconfirm.setVisibility(View.GONE);

                        }


                    }


                });

            }
        });



    }

    private void withoutpic(String type, String upload_date, String time, String people, String mainspot, String subspot, String startdate, String finishdate, String title, String content, String basic, int mainpos, int subpos){

        Log.e(TAG, "withoutpic: upload_date :" + upload_date );

        retrofitInterface.basicpic(type, upload_date, time, startdate, finishdate, people, mainspot, subspot, title, content, basic, user_email, mainpos, subpos).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful())
                {
                     CompanyData result = response.body();
                     Log.e(TAG, "onResponse: 작성 result : "+ result );
                        String status = result.getStatus();


                        if(status.equals("true"))
                        {
                            Intent intent = new Intent(Wcompany1Activity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "fail");
                        }


                }
                else
                {
                    Log.e(TAG, "fail");
                }
            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    // 전체 업로드하는 함수.
    private void upload(String type, String upload_date, String time, String people, String mainspot, String subspot, String startdate, String finishdate, String title, String content, int mainpos, int subpos){

        String path = imagePath;

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);


        // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
        // RequestBody로 Multipart.Part 객체 생성
        //createFormData(...)에 들어가는 파라미터는 순서대로 다음과 같은 의미를 가진다.
        //1. 서버에서 받는 키값 String
        //2. 파일 이름 String
        //3. 파일 경로를 가지는 RequestBody 객체

        //로그를 보기 위한 Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) //로그 기능 추가
                .build();


        RetrofitInterface uploadapi = retrofit.create(RetrofitInterface.class);

        Log.e(TAG, "upload: mainpos :" + mainpos );
        Log.e(TAG, "upload: subpos : " + subpos );
        Call<CompanyData> call=uploadapi.Upload(body,type, upload_date, time, startdate, finishdate, people, mainspot, subspot, title, content, user_email, mainpos, subpos);
        call.enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {

                if(response.isSuccessful()){

                    CompanyData result = response.body();

                    String status = result.getStatus();


                    if(status.equals("true"))
                    {
                        Log.e(TAG, "onResponse: test 확인 result"+ result );
                        Intent intent = new Intent(Wcompany1Activity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                    else{
                        Log.e(TAG, "fail333");
                        String message = result.getMessage();
                        Log.e(TAG, "onResponse: message : " + message );
                    }



                }
                else{
                    // 실패
                    Log.e(TAG, "onResponse fail");
                }


            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

    }



    // 사진 선택 버튼 클릭시 다이얼로그
    private void makeDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(Wcompany1Activity.this);
        alt_bld.setTitle("사진 선택");
        alt_bld.setNeutralButton("갤러리에서 선택", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int i) {

                boolean hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if(!hasWritePerm)
                {
                    OnCheckPermission();
                }
                else
                {
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY); // final int GALLERY = 101;

                }


            }
        }).setNegativeButton("카메라로 촬영", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(DialogInterface dialog, int i) {


//        권한 체크
                boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                if (!hasCamPerm )  // 권한 없을 시  권한설정 요청
                {
                    OnCameraCheckPermission();
                }
                else
                {
                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File imageFile = null;
                        try {
                            imageFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (imageFile != null) {
                            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                                    "com.example.OurTravel.fileprovider",
                                    imageFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, CAMERA); // final int CAMERA = 100;
                        }
                    }
                }

            }
        });
        alt_bld.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();

            }
        });
        alt_bld.show();
    }

    // 카메라 권한 요청
    private void OnCameraCheckPermission() {

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ){
                Toast.makeText(this, "해당 기능 사용을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},PERMISSIONS_REQUEST2);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST2);

            }

        }
    }

    // 갤러리 권한 요청
    public void OnCheckPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
                Toast.makeText(this, "해당 기능 사용을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        System.out.println("requestcode"+requestCode);


        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "갤러리에 대한 권한이 설정 되었습니다.", Toast.LENGTH_LONG).show();

                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wcompany1Activity.this);

                    builder.setMessage("갤러리에 대한 권한 사용을 거부하였습니다.\n기능 사용을 원하실 경우 휴대폰 설정 > 애플리케이션 관리자에서 해당 앱의 권한을 허용해주세요.");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }

                break;

            case PERMISSIONS_REQUEST2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "카메라에 대한 권한이 설정 되었습니다.", Toast.LENGTH_LONG).show();

                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File imageFile = null;
                        try {
                            imageFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (imageFile != null) {
                            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                                    "com.example.OurTravel.fileprovider",
                                    imageFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, CAMERA);
                        }
                    }

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wcompany1Activity.this);

                    builder.setMessage("카메라에 대한 권한 사용을 거부하였습니다.\n기능 사용을 원하실 경우 휴대폰 설정 > 애플리케이션 관리자에서 해당 앱의 권한을 허용해주세요.");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();


                }

                break;



        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // 결과가 있을 경우

            switch (requestCode) {
                case GALLERY: // 갤러리에서 이미지로 선택한 경우
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        imagePath = cursor.getString(index);
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        Glide.with(this).load(imagePath).into(banner);
                        cursor.close();
                    }
//                    InputStream 으로 이미지 세팅하기
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        //ㅡGlide.with(this).load(imagePath).into(banner);
                        //banner.setImageBitmap(bitmap);
                        // 비트맵 객체를 jpg형식으로 변환
                        // 안드로이드에서 이미지 파일은 bitmap을 이용해 다룬다. bitmap객체를 이미지 파일로 저장하고 싶을 대 bitmap의 compress 메서드 이용한다.

//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream );
//                        // 세번째 파라미터는 bitmap 이미지를 저장하기 위한 outputstream이다.
//                        // + input은 읽을때 output은 저장할때
//                        // 두번째 파라미터인 40은 사진을 40%로 압축한다는 의미이다.
//
//

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case CAMERA: // 카메라로 이미지 가져온 경우
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                    //BitmapFactory.decodeFile 로컬에 존재하는 파일을 그대로 읽어올 때 쓴다.
                    //options를 사용해서 이미지를 줄이고 줄인 이미지 경로 저장한것.
                    //banner.setImageBitmap(bitmap);
                    Glide.with(this).load(imagePath).into(banner);
                    // 비트맵 객체를 jpg형식으로 변환
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream );
                    byte[] byteArray = stream.toByteArray();

                    // setImageBitmap 비트맵을 load하는 것.
                    // "bitmap" 은 비트맵 객체이다.
                    // 이 비트맵 객체를 jpeg 이미지로 저장해야 함.
                    break;
            }

        }
    }


    @SuppressLint("SimpleDateFormat")
    File createImageFile() throws IOException {
//        이미지 파일 생성
        String timeStamp = imageDate.format(new Date()); // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
        String fileName = "IMAGE_" + timeStamp; // 이미지 파일 명
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(fileName, ".jpg", storageDir); // 이미지 파일 생성
        imagePath = file.getAbsolutePath(); // 파일 절대경로 저장하기
        return file;
    }



}