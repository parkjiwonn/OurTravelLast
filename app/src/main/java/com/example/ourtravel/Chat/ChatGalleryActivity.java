package com.example.ourtravel.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ourtravel.AboutChatGallery.LookUpViewHolder;
import com.example.ourtravel.AboutChatGallery.Photo;
import com.example.ourtravel.AboutChatGallery.PhotoAdapter;
import com.example.ourtravel.AboutChatGallery.PhotoDetailsLookUp;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.RecyclerDiaryData;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.diary.WdiaryActivity;
import com.example.ourtravel.home.RcompanyActivity;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatGalleryActivity extends AppCompatActivity {

    public static final String TAG = ChatGalleryActivity.class.getSimpleName();
    private static final int REQ_CODE_PERMISSION = 0;

    //============리사이클러뷰 관련 변수=========================
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    ArrayList<String> photoArrayList = new ArrayList<>();
    //=========================================================
    SelectionTracker<Long> selectionTracker; // selection tracker
    ArrayList<String> list = new ArrayList<>(); // 사진 경로담은 list
    String photopath; // 개별 사진 경로

    // 현재 로그인한 회원 정보 담고있는 shared 선언
    private PreferenceHelper preferenceHelper;
    String now_user_email; // 현재 로그인한 유저의 이메일

    // 레트로핏
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;



    int roomNum; // 채팅방 숫자
    String gallery; // 갤러리 구분
    String unitime; // 사진 보낸 시간

    Button btn_next; // 사진 전송 버튼
    ImageButton back_btn; // 뒤로가기 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gallery);

        // 리사이클러뷰 아이템 클릭 리스너에서 보내온 데이터 인텐트 받기
        Intent intent = getIntent();
        roomNum = (int)intent.getExtras().get("roomNum"); // 채팅방 번호
        Log.e(TAG, "onCreate: roomNum =" +roomNum );

        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저의 이멜 = 아이디

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();



//        // 툴바 생성
//        Toolbar toolbar = findViewById(R.id.next_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
//        getSupportActionBar().setTitle("다른 액티비티"); // 툴바 제목 설정


        Log.e(TAG, "onCreate: 들어옴" );
        // 갤러리 접근 권한
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_PERMISSION );



    }





    // 갤러리 접근권한 받아오는 부분, 접근 권한 허용되면 setupUI 실행.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: 들어옴 1" );
        // 갤러리 권한
        if(requestCode == REQ_CODE_PERMISSION){
            for(int grantResult :grantResults){
                if(grantResult == PackageManager.PERMISSION_DENIED){
                    finish();
                    return;
                }
            }
            setupUI();
        }
    }

    // 갤러리 UI 세팅하는 메서드
    // 리사이클러뷰 어댑터 리스트 연결해주는 부분
    private void setupUI(){
        Log.e(TAG, "setupUI: 들어옴 2" );
        setContentView(R.layout.activity_chat_gallery);
        // ==================리사이클러뷰 연결 부분=============================
        recyclerView = findViewById(R.id.recycler_view); // 액티비티에 있는 리사이클러뷰 위젯 참조하기
        photoAdapter = new PhotoAdapter(this, photoArrayList); // 어댑터 객체 생성
        recyclerView.setAdapter(photoAdapter); // 어댑터 연결
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 그리드 형식으로 리사이클러뷰 만들기
        // =====================================================================
        setupSelectionTracker(); // selectiontracker를 만들어서
        photoAdapter.setSelectionTracker(selectionTracker); // 어댑터에 selectiontracker를 넘겨준다.

        Log.e(TAG, "setupUI: 클릭리스너 전" );

        // 사진 전송 버튼
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: click" );
                gallery = "gallery";


                ChatPhotoUpload();


            }

        });

        // 뒤로가기 버튼
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery = "back";
                Intent intent = new Intent(ChatGalleryActivity.this, Chat_Activity.class);
                intent.putExtra("gallery", gallery); // 갤러리에서 채팅액티비티로 갔다는걸 알려주기 위해
                intent.putExtra("roomNum", roomNum); // 채팅방 번호
                startActivity(intent);
                finish();
            }
        });


        // 어댑터 클릭리스너 인터페이스 받는 부분
        photoAdapter.changeFt(new PhotoAdapter.getFileNameListner() {
            @Override
            public void change(ArrayList<String> result, String path) {
                // 선택한 사진들 경로 담은 arraylist
                // 선택한 개별 사진 경로
                list = result;
                System.out.println(list);
                photopath = path;
                System.out.println(path);
                Log.e(TAG, "change: result size ="  );

            }
        });

        Log.e(TAG, "setupUI: 클릭리스너 후" );


    }

    // 채팅에서 보낼 사진 서버로 전송하는 메서드
    private void ChatPhotoUpload() {
        Log.e(TAG, "ChatPhotoUpload: photolist =" + photoArrayList ); // 유저가 선택한 사진 리스트
        Log.e(TAG, "ChatPhotoUpload: user_email = "+now_user_email ); // 유저의 이메일

        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        // 파일 경로들을 가지고 있는 photoarraylist
        for(int i =0; i<photoArrayList.size(); i++){
            // 이미 절대경로로 갖고 있음.
            Log.e(TAG, "ChatPhotoUpload: photoarraylist.get(i) =" + photoArrayList.get(i) );
            File diaryfile = new File(photoArrayList.get(i));
            Log.e(TAG, "ChatPhotoUpload: diaryfile = "+ diaryfile );

            Log.e(TAG, "diaryupload: urllist =" + photoArrayList );

            if (!diaryfile.exists()) {       // 원하는 경로에 폴더가 있는지 확인

                diaryfile.mkdirs();    // 하위폴더를 포함한 폴더를 전부 생성

            }

            // uri 타입의 파일 경로를 가지는 requestBody 객체 생성한다.
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), diaryfile);

            // 사진 파일 이름
            String fileName = "photo" + i + "_";
            // RequestBody로 Multipartbody.Part 객체 생성.
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo[]" , fileName, fileBody);
            // fileName

            // 파일 추가해주기
            files.add(filePart);
        }

        Log.e(TAG, "ChatPhotoUpload: files =" + files );

        retrofitInterface.ChatPhotoUpload(files, now_user_email, roomNum).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    String jsonResponse = response.body();
                    try
                    {
                        Log.e("in try", jsonResponse);

                        // 응답받은 부분을 json object로 변환하기 위함.
                        PhotoResponse(jsonResponse);
                        Log.e("next parseRegData", jsonResponse);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Log.e(TAG, "onResponse: fail" );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });


    }

    // 응답받은 response를 json object로 변한하기 위한 메서드
    private void PhotoResponse(String response) throws JSONException{

        JSONObject jsonObject = new JSONObject(response);
        // 해당 키값에 해당하는 값이 있는 경우에 value 값을 리턴하지만
        // 값이 없는 경우에 ""와 같은 빈 문자열을 반환한다.

        if (jsonObject.optString("status").equals("true"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            Log.e(TAG, "PhotoResponse: unitime =" + jsonObject.optString("unitime") );
            unitime = jsonObject.optString("unitime");
            Log.e(TAG, "PhotoResponse: unitime = " +unitime );
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));
            Toast.makeText(ChatGalleryActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
            // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.

            Intent intent = new Intent(ChatGalleryActivity.this, Chat_Activity.class);
            intent.putExtra("gallery", gallery); // 갤러리에서 채팅액티비티로 갔다는걸 알려주기 위해
            intent.putExtra("roomNum", roomNum); // 채팅방 번호
            intent.putExtra("unitime", unitime); // 사진 보낸 시간

            setResult(RESULT_OK, intent);
//            startActivity(intent);
            finish();


        }
        else
        {

            Toast.makeText(ChatGalleryActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }



    // selectiontracker 빌드하는 부분
    private void setupSelectionTracker(){
        Log.e(TAG, "setupSelectionTracker: 들어옴 4" );
        selectionTracker = new SelectionTracker.Builder<>( // 빌더를 통해 selectionTracker를 만든다.
                "selection_id",
                recyclerView,
                new StableIdKeyProvider(recyclerView),
                new PhotoDetailsLookUp(recyclerView),
                StorageStrategy.createLongStorage())
                // builder를 만드는데 필요한 파라미터
                // selectionid : 선택내용에 대한 id를 지정
                // recyclerview : 선택내용을 추적할 recyclerview를 지정한다.
                // key provider : 캐시를 위한 선택되는 아이템의 key 제공자
                // itemDetailsLookup : Recyclerview 아이템에 대한 정보
                // storage : saved state에서 키를 저장하기 위한 전략
                .withSelectionPredicate(SelectionPredicates.<Long>createSelectAnything()) // 어떠한 선택을 할지 결정할 수 있게 한다.
                .build();

        // 갤러리의 사진을 선택했을 때 onitemstatechanged, selectionchanged가 선택됨.
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
                Log.e(TAG, "onItemStateChanged: 아이템의 상태가 변화되었을때 불려짐 " );
                Log.e(TAG, "onItemStateChanged: key = " + key ); // key는 아이템의 position 값이 불러와짐
                // selected 찍어보기
                Log.e(TAG, "onItemStateChanged: selected = " + selected );

                if(selected == false){
                    // 선택이 해제되었다는 의미
                    Log.e(TAG, "onItemStateChanged: photoarraylist= "+photoArrayList );
                    photoArrayList.clear();
                    photoAdapter.notifyDataSetChanged();

                }
                else
                {
                    // 3장 이상인 경우
                    if(photoArrayList.size() > 2)
                    {
                      // 3장 이상은 전송 불가라는 다이얼로그 띄우기.
                      showLimitNumOfPic();
                    }
                    else{

                    }

                }



            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
                Log.e(TAG, "onSelectionRefresh: 기존 데이터 집합이 변경되면 호출" );
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                Log.e(TAG, "onSelectionChanged: refresh,restore에 대한 호출 제외한 모든 변경이 완료된후 호출된다.");
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
                Log.e(TAG, "onSelectionRestored: 선택 항목이 복원된 후 즉시 호출" );
            }
        });

    }

    // 사진 갯수 제한 알려주는 다이얼로그
    private void showLimitNumOfPic() {

        AlertDialog.Builder dig_limit = new AlertDialog.Builder(this);
        dig_limit.setMessage("이미지 전송은 3장까지 가능합니다.");
        dig_limit.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // 리스트에서 추가된 사진 제거하기
                photoArrayList.remove(photopath);
                System.out.println(photoArrayList);


            }
        });
        dig_limit.show();
    }

















}