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
    // ????????? ?????? ????????? ?????? ??? = 1 / ????????? ???????????? 2??? ??????.
    int check = 1;

    String updateProduce;
    String updateNick;

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

        // ????????? ???

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

                // ?????? ??????????????? ?????????, ??????????????? ????????? ???
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

                            // ??????
                            Log.e(TAG, "?????? = " + t.getMessage());
                        }
                    });

                }
                // ?????? ????????? ???
                else
                {
                    if(img_check == 1 )
                    {
                        //?????? ?????????
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

                                        Log.e(TAG, "?????? ????????? ?????? ??? modify fail");
                                        Log.e(TAG, "onResponse: " + message );
                                        Log.e(TAG, "onResponse: photo : " + photo );
                                    }
                                }
                                else{

                                    Log.e(TAG, "?????? ????????? ?????? ??? modify onResponse fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<UserData> call, Throwable t) {
                                Log.e(TAG, "?????? ????????? ?????? ??? modify ?????? = " + t.getMessage());
                            }
                        });



                    }
                    else
                    {
                        // ?????? ????????? X ?????? ???????????? ????????? ????????? ?????? ???
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

        // ?????? ????????? ????????????
        btn_basicphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_img.setImageResource(R.drawable.profile2);

                img_check = 1;
                check = 2;

            }
        });

        // ?????? ????????????.
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDialog();
                img_check = 2;
                check = 2;

            }
        });


        // ?????????
        et_nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pattern = "^[0-9|a-z|A-Z|???-???|???-???|???-???]*$";
                if(!Pattern.matches(pattern,et_nick.getText())){
                    nickconfirm.setText("????????? ?????? ????????? ?????? ????????????.");    // ?????? ?????????
                    et_nick.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btn_nick.setEnabled(false);
                    // ???????????? ????????? ?????? ??????????????? ???????????? ???????????? ????????????.
                    // ?????? ??? ?????????

                }
                else if(et_nick.getText().toString().equals(""))
                {
                    nickconfirm.setText("???????????????.");
                    et_nick.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
                    nickconfirm.setTextColor(Color.parseColor("#FF0000"));
                    btn_nick.setEnabled(false);
                }
                else{
                    et_nick.setBackgroundResource(R.drawable.white_edittext);  //????????? ???????????? ??????
                    nickconfirm.setText("?????? ????????? ???????????????.");
                    nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));
                    btn_nick.setEnabled(true);
                }


            }
        });

        // ????????? ?????? ??????
        btn_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNick();
            }
        });

        // ????????????
        et_produce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = et_produce.getText().toString();
                textView.setText(input.length()+" / 60 ?????? ???");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // ????????? ?????? ?????????
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
                Log.e("onresponse", "?????? : " + t.getMessage());
            }
        });

    }



    // ????????? ????????????
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
            {// onresponse ?????? ????????? callback
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
                Log.e(TAG, "?????? = " + t.getMessage());
            }
        });

    }

    // ????????? response
    private void parseRegData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        Log.e("jsonobject", jsonObject.toString());

        //response??? ?????????
        // jsonObject??? getstring() ??? optstring()??? ??????
        // getstring??? ?????? ?????? ???????????? ?????? ?????? ?????? jsonException??? ??????????????? ?????????
        // optstring??? "" ??? ?????? ??? ???????????? ????????????. string ????????? ??? ???????????? ????????????.
        // key??? ???????????? ?????? ????????? ???????????? ???.
        if (jsonObject.optString("status").equals("true"))
        {
            Log.e(TAG, jsonObject.optString("status"));
            // true ??????.
            Log.e(TAG, response);
            //saveInfo(response);
            Log.e(TAG, jsonObject.optString("status"));

            et_nick.setBackgroundResource(R.drawable.white_edittext);  //????????? ???????????? ??????
            nickconfirm.setText("??????????????? ??????????????????.");
            nickconfirm.setTextColor(Color.parseColor("#0B2AEA"));

        }
        else
        {
            nickconfirm.setText("?????? ???????????? ????????? ?????????.\n?????? ???????????? ??????????????????.");    // ?????? ?????????
            et_nick.setBackgroundResource(R.drawable.red_edittext);  // ?????? ????????? ??????
            nickconfirm.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    // ?????? ?????? ?????? ????????? ???????????????
    private void makeDialog(){
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);
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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);

                    builder.setMessage("???????????? ?????? ?????? ????????? ?????????????????????.\n?????? ????????? ????????? ?????? ????????? ?????? > ?????????????????? ??????????????? ?????? ?????? ????????? ??????????????????.");

                    android.app.AlertDialog alertDialog = builder.create();

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
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);

                    builder.setMessage("???????????? ?????? ?????? ????????? ?????????????????????.\n?????? ????????? ????????? ?????? ????????? ?????? > ?????????????????? ??????????????? ?????? ?????? ????????? ??????????????????.");

                    android.app.AlertDialog alertDialog = builder.create();

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
                        Glide.with(this).load(imagePath).into(user_img);
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
                    Glide.with(this).load(imagePath).into(user_img);
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