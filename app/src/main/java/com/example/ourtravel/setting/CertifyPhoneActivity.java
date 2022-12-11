package com.example.ourtravel.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.LoginActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertifyPhoneActivity extends AppCompatActivity {

    final String TAG = getClass().getName();

    EditText inputPhoneNum; // 전화번호 입력 칸
    Button sendSMSbt; // 인증 번호 전송 버튼

    EditText inputCheckNum; // 인증 번호 작성 칸
    Button checkBt; // 인증번화 확인 버튼

    TextView tx_noti; // 전화번호 작성후 유저에게 알려주는 알림 text

    static final int SMS_SEND_PERMISSION = 1;

    String checkNum; // 인증 번호
    String phoneNum; // 유저가 입력한 전화번호

    private PreferenceHelper preferenceHelper; // 쉐어드 객체 생성하기.
    String user_phone; // 유저 전화번호
    String user_email; // 유저 이메일

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 전화번호 입력 후 인증번호 받기 버튼을 누르지 않으면 인증번호 입력 칸이 나오지 않고
    // 그 이전에 전화번호를 입력하지 않으면 인증번호 받기 버튼이 비활성화 된다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify_phone);

        preferenceHelper = new PreferenceHelper(this); // 유저 정보 담고 있는 쉐어드
        user_phone = preferenceHelper.getPHONE(); // 유저 전화번호 받기
        user_email = preferenceHelper.getEmail(); // 유저 이메일 찾기

        // 인증번호 담는 쉐어드 생성하기
        SharedPreferences pref = getSharedPreferences("certifyNum", MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        SharedPreferences.Editor editor1 = preferences.edit();
        Log.e(TAG, "onCreate: pref = " + pref );

        // 레트로핏 통신 위한 객체 생성하기
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // edittext
        inputPhoneNum = findViewById(R.id.input_phone_num); // 전화번호 입력 칸
        inputCheckNum = findViewById(R.id.input_check_num); // 인증번호 입력 칸

        // button
        sendSMSbt = findViewById(R.id.send_sms_button); // 인증번호 작성 칸
        checkBt = findViewById(R.id.check_button); // 인증 완료 버튼

        // textview
        tx_noti = findViewById(R.id.tx_noti); // 전화번호 작성후 유저에게 알려주는 text

        // 전화번호 입력전이라서 인증번호 전송 버튼이 비활성화 되어야 한다.
        // 전화번호 입력 칸이 공백인데 인증번호 전송 버튼을 눌렀다면 전화번호 입력해달라는 메세지가 나와야 한다.
        // 인증 번호 작성 칸과 인증 완료 버튼은 gone 상태에서 전화번호 입력하고 인증번호 전송했다면 visible 상태가 되어야 한다.
        inputCheckNum.setVisibility(inputCheckNum.GONE);
        checkBt.setVisibility(checkBt.GONE);


        // 문자 보내기 권환 확인
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        String[] permissions = {Manifest.permission.SEND_SMS};

        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            // 문자 보내기 권한 거부
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS))
            {
                Toast.makeText(getApplicationContext(), "SMS 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            // 문자 보내기 권한 허용
            ActivityCompat.requestPermissions(this, permissions, SMS_SEND_PERMISSION);
        }

        inputPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!inputPhoneNum.getText().toString().replace(" ", "").equals(""))
                {
                    tx_noti.setText("");

                    sendSMSbt.setEnabled(true); // 인증번호 전송 버튼 비활성화

                    // 전화번호를 입력하면 버튼이 활성화가 되어야 한다.
                }
            }
        });

        // 인증번호 받기 버튼 클릭
        sendSMSbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 전화번호가 공백이라면
                if(inputPhoneNum.getText().toString().replace(" ", "").equals(""))
                {
                    tx_noti.setText("전화 번호 입력해주세요!");
                    tx_noti.setTextColor(Color.parseColor("#E10404"));
                    sendSMSbt.setEnabled(false); // 인증번호 전송 버튼 비활성화

                    // 전화번호를 입력하면 버튼이 활성화가 되어야 한다.
                }
                else{

                    // 전화번호 입력하고 버튼 클릭했을 때 이 사람의 전화번호를 서버에서 찾아서 확인..?
                    // 있으면 이미 인증 완료한 휴대전화입니다. 알림.
                    // 0 이면 인증번호 입력하라고 알림.
                    // 로그인 할때 유저의 전화번호도 쉐어드에 저장하는 걸로 진행 하기. -> 로그인시 쉐어드에 유저 폰 번호 저장함.
                    if(user_phone.equals("0"))
                    {
                        // 전화번호 인증하지 않았다는 의미
                        tx_noti.setText("인증번호를 입력해주세요!");
                        tx_noti.setTextColor(Color.parseColor("#0B2AEA"));
                        inputCheckNum.setVisibility(inputCheckNum.VISIBLE);
                        checkBt.setVisibility(checkBt.VISIBLE);

                        checkNum = numberGen(4, 1);

                        // 쉐어드에 난수 저장하기
                        editor.putString("checkNum", checkNum);
                        editor.apply();
                        Log.e(TAG, "onClick: checknum in pref = " + pref.getString("checkNum", "") );
                        phoneNum = inputPhoneNum.getText().toString();
                        sendSMS(phoneNum, "인증번호 : "+checkNum);
                    }
                    else{
                        // 전화번호 인증 받았다는 의미
                        tx_noti.setText("이미 인증 완료한 휴대전화입니다.");
                        tx_noti.setTextColor(Color.parseColor("#E10404"));
                        sendSMSbt.setEnabled(false); // 인증번호 전송 버튼 비활성화
                    }





                }




            }
        });

        // 인증번호 체크하는 버튼
        checkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 입력한 인증번호와 저장한 인증번호 체크
                if(pref.getString("checkNum", "").equals(inputCheckNum.getText().toString())){
                    Toast.makeText(getApplicationContext(), "인증 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                    // 인증 완료가 되었으니 내 프로필로 돌아가서 번호 인증된거 확인해야 함.
                    // 인증 완료되었을때 유저의 전화번호 userinfo에 전화번호 저장하기
                    // 기본값 0으로 두고 전화번호 column이 0이면 전화번호 인증 안한것
                    // 0이 아니면 전화번호 인증을 한 것 이니까
                    // onactivityforresult를 해야하는 건지 안해도 되는건지 모르겠다.
                    // 일단 db에 전화번호를 저장하는 것부터 진행하기

                    // 현재까지 진행된 상황 shared에 유저 번호 저장하고 인증을 했는지 안했는지 판단해서 인증번호 전송하는것까지 진행함.
                    // 서버에 유저의 전화번호를 저장하는것 진행해야 함.
                    Log.e(TAG, "onClick: phoneNum = " + phoneNum );
                    retrofitInterface.phoneNum(user_email, phoneNum).enqueue(new Callback<UserData>() {
                        @Override
                        public void onResponse(Call<UserData> call, Response<UserData> response) {

                            if(response.isSuccessful() && response.body() != null)
                            {
                                UserData result = response.body();
                                Log.e(TAG, "전화번호 저장 후 onResponse: result = " + result );
                                String status = result.getStatus();

                                if(status.equals("true"))
                                {
                                    // 유저 전화번호가 저장되었으니 다른 액티비티로 넘어 가야 함.
                                    // 프로필로 넘어가기
                                    // 쉐어드에 있는 유저 번호를 바꿔줘야 함.
                                    editor1.putString("phone", phoneNum);
                                    editor1.commit();

                                    Intent intent = new Intent(CertifyPhoneActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }


                        }

                        @Override
                        public void onFailure(Call<UserData> call, Throwable t) {

                        }
                    });



                }else
                {
                    Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // SMS 발송 기능능
    private void sendSMS(String phoneNumber, String message)
    {
        //S+ 이상 (버전 31이상)을 타겟팅하려면 pendingintent를 생성할 때 FLAG_IMMUTABLE 또는 FLAG_MUTABLE 중 하나를 지정해야 한다.
        //FLAG_IMMUTABLE 사용을 강력히 고려하고 일부 기능이 변경 가능한 pendingintent에 의존하는 경우에만 fLAG_MUTABLE 을 사용하라고 한다.
        // -> gradle에 의존성을 추가하면 된다.

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, "Activity_name".getClass()), PendingIntent.FLAG_IMMUTABLE);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);

        Toast.makeText(getBaseContext(), "메세지가 전송 되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // len : 생성할 난수의 길이
    // dupCd : 중복 허용 여부 (1:중복 허용, 2:중복 제거)

    public static String numberGen(int len, int dupCd){

        Random rand = new Random();
        String numStr = ""; // 난수가 저장될 변수

        for(int i=0; i<len; i++){

            // 0~9까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd == 1){
                // 중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2){
                // 중복을 허용하지 않을시 중복된 값이 있는지 검사한다.
                if(!numStr.contains(ran))
                // 중복된 값이 없으면 numStr에 append
                {
                    numStr += ran;
                }else{
                    //생성된 난수가 중복되면 루틴을 다시 실행한다.
                    i -= 1;
                }
            }

        }
        return numStr;
    }



}





















