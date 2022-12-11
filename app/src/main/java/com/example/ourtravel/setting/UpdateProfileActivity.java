package com.example.ourtravel.setting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UpdateProfileActivity extends AppCompatActivity {


    public final String TAG = "ProfileActivity";

    private TextView nickconfirm, textView;
    private EditText et_nick, et_produce;
    private Button btn_nick, btn_modify, btn_photo , btn_basicphoto;
    private ImageButton btn_back;
    private ImageView user_img;

    int img_check = 1;
    // 이미지 선택 안하면 기본 값 = 1 / 이미지 선택하면 2로 변경.
    int check = 1;

    String updateProduce;
    String updateNick;

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

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    private PreferenceHelper preferenceHelper;
    String user_email;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        preferenceHelper = new PreferenceHelper(this);

        //text view
        nickconfirm = findViewById(R.id.nickconfirm);
        textView = findViewById(R.id.textView);

        //edit text
        et_nick = findViewById(R.id.et_nick);
        et_produce = findViewById(R.id.et_produce);

        //button
        btn_nick = findViewById(R.id.btn_nick);
        btn_nick.setEnabled(false);
        btn_modify = findViewById(R.id.btn_modify);
        btn_photo = findViewById(R.id.btn_photo);
        btn_basicphoto = findViewById(R.id.btn_basicphoto);
        btn_back = findViewById(R.id.btn_back);

        //imageview
        user_img = findViewById(R.id.img);

        // 프로필 뷰

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        user_email = preferenceHelper.getEmail();

        Log.e(TAG, "onCreate : " + user_email );




        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if(response.isSuccessful())
                {
                    Log.e(TAG, "success"  );
                    UserData result = response.body();
                    Log.e(TAG, "response : " + response );

                    String user_nick = result.getUser_nick();
                    String user_produce = result.getUser_produce();
                    String status = result.getStatus();
                    String photo = result.getPhoto();

                    Log.e(TAG, "user nick : " + user_nick );
                    Log.e(TAG, "user produce : " + user_produce );

                    if(status.equals("true"))
                    {
                        et_nick.setText(user_nick);
                        et_produce.setText(user_produce);
                        //Log.e(TAG, "onResponse: user photo : " + photo );

                        if(photo.equals("basic"))
                        {
                            user_img.setImageResource(R.drawable.profile2);
                        }
                        else{

                            String url = "http://3.39.168.165/userimg/" + photo;
                            //Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(UpdateProfileActivity.this).load(url).into(user_img);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "onResponse false");
                    }

                }
                else
                {
                    Log.e(TAG, "onResponse fail");
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateProfileActivity.this, AcountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 사진 선택안하고 닉네임, 자기소개만 변경할 때
                if(check == 1)
                {
                    Log.e(TAG, "onClick: emial : " + user_email );
                    updateNick = et_nick.getText().toString();
                    updateProduce = et_produce.getText().toString();

                    retrofitInterface.withoutpic(user_email, updateNick, updateProduce).enqueue(new Callback<UserData>() {
                        @Override
                        public void onResponse(Call<UserData> call, Response<UserData> response) {

                            if(response.isSuccessful())
                            {
                                UserData result = response.body();
                                String upnick = result.getUser_nick();
                                String upproduce = result.getUser_produce();
                                String status = result.getStatus();

                                if(status.equals("true"))
                                {
                                    Log.e(TAG, "onResponse of upnick: " + upnick );
                                    Log.e(TAG, "onResponse of upproduce: " + upproduce );

                                    Intent intent = new Intent(UpdateProfileActivity.this, AcountActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                            else
                            {
                                Log.e(TAG, "fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<UserData> call, Throwable t) {

                            // 실패
                            Log.e(TAG, "에러 = " + t.getMessage());
                        }
                    });

                }
                // 사진 변경할 때
                else
                {
                    if(img_check == 1 )
                    {
                        //기본 이미지
                        Log.e(TAG, "modify: " + img_check );

                        String updateNick2 = et_nick.getText().toString();
                        String updateProduce2 = et_produce.getText().toString();
                        String basic = "basic";

                        retrofitInterface.UpdateUserInfo(user_email, updateNick2, updateProduce2, basic).enqueue(new Callback<UserData>() {
                            @Override
                            public void onResponse(Call<UserData> call, Response<UserData> response) {
                                if(response.isSuccessful())
                                {
                                    UserData result = response.body();
                                    String status = result.getStatus();
                                    String message = result.getMessage();
                                    String nick = result.getUser_nick();
                                    String produce = result.getUser_produce();
                                    String photo = result.getPhoto();

                                    if(status.equals("true"))
                                    {
                                        Log.e(TAG, "onResponse: nick : " + nick );
                                        Log.e(TAG, "onResponse: produce : " + produce );
                                        Log.e(TAG, "onResponse: photo : " + photo );

                                        Intent intent = new Intent(UpdateProfileActivity.this, AcountActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{

                                        Log.e(TAG, "기본 이미지 선택 후 modify fail");
                                        Log.e(TAG, "onResponse: " + message );
                                        Log.e(TAG, "onResponse: photo : " + photo );
                                    }
                                }
                                else{

                                    Log.e(TAG, "기본 이미지 선택 후 modify onResponse fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<UserData> call, Throwable t) {
                                Log.e(TAG, "기본 이미지 선택 후 modify 에러 = " + t.getMessage());
                            }
                        });



                    }
                    else
                    {
                        // 기본 이미지 X 사진 선택해서 이미지 업로드 했을 때
                        Log.e(TAG, "modify: " + img_check );
                        Log.e(TAG, "onClick: imagepath"+imagePath);
                        String nickWithpic = et_nick.getText().toString();
                        String produceWithpic = et_produce.getText().toString();
                        profileUpload(nickWithpic, produceWithpic);
                        Intent intent = new Intent(UpdateProfileActivity.this, AcountActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }


            }
        });

        // 기본 이미지 선택하기
        btn_basicphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_img.setImageResource(R.drawable.profile2);

                img_check = 1;
                check = 2;

            }
        });

        // 사진 선택하기.
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog();
                img_check = 2;
                check = 2;

            }
        });


        // 닉네임
        et_nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
                if(!Pattern.matches(pattern,et_nick.getText())){
                    nickconfirm.setText("닉네임 설정 방법에 맞지 않습니다.");    // 경고 메세지
                    et_nick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btn_nick.setEnabled(false);
                    // 공백일때 아닐때 분기 처리해주곤 공백일대 중복확인 막아두기.
                    // 조건 잘 쪼개기

                }
                else if(et_nick.getText().toString().equals(""))
                {
                    nickconfirm.setText("공백입니다.");
                    et_nick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btn_nick.setEnabled(false);
                }
                else{
                    et_nick.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
                    nickconfirm.setText("중복 확인이 가능합니다.");
                    nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                    btn_nick.setEnabled(true);
                }


            }
        });

        // 닉네임 중복 확인
        btn_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNick();
            }
        });

        // 자기소개
        et_produce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = et_produce.getText().toString();
                textView.setText(input.length()+" / 60 글자 수");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // 프로필 수정 메서드
    private void profileUpload(String nick, String produce){

        String path = imagePath;
        Log.d(TAG, "profileUpload: path = "+path);

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        retrofitInterface.UpUserInfoWith(body, nick, produce, user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful()){

                    UserData result = response.body();
                    String status = result.getStatus();
                    String message = result.getMessage();
                    String nick = result.getUser_nick();
                    String produce = result.getUser_produce();
                    String photo = result.getPhoto();

                    if(status.equals("true"))
                    {
                        Log.e(TAG, "onResponse: true : message" + message );
                        Log.e(TAG, "onResponse: true : nick" + nick );
                        Log.e(TAG, "onResponse: true : produce" + produce );
                        Log.e(TAG, "onResponse: true : photo" + photo );
                    }
                    else
                    {
                        Log.e(TAG, "onResponse: true : message" + message );
                    }
                }
                else
                {
                    Log.e(TAG, "fail");
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

    }



    // 닉네임 중복확인
    private void checkNick(){
        final String user_nick = et_nick.getText().toString();

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

    // 닉네임 response
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
            Log.e(TAG, jsonObject.optString("status"));
            // true 찍힘.
            Log.e(TAG, response);
            //saveInfo(response);
            Log.e(TAG, jsonObject.optString("status"));

            et_nick.setBackgroundResource(R.drawable.white_edittext);  //테투리 흰색으로 변경
            nickconfirm.setText("사용가능한 닉네임입니다.");
            nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));

        }
        else
        {
            nickconfirm.setText("이미 사용중인 닉네임 입니다.\n다른 닉네임을 입력해주세요.");    // 경고 메세지
            et_nick.setBackgroundResource(R.drawable.red_edittext);  // 적색 테두리 적용
            nickconfirm.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    // 사진 선택 버튼 클릭시 다이얼로그
    private void makeDialog(){
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);

                    builder.setMessage("갤러리에 대한 권한 사용을 거부하였습니다.\n기능 사용을 원하실 경우 휴대폰 설정 > 애플리케이션 관리자에서 해당 앱의 권한을 허용해주세요.");

                    android.app.AlertDialog alertDialog = builder.create();

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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);

                    builder.setMessage("카메라에 대한 권한 사용을 거부하였습니다.\n기능 사용을 원하실 경우 휴대폰 설정 > 애플리케이션 관리자에서 해당 앱의 권한을 허용해주세요.");

                    android.app.AlertDialog alertDialog = builder.create();

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
                        Glide.with(this).load(imagePath).into(user_img);
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
                    Glide.with(this).load(imagePath).into(user_img);
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