package com.example.ourtravel.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;

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
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

public class UcompanyActivity extends AppCompatActivity {
    private Button btn_datePicker, btn_ko, btn_glo, btn_next, btn_photo, btn_basicphoto;
    private ImageButton btn_add, btn_minus, back_btn;
    private EditText et_title, et_content;
    private TextView tv_count;
    private TextView typeconfirm , dateconfirm , photoconfirm, titleconfirm, contentconfirm;
    private ImageView banner;
    private Calendar calendar, minDate, maxDate;
    // ???????????? count ??????
    private int count = 2;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email;

    final String TAG = "Wcompany1Activity";


    // ?????? ??????
    String dateString1;
    String dateString2;

    // ??????
    int check = 1;

    int id;
    int mainpos;
    int subpos;

    String type;
    String mainspot;
    String subspot;
    String start;
    String finish;
    String title;
    String content;
    String people;

    // ?????? ??????
    String MainSpot;
    String SubSpot;
    Bitmap bitmap = null;
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 1;
    static final int PERMISSIONS_REQUEST2 = 2;

    // ????????? ????????? ???????????? intent
    Intent intent;
    final int CAMERA = 100; // ????????? ????????? ???????????? ????????? ???
    final int GALLERY = 101; // ????????? ?????? ??? ???????????? ????????? ???

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucompany);

        // ?????? ??????
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

        // ?????? ??????
        final String[] glo_spot = getResources().getStringArray(R.array.glo_array);

        final String[] dongnamah_array = getResources().getStringArray(R.array.dongnamah_array);
        final String[] europe_array = getResources().getStringArray(R.array.europe_array);
        final String[] gwam_array = getResources().getStringArray(R.array.gwam_array);
        final String[] america_array = getResources().getStringArray(R.array.america_array);
        final String[] china_array = getResources().getStringArray(R.array.china_array);
        final String[] japan_array = getResources().getStringArray(R.array.japan_array);


        // createFromResource??? onClickListner????????? ???????????? ????????? ????????? ????????? ????????? ????????? ??????.
        // ?????? array
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


        // ?????? array
        // spinner ????????? dropdown ????????? ??????
        ArrayAdapter glomenuAdapter = ArrayAdapter.createFromResource(this, R.array.glo_array, android.R.layout.simple_spinner_item);
        ArrayAdapter dongnamahAdapter = ArrayAdapter.createFromResource(this, R.array.dongnamah_array, android.R.layout.simple_spinner_item);
        ArrayAdapter europeAdapter = ArrayAdapter.createFromResource(this, R.array.europe_array, android.R.layout.simple_spinner_item);
        ArrayAdapter gwamAdapter = ArrayAdapter.createFromResource(this, R.array.gwam_array, android.R.layout.simple_spinner_item);
        ArrayAdapter americaAdapter = ArrayAdapter.createFromResource(this, R.array.america_array, android.R.layout.simple_spinner_item);
        ArrayAdapter chinaAdapter = ArrayAdapter.createFromResource(this, R.array.china_array, android.R.layout.simple_spinner_item);
        ArrayAdapter japanAdapter = ArrayAdapter.createFromResource(this, R.array.japan_array, android.R.layout.simple_spinner_item);



        // ????????? ?????? - Spinner ??????
        // 1. ?????? ?????????
        Spinner spinnerMenu = (Spinner)findViewById(R.id.Mainspinner);
        // 2. ?????? ?????????(?????? ??????????????? ??????????????? ????????? ??????)
        Spinner spinnerSub = (Spinner)findViewById(R.id.Subspinner);

        // ????????????/ ???????????? ?????? ?????? ??? spinner ????????? GONE ???????????? VISIBLE ????????? ??????.
        spinnerMenu.setVisibility(spinnerMenu.GONE);
        spinnerSub.setVisibility(spinnerSub.GONE);

        Intent intent = getIntent();

        id = (int)intent.getExtras().get("id");
        Log.e(TAG, "Ucompany onCreate: id : " + id );


        preferenceHelper = new PreferenceHelper(this);

        user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // update ???????????? ?????? ???????????? ????????????.
        retrofitInterface.updateset(id).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful())
                {
                    CompanyData result = response.body();
                    String status = result.getStatus();
                    String type = result.getType();
                    String startdate = result.getStartdate();
                    String finishdate = result.getFinishdate();
                    String mainspot = result.getMainspot();
                    String subspot = result.getSubspot(); // + position
                    String company = result.getCompany();
                    String title = result.getTitle();
                    String content = result.getContent();
                    String photo = result.getPhoto();
                    int mainpos = result.getMainpos();
                    int subpos = result.getSubpos();


                    if(status.equals("true"))
                    { Log.e(TAG, "onResponse: type:"+ type );
                        if(type.equals("?????? ??????"))
                        {

                            btn_ko.setSelected(true);
                            btn_glo.setSelected(false);
                            spinnerMenu.setVisibility(spinnerMenu.VISIBLE);
                            spinnerSub.setVisibility(spinnerSub.VISIBLE);
                            //????????? ??????????????????
                            komenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // ???????????? ????????? ??????
                            spinnerMenu.setAdapter(komenuAdapter);
                            spinnerMenu.setSelection(mainpos, false);
                            spinnerSub.setSelection(subpos,false);

                            switch (mainpos)
                            {
                                case 0 :
                                    jejuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(jejuAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 1 :
                                    seoulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(seoulAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 2:
                                    kyonggiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(kyonggiAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                            }
                        }
                        else
                        {
                            btn_ko.setSelected(false);
                            btn_glo.setSelected(true);
                            spinnerMenu.setVisibility(spinnerMenu.VISIBLE);
                            spinnerSub.setVisibility(spinnerSub.VISIBLE);

                            glomenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // ???????????? ????????? ??????
                            spinnerMenu.setAdapter(glomenuAdapter);
                            spinnerMenu.setSelection(mainpos, false);

                            switch (mainpos)
                            {
                                case 0 :

                                    dongnamahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(dongnamahAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 1 :
                                    europeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(europeAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 2 :
                                    gwamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(gwamAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 3 :
                                    americaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(americaAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 4 :
                                    chinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(chinaAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                                case 5 :
                                    japanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSub.setAdapter(japanAdapter);
                                    spinnerSub.setSelection(subpos,false);
                                    break;

                            }



                        }


                        if(photo.equals("basic"))
                        {
                            banner.setImageResource(R.drawable.img);
                        }
                        else{

                            String url = "http://3.39.168.165/companyimg/" + photo;
                            Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(UcompanyActivity.this).load(url).into(banner);
                        }
                        tv_count.setText(company);
                        et_title.setText(title);
                        et_content.setText(content);
                        btn_datePicker.setText(startdate + " ~ " + finishdate);

                    }
                    else
                    {
                        Log.e(TAG, "onResponse: status fasle" );
                    }
                }
                else
                {
                    Log.e(TAG, "onResponse: response fail" );
                }

            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e("onresponse", "?????? : " + t.getMessage());
            }
        });

        //TextView
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(count));

        //confirm ???
        typeconfirm = findViewById(R.id.typeconfirm);
        dateconfirm = findViewById(R.id.dateconfirm);
        photoconfirm = findViewById(R.id.photoconfirm);
        titleconfirm = findViewById(R.id.titleconfirm);
        contentconfirm= findViewById(R.id.contentconfirm);

        //confirm ?????? ???????????? GONE ????????? ??????
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






        //button
        btn_datePicker = (Button) findViewById(R.id.btn_datePicker);
        btn_add  = (ImageButton) findViewById(R.id.btn_add);
        btn_minus  = (ImageButton) findViewById(R.id.btn_minus);

        btn_minus.setEnabled(false);
        //????????? ??????????????? 2??? ??????????????? ???????????? ?????? ???????????? ????????????.

        btn_ko = (Button)findViewById(R.id.btn_ko);
        btn_glo = (Button)findViewById(R.id.btn_glo);
        btn_next = (Button)findViewById(R.id.btn_next);
        back_btn = (ImageButton)findViewById(R.id.back_btn);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_basicphoto = (Button) findViewById(R.id.btn_basicphoto);


        // ????????? ?????? ???????????? ???????????? ?????? ????????? ??? ??????????????? ??????.
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UcompanyActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ?????? ????????? ??????.
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


        // ??? ?????? ?????? ??????
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                people = tv_count.getText().toString();
                Log.e("people", people);
                Log.e("check", String.valueOf(check));

                // ?????? ??????
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

                // ?????? ??????
                if(btn_datePicker.getText().toString().equals("????????????"))
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

                // ??????
                if(et_title.getText().toString().equals(""))
                {
                    titleconfirm.setVisibility(view.VISIBLE);
                }
                else{
                    // ??????
                    title = et_title.getText().toString();
                    Log.e("title", title);
                }

                // ??????
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
                    // ????????? ????????? ????????? ??????.
                    // ???????????? ????????? ?????????..
                }


                if(typeconfirm.getVisibility() == View.GONE  && photoconfirm.getVisibility() == View.GONE && dateconfirm.getVisibility() == View.GONE &&
                        titleconfirm.getVisibility() == View.GONE && contentconfirm.getVisibility() == View.GONE)
                {

                    if(check == 3) {

                        // ????????? ??????
                        upload(type, people, mainspot, subspot, start, finish, title, content, mainpos, subpos);
                    }
                    else if(check == 2)
                    {
                        // ?????? ????????? ??????
                        String basic = "basic";

                        withoutpic(type, people, mainspot, subspot, start, finish, title, content, basic , mainpos, subpos);

                    }

                }
                else{

                }




            }
        });


        // ???????????? ??????
        btn_ko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_ko.setSelected(true);
                btn_glo.setSelected(false);
                // ????????? select ?????? ??? ?????? ??????????????? ???????????? ???.
                typeconfirm.setVisibility(typeconfirm.GONE);

                spinnerMenu.setVisibility(spinnerMenu.VISIBLE);
                spinnerSub.setVisibility(spinnerSub.VISIBLE);

                //Log.e("ko", btn_ko.getText().toString());


                komenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // ???????????? ????????? ??????
                spinnerMenu.setAdapter(komenuAdapter);

                spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        Log.e("?????? ???", ko_spot[position]);
                        Log.e(TAG, "onItemSelected: position " + position );


                        if(ko_spot[position].equals("?????????"))
                        {
                            jejuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSub.setAdapter(jejuAdapter);
                            MainSpot = ko_spot[position];
                            mainpos = position;
                            Log.e("?????? ???1", ko_spot[position]);

                            spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position2, long l) {
                                    SubSpot = jeju_array[position2];
                                    subpos = position2;
                                    Log.e("?????? ???2", jeju_array[position2]);
                                    Log.e(TAG, "onItemSelected: position2 " + position2 );
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });




                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", seoul_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });


                        }
                        else if(ko_spot[position].equals("?????????"))
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
                                    Log.e("?????? ???2", kyonggi_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });

                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", incheon_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("?????????"))
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
                                    Log.e("?????? ???2", kang_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("????????????"))
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
                                    Log.e("?????? ???2", chungN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });

                        }
                        else if(ko_spot[position].equals("????????????"))
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
                                    Log.e("?????? ???2", chungS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", dae_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", gwang_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("????????????"))
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
                                    Log.e("?????? ???2", junN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("????????????"))
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
                                    Log.e("?????? ???2", junS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("????????????"))
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
                                    Log.e("?????? ???2", kyongN_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("????????????/??????"))
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
                                    Log.e("?????? ???2", kyongS_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", busan_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(ko_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", daegu_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
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

        // ???????????? ??????
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
                // ???????????? ????????? ??????
                spinnerMenu.setAdapter(glomenuAdapter);

                spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        if(glo_spot[position].equals("?????????/??????/?????????"))
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
                                    Log.e("?????? ???2", dongnamah_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("??????/????????????"))
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
                                    Log.e("?????? ???2", europe_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("???/?????????/??????/????????????"))
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
                                    Log.e("?????? ???2", gwam_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("??????/?????????/?????????"))
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
                                    Log.e("?????? ???2", america_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("??????/??????/??????"))
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
                                    Log.e("?????? ???2", china_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
                                }
                            });
                        }
                        else if(glo_spot[position].equals("??????"))
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
                                    Log.e("?????? ???2", japan_array[position2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Log.e("?????? ???2", "??????22");
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


        // ??????????????? 10????????? + ?????? ????????????
        // ???????????? +
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
                // +????????? ?????? 10??? ?????? ??? ?????????????????? minus true?????? +????????? ?????? 3??? ?????? ?????? -????????? true??? ????????? ??????????????? ?????????.

            }
        });

        // ??????????????? 2????????? - ?????? ????????????
        // ???????????? -
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



        // ?????? ?????? ?????? ??????
        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("?????? ?????? ??????") ;

                //?????? ?????? ???????????? ?????? ??????.
                //builder.setSelection(androidx.core.util.Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));

                MaterialDatePicker materialDatePicker = builder.build();


                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(androidx.core.util.Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy. MM. dd");
                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);

                        // dateString1 ?????? ?????? dateString2 ????????? ??????
                        dateString1 = simpleDateFormat.format(date1);
                        dateString2 = simpleDateFormat.format(date2);

                        btn_datePicker.setText(dateString1 + " ~ " + dateString2);

                        if(!btn_datePicker.getText().toString().equals("????????????"))
                        {

                            dateconfirm.setVisibility(View.GONE);

                        }


                    }


                });

            }
        });



    }

    private void withoutpic(String type, String people, String mainspot, String subspot, String startdate, String finishdate, String title, String content, String basic, int mainpos, int subpos){

        retrofitInterface.update(type, startdate, finishdate, people, mainspot, subspot, title, content, basic, user_email, mainpos, subpos, id).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful())
                {
                    CompanyData result = response.body();

                    String status = result.getStatus();
                    String message = result.getMessage();

                    if(status.equals("true"))
                    {
                        Log.e(TAG, "onResponse: file name " + message );
                        Intent intent = new Intent(UcompanyActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Log.e(TAG, "fail222");
                    }


                }
                else
                {
                    Log.e(TAG, "fail333");
                }
            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e(TAG, "?????? = " + t.getMessage());
            }
        });
    }

    // ?????? ??????????????? ??????.
    private void upload(String type, String people, String mainspot, String subspot, String startdate, String finishdate, String title, String content  ,int mainpos, int subpos){

        String path = imagePath;

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) //?????? ?????? ??????
                .build();


        RetrofitInterface uploadapi = retrofit.create(RetrofitInterface.class);

        Log.e(TAG, "upload: mainpos :" + mainpos );
        Log.e(TAG, "upload: subpos : " + subpos );
        Call<CompanyData> call=uploadapi.updateAll(body,type, startdate, finishdate, people, mainspot, subspot, title, content, user_email, mainpos, subpos, id);
        call.enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {

                if(response.isSuccessful()){

                    CompanyData result = response.body();

                    String status = result.getStatus();


                    if(status.equals("true"))
                    {

                        Intent intent = new Intent(UcompanyActivity.this, HomeActivity.class);
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
                    // ??????
                    Log.e(TAG, "onResponse fail");
                }


            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e("onresponse", "?????? : " + t.getMessage());
            }
        });

    }



    // ?????? ?????? ?????? ????????? ???????????????
    private void makeDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(UcompanyActivity.this);
        alt_bld.setTitle("?????? ??????");
        alt_bld.setNeutralButton("??????????????? ??????", new DialogInterface.OnClickListener() {
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
        }).setNegativeButton("???????????? ??????", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(DialogInterface dialog, int i) {


//        ?????? ??????
                boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                if (!hasCamPerm )  // ?????? ?????? ???  ???????????? ??????
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
        alt_bld.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();

            }
        });
        alt_bld.show();
    }

    // ????????? ?????? ??????
    private void OnCameraCheckPermission() {

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ){
                Toast.makeText(this, "?????? ?????? ????????? ???????????? ????????? ???????????? ?????????.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},PERMISSIONS_REQUEST2);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST2);

            }

        }
    }

    // ????????? ?????? ??????
    public void OnCheckPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
                Toast.makeText(this, "?????? ?????? ????????? ???????????? ????????? ???????????? ?????????.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "???????????? ?????? ????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();

                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UcompanyActivity.this);

                    builder.setMessage("???????????? ?????? ?????? ????????? ?????????????????????.\n?????? ????????? ????????? ?????? ????????? ?????? > ?????????????????? ??????????????? ?????? ?????? ????????? ??????????????????.");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }

                break;

            case PERMISSIONS_REQUEST2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "???????????? ?????? ????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(UcompanyActivity.this);

                    builder.setMessage("???????????? ?????? ?????? ????????? ?????????????????????.\n?????? ????????? ????????? ?????? ????????? ?????? > ?????????????????? ??????????????? ?????? ?????? ????????? ??????????????????.");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();


                }

                break;



        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // ????????? ?????? ??????

            switch (requestCode) {
                case GALLERY: // ??????????????? ???????????? ????????? ??????
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        imagePath = cursor.getString(index);
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        Glide.with(this).load(imagePath).into(banner);
                        cursor.close();
                    }
//                    InputStream ?????? ????????? ????????????
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        //???Glide.with(this).load(imagePath).into(banner);
                        //banner.setImageBitmap(bitmap);
                        // ????????? ????????? jpg???????????? ??????
                        // ????????????????????? ????????? ????????? bitmap??? ????????? ?????????. bitmap????????? ????????? ????????? ???????????? ?????? ??? bitmap??? compress ????????? ????????????.

//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream );
//                        // ????????? ??????????????? bitmap ???????????? ???????????? ?????? outputstream??????.
//                        // + input??? ????????? output??? ????????????
//                        // ????????? ??????????????? 40??? ????????? 40%??? ??????????????? ????????????.
//
//

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case CAMERA: // ???????????? ????????? ????????? ??????
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // ????????? ?????? ??????. ??? ???????????? 1/inSampleSize ??? ?????????
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                    //BitmapFactory.decodeFile ????????? ???????????? ????????? ????????? ????????? ??? ??????.
                    //options??? ???????????? ???????????? ????????? ?????? ????????? ?????? ????????????.
                    //banner.setImageBitmap(bitmap);
                    Glide.with(this).load(imagePath).into(banner);
                    // ????????? ????????? jpg???????????? ??????
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream );
                    byte[] byteArray = stream.toByteArray();

                    // setImageBitmap ???????????? load?????? ???.
                    // "bitmap" ??? ????????? ????????????.
                    // ??? ????????? ????????? jpeg ???????????? ???????????? ???.
                    break;
            }

        }
    }


    @SuppressLint("SimpleDateFormat")
    File createImageFile() throws IOException {
//        ????????? ?????? ??????
        String timeStamp = imageDate.format(new Date()); // ????????? ????????? ????????? ?????? "yyyyMMdd_HHmmss"?????? timeStamp
        String fileName = "IMAGE_" + timeStamp; // ????????? ?????? ???
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(fileName, ".jpg", storageDir); // ????????? ?????? ??????
        imagePath = file.getAbsolutePath(); // ?????? ???????????? ????????????
        return file;
    }



}