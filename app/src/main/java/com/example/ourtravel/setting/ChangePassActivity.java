package com.example.ourtravel.setting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtravel.userinfo.PreferenceHelper;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {

    private EditText et_nowpass, et_pass, et_passck;
    private TextView nowconfirm, passconfirm, passckconfirm;
    private TextView tx_1, tx_2, tx_3;
    private Button btn_check, btn_change;
    private ImageButton btn_Back;
    private PreferenceHelper preferenceHelper;
    final String TAG = "ChangePassActivity";

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String id;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        preferenceHelper = new PreferenceHelper(this);

        //editText
        et_nowpass = findViewById(R.id.et_nowpass);
        et_pass = findViewById(R.id.et_pass);
        et_passck = findViewById(R.id.et_passck);

        //Textview
        nowconfirm = findViewById(R.id.nowconfirm);
        passconfirm = findViewById(R.id.passconfirm);
        passckconfirm = findViewById(R.id.passckconfirm);
        tx_1 = findViewById(R.id.tx_1);
        tx_2 = findViewById(R.id.tx_2);
        tx_3 = findViewById(R.id.tx_3);

        //Button
        btn_change = findViewById(R.id.btn_change);
        btn_check = findViewById(R.id.btn_check);
        btn_Back = findViewById(R.id.btn_Back);

        et_pass.setVisibility(View.GONE);
        passconfirm.setVisibility(View.GONE);
        et_passck.setVisibility(View.GONE);
        passckconfirm.setVisibility(View.GONE);
        btn_change.setVisibility(View.GONE);
        tx_1.setVisibility(View.GONE);
        tx_2.setVisibility(View.GONE);
        tx_3.setVisibility(View.GONE);

        //Button
        // ?????? ???????????? ??????

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChangePassActivity.this, AcountActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = preferenceHelper.getEmail();
                Log.e(TAG,"email : "+ id);
                password = et_nowpass.getText().toString();

                retrofitClient = RetrofitClient.getInstance();
                retrofitInterface = RetrofitClient.getRetrofitInterface();

                retrofitInterface.getUserLogin(id, password).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e(TAG, "onsuccess : " + response.body());

                            String jsonResponse = response.body();
                            parseLoginData(jsonResponse);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.e(TAG, "?????? = " + t.getMessage());

                    }
                });


            }
        });

        // ???????????? ??????

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(passconfirm.getText().toString().equals("??????") && passckconfirm.getText().toString().equals("??????") ){
                    // ??????????????????, ????????? ?????? ??????, ???????????? ?????? ??????, ?????? ??????

                    updateMe();

                }


            }
        });


    }

    private void updateMe(){

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        final String user_pass = et_pass.getText().toString().trim();
        final String user_email = preferenceHelper.getEmail();

        retrofitInterface.getUserpass(user_pass,user_email).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e(TAG, "onsuccess : " + response.body());

                    String jsonResponse = response.body();
                    PassData(jsonResponse);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Log.e(TAG, "?????? = " + t.getMessage());

            }
        });

    }


    private void PassData(String response)
    {
        try
    {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getString("status").equals("true"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassActivity.this);

            builder.setMessage("??????????????? ?????????????????????.");


            //  setPositiveButton -> "OK"??????  //
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ChangePassActivity.this, AcountActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

            builder.show();



        }
        else
        {
            Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????.\n?????? ??????????????????.", Toast.LENGTH_LONG).show();
        }
    }
    catch (JSONException e)
    {
        e.printStackTrace();
    }

    }

    private void parseLoginData(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true"))
            {

                nowconfirm.setText("??????");         //?????? ????????? ??????
                et_nowpass.setBackgroundResource(R.drawable.white_edittext);  //????????? ???????????? ??????
                nowconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                et_pass.setVisibility(View.VISIBLE);
                passconfirm.setVisibility(View.VISIBLE);
                et_passck.setVisibility(View.VISIBLE);
                passckconfirm.setVisibility(View.VISIBLE);
                btn_change.setVisibility(View.VISIBLE);
                tx_1.setVisibility(View.VISIBLE);
                tx_2.setVisibility(View.VISIBLE);
                tx_3.setVisibility(View.VISIBLE);

                // ???????????? ?????? ??????
                et_pass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", et_pass.getText())){
                            passconfirm.setText("???????????? ?????? ????????? ??????????????????.\n??????, ??????, ???????????? ?????? 8~16???");    // ?????? ?????????
                            et_pass.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
                            passconfirm.setTextColor(Color.parseColor("#FF0000"));
                        }
                        else{
                            passconfirm.setText("??????");         //?????? ????????? ??????
                            et_pass.setBackgroundResource(R.drawable.white_edittext);  //????????? ???????????? ??????
                            passconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                        }
                    }// afterTextChanged()..
                });

                // ???????????? ??????
                et_passck.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!et_pass.getText().toString().equals(et_passck.getText().toString())){
                            passckconfirm.setText("??????????????? ???????????? ????????????.");    // ?????? ?????????
                            et_passck.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
                            passckconfirm.setTextColor(Color.parseColor("#FF0000"));
                        }
                        else{
                            passckconfirm.setText("??????");         //?????? ????????? ??????
                            et_passck.setBackgroundResource(R.drawable.white_edittext);  //????????? ???????????? ??????
                            passckconfirm.setTextColor(Color.parseColor("#0B2AEA"));

                        }
                    }// afterTextChanged()..
                });



            }
            else
            {
                nowconfirm.setText("???????????? ?????? ????????? ??????????????????.\n??????, ??????, ???????????? ?????? 8~16???");    // ?????? ?????????
                et_nowpass.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
                nowconfirm.setTextColor(Color.parseColor("#FF0000"));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
}