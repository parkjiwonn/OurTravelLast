package com.example.ourtravel.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ourtravel.R;

public class FindRouteActivity extends AppCompatActivity {

    // creating variables for
    // edit texts and button.
    TextView sourceEdt, destinationEdt;
    Button trackBtn;
    String currentLocation;
    ImageButton btn_convert;
    boolean click;

    String from;
    String to;

    String destination; // 주소 검색해서 경로로 넘어온 경우

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route);


        // map 액티비티 -> 경로 찾기

        Log.e(TAG, "onCreate: currentLocation = " + MapActivity.current_location );
        currentLocation = MapActivity.current_location; // 현재 위치
        // 출발은 항상 내 위치로 고정하기
        // LocationResult 에서 경로 구하는 액티비티로 이동했을 경우에는 도착에 검색결과 넣기

        btn_convert = findViewById(R.id.btn_convert);

        // initializing our edit text and buttons
        sourceEdt = findViewById(R.id.idEdtSource);
        sourceEdt.setText("내 위치");

        destinationEdt = findViewById(R.id.idEdtDestination);
        trackBtn = findViewById(R.id.idBtnTrack);

        Intent intent = getIntent();

        if(!TextUtils.isEmpty(intent.getStringExtra("destination")))
        {
            destination = (String)intent.getExtras().get("destination");
            Log.e(TAG, "onCreate: 주소 검색해서 넘어옴" );
            destinationEdt.setText(destination);
        }
        else{
            Log.e(TAG, "onCreate: 주소 검색후 넘어온게 아님" );
        }



        // 원래 출발에 있었던 text
        from = sourceEdt.getText().toString();
        to = destinationEdt.getText().toString();

        // 클릭시 출발과 도착 바꾸기
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 원래 도착에 있었던 text

                if(click)
                {
                    // 내 위치가 도착으로 가야함
                    sourceEdt.setText(to);
                    destinationEdt.setText(from);
                    click = false;
                }
                else{
                    sourceEdt.setText(from);
                    destinationEdt.setText(to);
                    click = true;
                }

            }
        });

        // 내 위치는 고정되지만 내 위치 바꾸고 싶은 사람을 위해 내 위치 클릭하면 주소 검색으로 넘어가게 하기
        sourceEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindRouteActivity.this, SearchDestinationActivity.class);
                startActivityForResult(intent, 2000);

            }
        });

        // 도착도 클릭하면 주소 검색으로 넘어가게 하기
        destinationEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindRouteActivity.this, SearchDestinationActivity.class);
                startActivityForResult(intent, 3000);

            }
        });

        // adding on click listener to our button.
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to draw a track on google maps.

                if(to.equals("도착"))
                {
                    // 도착지 선택을 안하고 클릭했다는 것.
                    AlertDialog.Builder dig_worning = new AlertDialog.Builder(FindRouteActivity.this);
                    dig_worning.setMessage("정확한 주소를 입력해주세요");
                    dig_worning.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dig_worning.show();
                }
                else{
                    // 출발 위치가 내위치인지 아니면 유저가 따로 고른 출발지인지 나눠줘야 함.
                    String my_location = sourceEdt.getText().toString();
                    if(my_location.equals("내 위치"))
                    {
                        drawTrack(currentLocation, destinationEdt.getText().toString());
                    }
                    else{
                        drawTrack(sourceEdt.getText().toString(), destinationEdt.getText().toString());
                    }
                }



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2000){
            // 출발지 선택해서 온 것
            if(resultCode == Activity.RESULT_OK)
            {
                sourceEdt.setText(data.getStringExtra("location"));
                from = data.getStringExtra("location");
            }
        }
        else{
            // 도착지 선택해서 온 것
            if(resultCode == Activity.RESULT_OK)
            {
                destinationEdt.setText(data.getStringExtra("location"));
                to = data.getStringExtra("location");
            }
        }
    }

    private void drawTrack(String source, String destination) {
        try {
            // create a uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + source + "/" + destination);

            // initializing a intent with action view.
            Intent i = new Intent(Intent.ACTION_VIEW, uri);

            // below line is to set maps package name
            i.setPackage("com.google.android.apps.maps");

            // below line is to set flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // start activity
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // when the google maps is not installed on users device
            // we will redirect our user to google play to download google maps.
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");

            // initializing intent with action view.
            Intent i = new Intent(Intent.ACTION_VIEW, uri);

            // set flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // to start activity
            startActivity(i);
        }
    }
}