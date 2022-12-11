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

public class FindPasswordActivity extends AppCompatActivity {

    public final String TAG = "FindPasswordActivity";
    private EditText etemail, etcode, etpass, etpassck;
    private TextView tx_code, emailconfirm, codeconfirm, passconfirm, passckconfirm;
    private Button btnemail, btncode, btnpass;
    private ImageButton btnback;
    static String emailcode;
    //이메일 인증 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        //edit text
        etemail = (EditText) findViewById(R.id.et_email);
        etcode = (EditText) findViewById(R.id.et_code);
        etpass = (EditText) findViewById(R.id.et_pass);
        etpassck = (EditText) findViewById(R.id.et_passck);

        //text view
        tx_code = (TextView) findViewById(R.id.tx_code);
        emailconfirm = (TextView) findViewById(R.id.emailconfirm);
        codeconfirm = (TextView) findViewById(R.id.codeconfirm);
        passconfirm = (TextView) findViewById(R.id.passconfirm);
        passckconfirm = (TextView) findViewById(R.id.passckconfirm);

        //button
        btnemail = (Button) findViewById(R.id.btn_email);
        btncode = (Button) findViewById(R.id.btn_code);
        //완료버튼
        btnpass = (Button) findViewById(R.id.btn_pass);

        //imagebutton
        btnback = (ImageButton) findViewById(R.id.backbtn);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
        btnpass.setOnClickListener(new View.OnClickListener()
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

                    if(codeconfirm.getText().toString().equals("인증 성공") && passconfirm.getText().toString().equals("확인") && passckconfirm.getText().toString().equals("확인") ){
                        // 인증코드확인, 닉네임 중복 확인, 비밀번호 설정 확인, 비번 확인

                        updateMe();

                    }

                }




            }
        });




    }

    private void updateMe(){
        final String user_pass = etpass.getText().toString().trim();;
        final String user_email = etemail.getText().toString().trim();;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL) //php 파일 경로
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface passapi = retrofit.create(RetrofitInterface.class);

        Call<String> call = passapi.getUserpass(user_pass, user_email);

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
                        PassData(jsonResponse);
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
    private void PassData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        Log.e("jsonobject", jsonObject.toString());

        //response는 문자열
        // jsonObject의 getstring() 과 optstring()의 차이
        // getstring의 경우 키에 해당하는 값이 없는 경우 jsonException을 발생시키는 반몀ㄴ
        // optstring은 "" 와 같은 빈 문자열을 반환한다. string 타입의 빈 문자열을 반환한다.
        // key에 해당하는 값이 없을때 반환하는 것.
        if (jsonObject.optString("status").equals("false"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));

            Toast.makeText(getApplicationContext(), "비밀번호 찾기에 실패했습니다.\n다시 시도해주세요.", Toast.LENGTH_LONG).show();



        }
        else
        {
            Toast.makeText(getApplicationContext(), "비밀번호 찾기 성공!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

    }

    class MailTread extends Thread{
        public void run()
        { GMailSender gMailSender = new GMailSender("monspirit47@gmail.com", "utbdlsiejauimhzr");
            emailcode=gMailSender.getEmailCode();
            //인증코드
            String content = "안녕하세요. OurTravel입니다. \r\n비밀번호 찾기에 필요한 인증코드 메일입니다. \r\n아래 인증코드를 인증코드 기입란에 입력하시기 바랍니다. \r\n 인증코드: " + emailcode;
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
        if (jsonObject.optString("status").equals("false"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));
            etemail.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
            emailconfirm.setText("이메일로 인증번호가 전송되었습니다.\n인증번호를 입력해주세요.");
            emailconfirm.setTextColor(Color.parseColor("#0B2AEA"));

            FindPasswordActivity.MailTread mailTread = new FindPasswordActivity.MailTread();
            //메일을 보내주는 쓰레드
            mailTread.start();
            Toast.makeText(getApplicationContext(), "인증코드가 발송되었습니다.", Toast.LENGTH_LONG).show();

            tx_code.setVisibility(tx_code.VISIBLE);
            etcode.setVisibility(etcode.VISIBLE);
            btncode.setVisibility(btncode.VISIBLE);

        }
        else
        {
            emailconfirm.setText("등록되지 않은 이메일입니다.\n다른 이메일 주소를 입력해주세요.");    // 경고 메세지
            etemail.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
            emailconfirm.setTextColor(Color.parseColor("#FF0000"));
        }
    }
}