package com.example.ourtravel.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ourtravel.Adapter.MultiImageAdapter;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.DiaryData;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WdiaryActivity extends AppCompatActivity {

    private static final String TAG = "WdiaryActivity";
    // 뒤로가기 버튼
    private ImageButton back_btn;
    // 이미지 불러오기 버튼 , 등록 버튼
    private Button btn_img,btn_next;
    // 일기 내용
    private EditText et_content;

    // 일기 공개범위 정하는 라디오 버튼 그룹
    RadioGroup radioGroup;

    ArrayList<String> uriList = new ArrayList<>();
    // 이미지의 uri를 담을 ArrayList 객체

    RecyclerView recyclerView;
    // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;
    // 리사이클러뷰에 적용시킬 어댑터

    // 현재 시간 구하기
    long now = System.currentTimeMillis();
    String date ;
    int j;
    int cnt;

    // 현재 로그인한 유저의 정보를 담고 있는 shared
    private PreferenceHelper preferenceHelper;

    // 레트로핏 겍체
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 현재 로그인한 유저의 이메일
    String now_user_email;

    // 사진 선택 여부 확인 변수
    int check = 0  ;

    // 사진 공개 범위
    int range;
    // range = 0 이면 전체공개
    // range = 1 이면 이웃공개
    // range = 2 이면 서로이웃공개


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wdiary);
        

        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail();

        // 레트로핏 객체화ㅁ
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 이미지 리사이클러뷰
        recyclerView = findViewById(R.id.recyclerView);
        // 일기 내용 edittext
        et_content = findViewById(R.id.et_content);

        // 일기 공개범위 정하는 라디오버튼 그룹
        radioGroup = findViewById(R.id.radio_group);


        // 뒤로가기 버튼 클릭 리스너
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WdiaryActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 이미지 불러오기 버튼 클릭리스너 (다중 이미지 갤러리에서 불러오기)
        btn_img = findViewById(R.id.btn_img);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이미지 갯수 비교 
                check = 1; // 이미지 선택했으니 1로 초기화
                final int MAXNUMPHOTO = 3;
                Intent intent = new Intent(Intent.ACTION_PICK);
                // ACTION_PICK, ACTION_GET_CONTENT 앨범을 호출할 때 인텐트에 넣는 액션 2가지
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                // 해당 인텐트에 담을 데이터의 유형을 정해주는것. 단말기 안의 저장소에 접근
                // settype 데이터의 유형을 지정해주는 것. MIME유형 지정
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // 해당값을 true로 줘야 멀티 초이스르 지원함.
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI 는 외부사진 그래서 READ_EXTERNAL_STORAGE 권한 필요하다.
                startActivityForResult(intent, 2222);
                // startActivityforresult를 통해 앨범으로 이동시킨다.
            }
        });

        // range = 0 이면 전체공개
        // range = 1 이면 이웃공개
        // range = 2 이면 서로이웃공개

        // 일기의 공개 범위 정하기
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.radio_button_all:
                        range = 0;
                        break;
                    case R.id.radio_button_neighbor:
                        range = 1;
                        break;
                    case R.id.radio_button_bf:
                        range = 2;
                        break;

                }
            }
        });




        // 일기 등록 버튼
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = et_content.getText().toString();
                long now = System.currentTimeMillis();

                // 사진 업로드를 한 경우와 하지 않은 경우를 나눠줘야 한다.
                if(check != 1)
                {
                    Log.e(TAG, "onClick: 사진 upload x" );
                    Log.e(TAG, "onClick: range = " + range);
                    withoutpic(content, now, range);
                }
                else
                {
                    Log.e(TAG, "onClick: 사진 upload o" );
//                    // 내용, 작성 시간, 사진 업로드 해야 한다
                    Log.e(TAG, "onClick: range = " + range);
                    diaryupload(content, now, range);
                }





            }
        });



    }

    // 사진 업로드 없이 일기를 등록 할 때
    private void withoutpic(String content, long date, int range)
    {
        retrofitInterface.withoutpic(content,date,now_user_email, range).enqueue(new Callback<DiaryData>() {
            @Override
            public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    DiaryData result = response.body();
                    String status = result.getStatus();
                    if(status.equals("true"))
                    {
                        Toast.makeText(WdiaryActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.


                        // 다이어리 작성 후 다이어리 메인 페이지로 이동.
                        Intent intent = new Intent(WdiaryActivity.this, DiaryActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Log.e(TAG, "onResponse: status is not ture" );
                    }
                }
                else
                {
                    Log.e(TAG, "onResponse: 사진없이 업로드 fail" );
                }
            }

            @Override
            public void onFailure(Call<DiaryData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });
    }

    // 다이어리 업로드하는 메서드
    private void diaryupload(String content, long date, int range){

        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        // 파일 경로들을 가지고 있는 uriList
        for(int i =0; i< uriList.size(); ++i)
        {
            File diaryfile = new File(makePath(getApplicationContext(), uriList.get(i)));
            //
            //Log.e(TAG, "diaryupload: path : " + uriList.get(i).getPath() );
            Log.e(TAG, "diaryupload: urllist =" + uriList );
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

        Log.e(TAG, "diaryupload: files :" + files );
        Log.e(TAG, "diaryupload: content : " + content );
        //Log.e(TAG, "diaryupload: date : " + date );
        //Log.e(TAG, "diaryupload: date 의 데이터 타입 :" + date.getClass().getName() );
        Log.e(TAG, "diaryupload: content 의 데이터 타입 :" + content.getClass().getName() );
        Log.e(TAG, "diaryupload: files 의 데이터 타입 :" + files.getClass().getName() );

        // 이미지 file들, 작성한 일기내용, 일기가 작성된 시간, 작성한 유저의 이메일을 db에 저장해야한다.ㅗ
        // 서버 요청
        retrofitInterface.diaryupload(files,content,date,now_user_email, range).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()&& response.body() != null)
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



    };

    // 응답받은 response를 json object로 변한하기 위한 메서드
    private void PhotoResponse(String response) throws JSONException{

        JSONObject jsonObject = new JSONObject(response);
        // 해당 키값에 해당하는 값이 있는 경우에 value 값을 리턴하지만
        // 값이 없는 경우에 ""와 같은 빈 문자열을 반환한다.

        if (jsonObject.optString("status").equals("true"))
        {
            Log.e("jsonobject2", jsonObject.optString("status"));
            // true 찍힘.
            Log.e("jsonobject2", response);
            
            Log.e("jsonobject3", jsonObject.optString("status"));
            Toast.makeText(WdiaryActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
            // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.

            // 다이어리 작성 후 다이어리 메인 페이지로 이동.
            Intent intent = new Intent(WdiaryActivity.this, DiaryActivity.class);
            startActivity(intent);
            finish();

           

        }
        else
        {

            Toast.makeText(WdiaryActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 절대주소
    @Nullable
    public static String makePath(@NonNull Context context, @NonNull String uri)
    { // 선택한 사진의 절대 경로 URL 만드는 코드

         final ContentResolver contentResolver = context.getContentResolver();
         // content provider에 접근하는 것은 content resolver를 통해서만 가능하다.
        // 그리고 contentresolver는 context의 getcontextresolver메서드로 구할 수 있다.

         System.out.println("리얼 패스 uri: "+uri);

         if (contentResolver == null)
         return null;

         // 파일 경로를 만듬 filePath
         String filePath = context.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
         // getApplicationInfo 는 컨텍스트 패키지에 대한 전체 애플리케이션 정보 반환
         // context.getApplicationInfo().dataDir 이유
         // 안드로이드 홈 디렉토리 알아내기 위함이다.
         //File.separator 는 파일 구분자이다., 파일의 경로를 분리해주는 메서드이다.

         File file = new File(filePath);

         try {
             // 매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러 들인다.
             Uri uri1 = Uri.parse(uri);
         InputStream inputStream = contentResolver.openInputStream(uri1);
         // 이미지 파일들을 inputstream으로 리턴하는 openinputstream 이다.
             // contentprovider를 통해 db에 있는 파일 path의 uri로 해당 파일을 inputstream으로 가져오는 방법이다.

         if (inputStream == null)
             return null;

         // 이미지 데이터를 다시 내보내면서 file 객체에 만들었던 경로를 이용한다.
         OutputStream outputStream = new FileOutputStream(file);

         byte[] buf = new byte[1024];

         int len;

         while ((len = inputStream.read(buf)) > 0)
         outputStream.write(buf, 0, len);
         outputStream.close();
         inputStream.close(); }
         catch (IOException ignore)
         {
             return null;
         }

         // 파일의 절대경로 얻는 방법.
         return file.getAbsolutePath();
    }



    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 요청 코드가 2222일 때
        if(requestCode == 2222){
            if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            }
            else{   // 이미지를 하나라도 선택한 경우
                if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                    // getClipData() 여러개의 파일을 선택한 후 getClipData()에서 반환한 ClipData 객체에서 선택된 각 파일에 엑세스할 수 있다.
                    // 데이터를 클립보드에 추가하려면 데이터 설명과 데이터 자체를 모두 포함하는 Clipdata 객체를 만들어야 한다.
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    // data.getData() 로그 확인
                    Uri imageUri = data.getData();
                    String imagestr = imageUri.toString();
                    Log.e(TAG, "onActivityResult: imageUri : " + imageUri );

                    uriList.add(imagestr);

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
                }
                else{      // 이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();
                    // clipdata는 클립보드에 저장되는 주체, 한개이상의 실체 데이터(clipdata.item)
                    // 클립보드란 프로그램 내부의 뷰끼리는 물론, 응용프로그램간에도 약속된 방법으로 데이터를 교환할 수 있다.
                    // 클립보드에 저장되는 데이터이고 속하는 메서드로 항목의 갯수를 조사하거나 항목을 읽고 추가할 수 있다.

                    Log.e("clipData", String.valueOf(clipData.getItemCount()));

                    if(clipData.getItemCount() > 3){   // 선택한 이미지가 3장 초과인 경우
                        Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                        Log.e(TAG, "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++){
                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            // 대부분의 경우 하나의 항목만 저장할 수있는데 getitemAt 호출로 항목을 구할수 있다.
                            // clipdata.item은 클립보드에 저장되는 데이터 하나이고 응용 프로그램 끼리 교환할 실제 데이터 이다.
                            // clipdata에 저장되어있는 데이터들에서 i 번째의 아이템을 가져오고 그 아이템의 uri를 가져와라 그리고 그 url 을 imageurl에 넣어라.

                            // 갤러리에서 동작하는것, 액티비티내에서 동작하는 것이 아니라. provider에서 컨트롤하면 되는 문제인것 같다.
                            // 내가 하고 싶은것은 갤러리내에서의 동작.
                            // 미디어 관련 프로파이더 공부하기.


                            try {

                                    cnt++;
                                    // 사진 한장한장씩 추가될때마다 cnt 변수가 하나씩 증가한다.
                                    // 증가한 후 3이 된다는 것 => 사진이 총 3개가 들어왔다는 것.

                                    if(cnt > 3)
                                    // cnt가 3이 넘는다면 list안에 사진을 넣지 않는다.
                                    {
                                        Toast.makeText(getApplicationContext(), "사진은 3장까지 업로드가능합니다.", Toast.LENGTH_LONG).show();
                                    }
                                    // cnt 가 3이 되지 않았다면 urlList에 사진 url을 넣는다.
                                    else{
                                        String imagestr = imageUri.toString();
                                        uriList.add(imagestr);
                                        //uri를 list에 담는다.
                                    }

                                    // 현재 list에 담겨져 있는 사진 갯수 보기 위함.
                                    j = uriList.size();
                                    Log.e(TAG, "onActivityResult: j-2 : " + j );
                                    Log.e(TAG, "onActivityResult: 전체 list : " + uriList );



                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }

                        // 어댑터에 list 넣기
                        adapter = new MultiImageAdapter(uriList, getApplicationContext());
                            // getApplicationContext() 액티비티 내에서 접근 가능한 인스턴스 여기는 액티비티니까 getapplicationcontext한것.

                        adapter.setOnItemClicklistener(new MultiImageAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int pos) {
                                Log.e(TAG, "onItemClick: 1" );
                                uriList.remove(pos);
                                Log.e(TAG, "onItemClick: uriList : " +uriList );
                                adapter.notifyItemRemoved(pos);
//                                // 선택한 position에 대항하는 아이템을 삭제하고 싶다.
                                Log.e(TAG, "onItemClick: 2" );
                                adapter.notifyDataSetChanged();
//                                // 그리고 나서 아이템 세팅을 새로고침 해준다. 전체 아이템을 업데이트 한다.
                                Log.e(TAG, "onItemClick: 3" );
                            }
                        });


                        // 만약 리사이클러뷰에 셋팅되는 이미지가 3개 이상이면
                        if(adapter.getItemCount() > 3)
                        {

                            Log.e(TAG, "onActivityResult: adapter.getItemCount()" + adapter.getItemCount() );
                            // 3개 이상이면 urllist에 추가된 가장 최근의 갯수 만큼 요소를 지워야한다.

                            Toast.makeText(getApplicationContext(), "사진은 3장까지 업로드가능합니다.", Toast.LENGTH_LONG).show();

                        }
                        else{

                            // item이 3개 이상이 아닐경우에 adpater는 리사이클러뷰에 장착된다.
                            recyclerView.setAdapter(adapter);
                            // 리사이클러뷰에 어댑터 세팅

                            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                        }


                    }
                }
            }
        }
    }
}