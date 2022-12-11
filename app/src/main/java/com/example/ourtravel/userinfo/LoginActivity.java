package com.example.ourtravel.userinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ourtravel.home.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private Button btnregister, btnfindpass, btn_login;
    private EditText et_id, et_pass;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceHelper = new PreferenceHelper(this);

        et_id = (EditText)findViewById(R.id.et_id);
        et_pass = (EditText)findViewById(R.id.et_pass);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });



        btnregister = (Button) findViewById(R.id.btnregister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnfindpass =(Button)findViewById(R.id.btnfindpass);
        btnfindpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser(){
        final String id = et_id.getText().toString().trim();
        final String password = et_pass.getText().toString().trim();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.RETROFIT_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface api = retrofit.create(RetrofitInterface.class);
        Call<String> call = api.getUserLogin(id, password);
        // 사용할 메서드 선언
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("onSuccess22", response.body());

                    String jsonResponse = response.body();
                    parseLoginData(jsonResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    private void parseLoginData(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true"))
            {
                saveInfo(response);
                Toast.makeText(LoginActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void saveInfo(String response)
    {
        preferenceHelper.putIsLogin(true);
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true"))
            {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++)
                {
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    Log.e(TAG, "saveInfo: dataobj = " +dataobj );
                    preferenceHelper.putEmail(dataobj.getString("user_email"));
                    preferenceHelper.putNick(dataobj.getString("user_nick"));
                    preferenceHelper.putPROFILE(dataobj.getString("user_photo"));
                    Log.e(TAG, "saveInfo: user_phone = " + dataobj.getString("user_phone") );
                    preferenceHelper.putPHONE(dataobj.getString("user_phone"));
                    // 프로필도 저장하기.
                    preferenceHelper.putSCORE(dataobj.getString("user_score"));
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}