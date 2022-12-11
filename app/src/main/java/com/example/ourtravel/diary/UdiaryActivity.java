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
import android.widget.Toast;

import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.MultiImageAdapter;
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

public class UdiaryActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();


    private EditText et_content; // 일기 내용 작성 부분
    private Button btn_img, btn_modify; // 사진 추가 버튼, 수정 완료 버튼
    private ImageButton btn_back; // 뒤로가기 버튼

    ArrayList<String> uriList = new ArrayList<>();
    // 이미지의 uri를 담을 ArrayList 객체

    // 수정되지 않은 사진들 담은 arraylist
    ArrayList<String> photo = new ArrayList<>();
    // 수정된 사진들을 담은 arraylist
    ArrayList<String> uri = new ArrayList<>();

    RecyclerView recyclerView;
    // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;
    // 리사이클러뷰에 적용시킬 어댑터

    int id; //게시글 id

    int cnt; //사진 추가할 때 cnt
    int j; // 사진 urilist의 size 담는 변수

    // 사진 다 삭제했는지 그대로 사진 남겼는지 알려주는 변수
    int check = 1;

    // 레트로핏 겍체
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 현재 로그인한 유저의 정보를 담고 있는 shared
    private PreferenceHelper preferenceHelper;
    // 현재 로그인한 유저의 이메일
    String now_user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udiary);

        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail();

        // 레트로핏 객체 지정해주기
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 이미지 리사이클러뷰
        recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        // 게시글 id 인텐트로 받아오기.
        id = (int)intent.getExtras().get("id");
        // 게시글 id 잘 받아와짐.
        Log.e(TAG, "onCreate: id : " + id );

        // 수정 페이지에서 유저가 업로드했던 데이터 세팅해줘야 함.
        retrofitInterface.setdiary(id).enqueue(new Callback<DiaryData>() {
            @Override
            public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                // 세팅 해줘야 하는 부분은 내용이랑 사진 밖에 없다.
                if(response.isSuccessful() && response.body() != null)
                {
                    DiaryData result = response.body();
                    Log.e(TAG, "onResponse: result : " + result );
                    String content = result.getContent();
                    et_content.setText(content);

                    for(int i=0; i < result.getPhoto_list().size(); i++)
                    {
                        uriList.add(result.getPhoto_list().get(i));
                    }

                    Log.e(TAG, "onResponse: uriList : "+ uriList );
                    Log.e(TAG, "onResponse: uriList length :" + uriList.size() );
                    // 어댑터 안에 list 넣기
                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(UdiaryActivity.this,LinearLayoutManager.HORIZONTAL, true));





                }
                else
                {
                    Log.e(TAG, "onResponse: response fail " );
                }
            }

            @Override
            public void onFailure(Call<DiaryData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

        Log.e(TAG, "onItemClick: uriList : " + uriList );

        //edittext
        et_content = findViewById(R.id.et_content); // 내용 작성 부분

        //button
        btn_img = findViewById(R.id.btn_img); // 사진 추가 버튼
        btn_modify = findViewById(R.id.btn_modify); // 수정 완료 버튼

        //imagebutton
        btn_back = findViewById(R.id.btn_back);
        // 뒤로가기 버튼 클릭 리스너
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 뒤로가기 하면 다이어리 리스트 화면으로 이동하게 된다.
                Intent intent = new Intent(UdiaryActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        // 이미지 업로드 버튼
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지 갯수 비교 

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

        // 일기 수정하기 버튼
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수정한 일기 내용
                String content = et_content.getText().toString();
                // 수정한 시간
                long now = System.currentTimeMillis();

                // 사진을 모두 삭제하고 그냥 글만 올렸을때

                if(uriList.size() == 0)
                {
                    check = 0;
                    // 서버에서 사진 모두 삭제해야 하는 경우
                    Log.e(TAG, "onClick: 아무런 사진도 없음" );
                    // 일단 사진 없이 업데이트한 상황임
                    // 텍스트, 수정된 시간, 해당 일기 id 보내면 됨.
                    Upwithoutpic(content, now, check); // 구현
                }
                else
                {

                    for(int i =0; i < uriList.size(); i++)
                    {
                        String str = uriList.get(i);
                        Log.e(TAG, "onClick: str :" + str);
                        // 텍스트만 따로 db로 보내고 사진 db에 있는 사진들 모두 삭제해 준다...


                        if(str.contains(".jpg"))
                        {
                            Log.e(TAG, "onClick: 수정하지 않은 photo임" );
                            // photo를 포함하고 있다면 멈추기.
                            // 서버 사진
                            photo.add(str);

                        }
                        else
                        {
                            Log.e(TAG, "onClick: 수정한 photo " );
                            // 새로 추가한 사진
                            uri.add(str);
                        }


                    }

                    Log.e(TAG, "onClick: photo에 들어간 요소들" + photo );
                    Log.e(TAG, "onClick: uri에 들어간 요소들" + uri );

                    modify(photo, uri, content, now);




                }

                // diarymodify 메서드는 모든 사진들을 지우고 새로 추가했을 경우에 사용하자
                //diarymodify(content, now);
            }
        });



    }

    // 사진이 있는 상황에서 수정하기.
    private void modify(ArrayList<String> photo, ArrayList<String> uri, String content, long now){

        int photo_size= photo.size();
        int uri_size = uri.size();
        Log.e(TAG, "modify: photo_size : "+ photo_size);
        Log.e(TAG, "modify: uri_size : "+ uri_size);

        if(photo_size == 0)
        {
            // 업로드된 사진 모두 경로값을 가질때
            Log.e(TAG, "modify: 모두 경로값을 가지는 사진인 경우" );
            diarymodify(content, now);
        }
        else if(uri_size == 0)
        {
            // 사진을 하나도 추가하지 않은 경우 텍스트만 건들인거고 사진은 그대로라는 뜻.
            Log.e(TAG, "modify: 사진 하나도 추가하지 않음" );
            Upwithoutpic(content, now, check);

        }
        else
        {
            // 경로값을 가지고 있는 이미지, 건드리지 않은 이미지가 같이 있는 경우.
            Log.e(TAG, "modify: 수정되지 않은 사진을 포함하고 있는 경우" );
            updateMixed(photo, uri, content, now, now_user_email);
        }



    }

    // 경로를 가지고 있는 사진, 수정하지 않은 사진이 섞여있는 경우
    private void updateMixed(ArrayList<String> photo, ArrayList<String> uri, String content, long now, String now_user_email)
    {
        // 우선 uri 에 담겨있는 경로값들을 파일형식으로 바꿔서 서버로 보내줘야 한다.
        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        // 파일 경로들을 가지고 있는 uriList
        for(int i =0; i< uri.size(); ++i)
        {


            File diaryfile = new File(makePath(getApplicationContext(), uri.get(i)));


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

        // 서버 통신
        retrofitInterface.updateMixed(files, photo, content, now, id, now_user_email).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    String jsonResponse = response.body();
                    Log.e(TAG, "onResponse: updateMixed jsonResponse : " + jsonResponse );

                    Toast.makeText(UdiaryActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                    // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.

                    // 다이어리 작성 후 다이어리 메인 페이지로 이동.
                    Intent intent = new Intent(UdiaryActivity.this, DiaryActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.e(TAG, "onResponse: updateMixed fail" );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onresponse", "다이어리 수정 updateMixed 서버 에러 : " + t.getMessage());
            }
        });

    }


    // 사진 없이 일기 수정하려고 할때 ( 사진 모두 삭제하고 수정 or 애초에 사진이 없었는데 텍스트만 수정 )
    private void Upwithoutpic(String content, long date, int check)
    {
        Log.e(TAG, "Upwithoutpic: check : "+ check );
        // check= 1 이면 아무것도 안건들인 상태 check=0 이면 모두 지운 상태

        retrofitInterface.Upwithoutpic(content, date, id, check).enqueue(new Callback<DiaryData>() {
            @Override
            public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                if(response.isSuccessful() &&  response.body() != null)
                {
                    DiaryData result = response.body();
                    Log.e(TAG, "onResponse 사진 없이 텍스트만 수정했을 경우 : result"+ result );

                    Toast.makeText(UdiaryActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                    // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.

                    // 다이어리 작성 후 다이어리 메인 페이지로 이동.
                    Intent intent = new Intent(UdiaryActivity.this, DiaryActivity.class);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    Log.e(TAG, "onResponse: 사진 없이 텍스트만 수정했을 경우 fail" );
                }
            }

            @Override
            public void onFailure(Call<DiaryData> call, Throwable t) {
                Log.e("onresponse", "다이어리 수정 서버 에러 : " + t.getMessage());
            }
        });
    }


    // 다이어리 수정하는 메서드
    private void diarymodify(String content, long date)
    {
        // 여러 file 들을 담아줄 arraylist
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        // 파일 경로들을 가지고 있는 uriList
        for(int i =0; i< uriList.size(); ++i)
        {

            // for each
            File diaryfile = new File(makePath(getApplicationContext(), uriList.get(i)));

            //Log.e(TAG, "diaryupload: path : " + uriList.get(i).getPath() );
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
        Log.e(TAG, "diaryupload: content 의 데이터 타입 :" + content.getClass().getName() );
        Log.e(TAG, "diaryupload: files 의 데이터 타입 :" + files.getClass().getName() );

        // 이미지 file들, 작성한 일기내용, 일기가 작성된 시간 db에 수정해야한다.
        // 유저 이메일은 그대로니까 수정할 필요 X 일기의 고유 id를 서버로 보내야 한다.
        // 서버 요청
        retrofitInterface.diaryupdate(files, content, now_user_email, date, id).enqueue(new Callback<String>() {
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
                    Log.e(TAG, "다이어리 수정 onResponse: fail" );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onresponse", "다이어리 수정 서버 에러 : " + t.getMessage());
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
            // true 찍힘.
            Log.e("jsonobject2", response);

            Log.e("jsonobject3", jsonObject.optString("status"));
            Toast.makeText(UdiaryActivity.this, "수정 성공", Toast.LENGTH_SHORT).show();
            // 업로드 성공해서 서버에 이미지 올라가는것 까지 확인.

            // 다이어리 작성 후 다이어리 메인 페이지로 이동.
            Intent intent = new Intent(UdiaryActivity.this, DiaryActivity.class);
            startActivity(intent);
            finish();



        }
        else
        {

            Toast.makeText(UdiaryActivity.this, "수정 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 절대주소 만드는 메서드
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
                        Toast.makeText(getApplicationContext(), "11사진은 3장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else{   // 선택한 이미지가 1장 이상 3장 이하인 경우
                        Log.e(TAG, "multiple choice");

                        // 선택한 이미지의 개수 만큼 for문 돌리고
                        // uriList에 선택한 이미지 담기.
                        for (int i = 0; i < clipData.getItemCount(); i++){
                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            // 대부분의 경우 하나의 항목만 저장할 수있는데 getitemAt 호출로 항목을 구할수 있다.
                            // clipdata.item은 클립보드에 저장되는 데이터 하나이고 응용 프로그램 끼리 교환할 실제 데이터 이다.
                            // clipdata에 저장되어있는 데이터들에서 i 번째의 아이템을 가져오고 그 아이템의 uri를 가져와라 그리고 그 url 을 imageurl에 넣어라.

                            // 갤러리에서 동작하는것, 액티비티내에서 동작하는 것이 아니라. provider에서 컨트롤하면 되는 문제인것 같다.
                            // 내가 하고 싶은것은 갤러리내에서의 동작.
                            // 미디어 관련 프로파이더 공부하기.


                            try {

                                  int g = uriList.size();
                                  // g는 현재 uriList에 담겨있는 사진의 갯수를 의미함.
                                  // 사진을 추가할때 0, 1 이런 순서대로 i가 증가함 -> 사진 2개 추가시 i는 0 , 1 이렇게 증가함.
                                  // 이때 사진 한장 추가했을 때 2+0 = 2, 사진 두 장 추가했을 때 2 +1 = 3 이니까 조건문 걸어서
                                  // 3개 이상 uriList에 담겨있을 때 toast 메세지 띄우게 구현.
                                Log.e(TAG, "onActivityResult: g :" + g );
                                Log.e(TAG, "onActivityResult: i :" + i );

                                // g 가 0 일때
                                // g size가 i 와 함께 같이 올라간다..

                                  if(g + i < 5)
                                  {
//                                      // 이미지 절대경로 string으로 변형하기.
                                      String imagestr = imageUri.toString();
                                      uriList.add(imagestr);
//                                    //uri를 list에 담는다.
                                  }
                                  else
                                  {
                                      Toast.makeText(getApplicationContext(), "22사진은 3장까지 업로드가능합니다.", Toast.LENGTH_LONG).show();
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


                        // 만약 리사이클러뷰에 셋팅되는 이미지가 3개 이상이면
                        if(adapter.getItemCount() > 3)
                        {

                            Log.e(TAG, "onActivityResult: adapter.getItemCount()" + adapter.getItemCount() );
                            // 3개 이상이면 urllist에 추가된 가장 최근의 갯수 만큼 요소를 지워야한다.

                            Toast.makeText(getApplicationContext(), "33사진은 3장까지 업로드가능합니다.", Toast.LENGTH_LONG).show();

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