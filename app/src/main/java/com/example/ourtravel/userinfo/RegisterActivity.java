package com.example.ourtravel.userinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    public final String TAG = "RegisterActivity";

    private EditText etemail, etcode, etnick, etpass, etpassck;
    private Button btnregister, btnnick, btncode, btnemail;
    private TextView emailconfirm, nickconfirm, passconfirm, passckconfirm, codeconfirm, tx_code ;
    private ImageButton btnback;
    private PreferenceHelper preferenceHelper;
    static String emailcode;
    //이메일 인증코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // preferenceHelper 객체를 안 만들어 줘서 오류 생겼었음.
        preferenceHelper = new PreferenceHelper(this);

        // edittext
        etemail = (EditText) findViewById(R.id.et_email);
        etnick = (EditText) findViewById(R.id.et_nick);
        etpass = (EditText) findViewById(R.id.et_pass);
        etpassck = (EditText) findViewById(R.id.et_passck);
        // passwork ck
        etcode = (EditText) findViewById(R.id.et_code);

        // textview
        emailconfirm = (TextView) findViewById(R.id.emailconfirm);
        nickconfirm = (TextView) findViewById(R.id.nickconfirm);
        passconfirm = (TextView) findViewById(R.id.passconfirm);
        passckconfirm = (TextView) findViewById(R.id.passckconfirm);
        codeconfirm = (TextView) findViewById(R.id.codeconfirm);
        tx_code = (TextView)findViewById(R.id.tx_code);

        // button
        btnregister = (Button) findViewById(R.id.btn);
        btnback = (ImageButton) findViewById(R.id.backbtn);
        btncode = (Button) findViewById(R.id.btn_code);

        btnnick = (Button) findViewById(R.id.btnnick);
        btnnick.setEnabled(false);

        btnemail = (Button) findViewById(R.id.btnemail);
        btnemail.setEnabled(false);

        //이메일 인증 코드 보내기 전에는 인증 코드입력(tx), 입력란 ,확인 버튼 invisible 하다가 인증하기 버튼 누르면 visible하게 만들기.
        tx_code.setVisibility(tx_code.GONE);
        etcode.setVisibility(etcode.GONE);
        btncode.setVisibility(btncode.GONE);

        // 이메일 인증코드 입력란
        etemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()){
                    emailconfirm.setText("이메일 형식으로 입력해주세요.");    // 경고 메세지
                    etemail.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    emailconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btnemail.setEnabled(false);
                    tx_code.setVisibility(tx_code.GONE);
                    etcode.setVisibility(etcode.GONE);
                    btncode.setVisibility(btncode.GONE);
                }
                else{
                    emailconfirm.setText("올바른 이메일 입니다.");         //에러 메세지 제거
                    emailconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                    etemail.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                    btnemail.setEnabled(true);


                }
            }// afterTextChanged()..
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        // 이메일 입력후 인증코드 보내기 버튼
        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkEmail();



            }
        });


        btncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etcode.getText().toString().equals(emailcode.toString()))
                {
                    codeconfirm.setText("인증 성공");         //에러 메세지 제거
                    codeconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                    etcode.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경

                }
                else
                {
                    codeconfirm.setText("인증 코드를 다시 확인해주세요.");    // 경고 메세지
                    etcode.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    codeconfirm.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        });


        // 닉네임 설정 방법
        etnick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
                if(!Pattern.matches(pattern,etnick.getText())){
                    nickconfirm.setText("닉네임 설정 방법에 맞지 않습니다.");    // 경고 메세지
                    etnick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btnnick.setEnabled(false);
                    // 공백일때 아닐때 분기 처리해주곤 공백일대 중복확인 막아두기.
                    // 조건 잘 쪼개기

                }
                else if(etnick.getText().toString().equals(""))
                {
                    nickconfirm.setText("공백입니다.");
                    etnick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btnnick.setEnabled(false);
                }
                else{
                    etnick.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                    nickconfirm.setText("중복 확인이 가능합니다.");
                    nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                    btnnick.setEnabled(true);
                }


            }
        });

        // 닉네임 중복 확인
        btnnick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNick();
            }
        });

        // 비밀번호 설정 방법
        etpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", etpass.getText())){
                    passconfirm.setText("비밀번호 설정 방법을 확인해주세요.\n영문, 숫자, 특수문자 포함 8~16자");    // 경고 메세지
                    etpass.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    passconfirm.setTextColor(Color.parseColor("#FF0000"));
                }
                else{
                    passconfirm.setText("확인");         //에러 메세지 제거
                    etpass.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                    passconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                }
            }// afterTextChanged()..
        });

        // 비밀번호 확인
        etpassck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!etpass.getText().toString().equals(etpassck.getText().toString())){
                    passckconfirm.setText("비밀번호가 일치하지 않습니다.");    // 경고 메세지
                    etpassck.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    passckconfirm.setTextColor(Color.parseColor("#FF0000"));
                }
                else{
                    passckconfirm.setText("확인");         //에러 메세지 제거
                    etpassck.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                    passckconfirm.setTextColor(Color.parseColor("#0B2AEA"));

                }
            }// afterTextChanged()..
        });



        // 회원 가입 버튼
        btnregister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(etemail.getText().toString().length() == 0)
                {
                    emailconfirm.setText("이메일을 입력하세요.");    // 경고 메세지
                    etemail.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    etemail.requestFocus();
                }
                else if(etcode.getText().toString().length() == 0)
                {
                    codeconfirm.setText("이메일 인증 코드를 입력하세요.");    // 경고 메세지
                    etcode.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    etcode.requestFocus();
                }
                else if(etnick.getText().toString().length() == 0)
                {
                    nickconfirm.setText("닉네임을 입력하세요.");    // 경고 메세지
                    etnick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    etnick.requestFocus();
                }
                else if(etpass.getText().toString().length() == 0)
                {
                    passconfirm.setText("비밀번호를 입력하세요.");    // 경고 메세지
                    etpass.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    etpass.requestFocus();
                }
                else if(etpassck.getText().toString().length() == 0)
                {
                    passckconfirm.setText("비밀번호 확인을 입력하세요.");    // 경고 메세지
                    etpassck.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    etpassck.requestFocus();
                }
                else{

                    if(nickconfirm.getText().toString().equals("사용가능한 닉네임입니다.") && codeconfirm.getText().toString().equals("인증 성공") && passconfirm.getText().toString().equals("확인") && passckconfirm.getText().toString().equals("확인") ){
                        // 인증코드확인, 닉네임 중복 확인, 비밀번호 설정 확인, 비번 확인

                        registerMe();

                    }

                }




            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void registerMe(){
        final String email = etemail.getText().toString();
        final String nick = etnick.getText().toString();
        final String password = etpass.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface registerapi = retrofit.create(RetrofitInterface.class);

        Call<String> call = registerapi.getUserRegist(email, nick, password);

        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {// onresponse 통신 성공시 callback
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("onSuccess22", response.body());

                    String jsonResponse = response.body();
                    try
                    {
                        Log.e("in try", jsonResponse);
                        RegisterData(jsonResponse);
                        Log.e("next parseRegData", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    private void RegisterData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        Log.e("jsonobject", jsonObject.toString());

        //response는 문자열
        // jsonObject의 getstring() 과 optstring()의 차이
        // getstring의 경우 키에 해당하는 값이 없는 경우 jsonException을 발생시키는 반몀ㄴ
        // optstring은 "" 와 같은 빈 문자열을 반환한다. string 타입의 빈 문자열을 반환한다.
        // key에 해당하는 값이 없을때 반환하는 것.
        if (jsonObject.optString("status").equals("true"))
        {

            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);
            saveInfo(response);
            Log.e("jsonobject3", jsonObject.optString("status"));
            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();


        }
        else
        {

            Toast.makeText(RegisterActivity.this, jsonObject.getString("회원가입에 실패했습니다."), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfo(String response)
    {
        Log.e("next saveInfo", response);
        preferenceHelper.putIsLogin(true);
        Log.e("next preferenceHelper", response);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true"))
            {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++)
                {
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putEmail(dataobj.getString("user_email"));
                    preferenceHelper.putNick(dataobj.getString("user_nick"));
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    class MailTread extends Thread{
        public void run()
        { GMailSender gMailSender = new GMailSender("monspirit47@gmail.com", "wujazmnpbolztnnb");
            emailcode=gMailSender.getEmailCode();
            //인증코드
            String content = "안녕하세요. OurTravel입니다. \r\n회원가입시 필요한 인증코드 메일입니다. \r\n아래 인증코드를 인증코드 기입란에 입력하시기 바랍니다. \r\n 인증코드: " + emailcode;
            try {
                gMailSender.sendMail("OurTravel 이메일 인증", content , etemail.getText().toString());
            }
            catch (SendFailedException e) {
                System.out.println("SendFailedException 문제"+e);
            }
            catch (MessagingException e) {
                System.out.println("인터넷 문제"+e);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkNick(){
        final String user_nick = etnick.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface api = retrofit.create(RetrofitInterface.class);

        Call<String> call = api.getUserNick(user_nick);

        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {// onresponse 통신 성공시 callback
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("onSuccess22", response.body());

                    String jsonResponse = response.body();
                    try
                    {
                        Log.e("in try", jsonResponse);
                        parseRegData(jsonResponse);
                        Log.e("next parseRegData", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

    }

    private void parseRegData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        Log.e("jsonobject", jsonObject.toString());

        //response는 문자열
        // jsonObject의 getstring() 과 optstring()의 차이
        // getstring의 경우 키에 해당하는 값이 없는 경우 jsonException을 발생시키는 반몀ㄴ
        // optstring은 "" 와 같은 빈 문자열을 반환한다. string 타입의 빈 문자열을 반환한다.
        // key에 해당하는 값이 없을때 반환하는 것.
        if (jsonObject.optString("status").equals("true"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);
            //saveInfo(response);
            Log.e("jsonobject3", jsonObject.optString("status"));

            etnick.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
            nickconfirm.setText("사용가능한 닉네임입니다.");
            nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));

        }
        else
        {
            nickconfirm.setText("이미 사용중인 닉네임 입니다.\n다른 닉네임을 입력해주세요.");    // 경고 메세지
            etnick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
            nickconfirm.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void checkEmail(){
        final String user_email = etemail.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL) //php 파일 경로
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface emailapi = retrofit.create(RetrofitInterface.class);

        Call<String> call = emailapi.getUserEmail(user_email);

        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {// onresponse 통신 성공시 callback
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("onSuccess22", response.body());

                    String jsonResponse = response.body();
                    try
                    {
                        Log.e("in try", jsonResponse);
                        EmailData(jsonResponse);
                        Log.e("next parseRegData", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
    private void EmailData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        Log.e("jsonobject", jsonObject.toString());

        //response는 문자열
        // jsonObject의 getstring() 과 optstring()의 차이
        // getstring의 경우 키에 해당하는 값이 없는 경우 jsonException을 발생시키는 반몀ㄴ
        // optstring은 "" 와 같은 빈 문자열을 반환한다. string 타입의 빈 문자열을 반환한다.
        // key에 해당하는 값이 없을때 반환하는 것.
        if (jsonObject.optString("status").equals("true"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));
            etemail.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
            emailconfirm.setText("이메일로 인증번호가 전송되었습니다.\n인증번호를 입력해주세요.");
            emailconfirm.setTextColor(Color.parseColor("#0B2AEA"));

            MailTread mailTread = new MailTread();
            //메일을 보내주는 쓰레드
            mailTread.start();
            Toast.makeText(getApplicationContext(), "인증코드가 발송되었습니다.", Toast.LENGTH_LONG).show();

            tx_code.setVisibility(tx_code.VISIBLE);
            etcode.setVisibility(etcode.VISIBLE);
            btncode.setVisibility(btncode.VISIBLE);

        }
        else
        {
            emailconfirm.setText("이미 사용중인 이메일 ID 입니다\n다른 이메일 주소를 입력해주세요.");    // 경고 메세지
            etemail.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
            emailconfirm.setTextColor(Color.parseColor("#FF0000"));
        }
    }






    }