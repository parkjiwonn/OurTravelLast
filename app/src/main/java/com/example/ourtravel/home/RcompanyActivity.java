package com.example.ourtravel.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Adapter.ChatUserAdapter;
import com.example.ourtravel.Chat.Chat_Activity;
import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.ChatRoomData;
import com.example.ourtravel.retrofit.ChatUser;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RcompanyActivity extends AppCompatActivity {

    final String TAG = "RcompanyActivity";

    private TextView tx_title, tx_start,tx_finish, tx_people, tx_main, tx_sub, tx_content, tx_type, tx_nick, tx_attend;
    private TextView tx_condition; // 동행 모집 중, 동행 완료, 동행 종료 알려주기
    private ImageView banner, img;
    private ImageButton back_btn, btn_menu;
    private Button btn_chat;
    private ToggleButton bookmark;
    private LinearLayout bottomLin;
    private View bottomLine;

    CompanyData companyDataArrayList;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;
    String now_user_email; // 현재 로그인한 유저
    String user_email; // 글작성한 유저
   

    int id;
    Dialog dialog01;

    //=====================================동행에 참여요청을 보낸 사람 리스트 리사이클러뷰=========================================
    RecyclerView userRecyclerview = null;
    ChatUserAdapter chatUserAdapter = null;
    ArrayList<ChatUser> chatUsers ;
    //=========================================================================

    private LinearLayout View;
    private LinearLayout.LayoutParams params;
    private Button btn_company;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcompany);

        // 버튼을 놓을 layout
        View = (LinearLayout)findViewById(R.id.bottomLin);
        // 레이아웃 파라미터 생성
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 동행 모집 완료 버튼
        btn_company = new Button(RcompanyActivity.this);




        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail();

        // 다이얼로그 (수정, 삭제)
        dialog01 = new Dialog(RcompanyActivity.this);
        dialog01.setContentView(R.layout.dialog_default);

        // 뒤로가기 버튼
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RcompanyActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Text View
        tx_title = findViewById(R.id.tx_title); // 게시글 제목
        tx_start = findViewById(R.id.tx_start); // 동행 시작 날짜
        tx_finish = findViewById(R.id.tx_finish); // 동행 끝 날짜
        tx_people = findViewById(R.id.tx_people); // 동행 모집 인원
        tx_main = findViewById(R.id.tx_main); // 동행 메인 장소
        tx_sub = findViewById(R.id.tx_sub); // 동행 서브 장소
        tx_content = findViewById(R.id.tx_content); // 게시글 내용
        tx_type = findViewById(R.id.tx_type); // 동행 지역 타입
        tx_nick = findViewById(R.id.tx_nick); // 작성자 닉네임
        tx_attend = findViewById(R.id.tx_attend); // 참여인원
        tx_condition = findViewById(R.id.tx_condition); // 동행 모집 현황

        // image view
        banner = findViewById(R.id.banner);
        img= findViewById(R.id.img);

        // Button
        btn_menu = findViewById(R.id.show_dialog_btn); // 메뉴 버튼
        btn_chat = findViewById(R.id.btn_chat); // 동행 요청하기 버튼
        bookmark = findViewById(R.id.bookmark); // 북마크 버튼


        // 인텐트 선언부
        Intent intent = getIntent();

        // 게시글 id
        id = (int)intent.getExtras().get("id");
        Log.e(TAG, "onCreate: id : " + id );

        //=================================================채팅에 참여중인 유저 리스트 리사이클러뷰================================================
        userRecyclerview = findViewById(R.id.rv_userlist);
        chatUsers = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(chatUsers, getApplicationContext());
//        userRecyclerview.setAdapter(chatUserAdapter);
//        userRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //===================================================================================================================================




        // 서버 통신 (작성한 유저의 프로필 서버통신으로 데이터 받아옴 -> 유저의 프로필 정보가 변경될때마다 확인 가능해야 하니까)
        user_email = (String)intent.getExtras().get("user_email");
        Log.e(TAG, "onCreate: user_email :"+user_email );

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        // 이때 user_email은 이 게시글을 작성한 유저의 이메일
        // userinfo 테이블에서 유저의 정보를 가져온다. (닉네임, 사진)
        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if(response.isSuccessful())
                {
                    UserData result = response.body();
                    Log.e(TAG, "onResponse: result" + result );
                    String status = result.getStatus();
                    String user_nick = result.getUser_nick();
                    String photo = result.getPhoto();
                    Log.e(TAG, "onResponse: photo " + photo );

                    if (status.equals("true"))
                    {
                        tx_nick.setText(user_nick);


                        if(photo.equals("basic"))
                        {
                            img.setImageResource(R.drawable.profile2);
                        }
                        else{

                            String url = "http://3.39.168.165/userimg/" + photo;
                            Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(RcompanyActivity.this).load(url).into(img);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "fail");
                    }


                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

        // 게시글 id 에 맞는 데이터들 set 해주기
        retrofitInterface.setdata(id).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    CompanyData result = response.body();
                    Log.e(TAG, "onResponse: setDATA 결과 result:"+result );
                    String bannerurl = result.getPhoto(); // 게시글 배너 사진
                    String title = result.getTitle(); // 게시글 title
                    String startdate = result.getStartdate(); // 시작 날짜
                    String finishdate = result.getFinishdate(); // 끝 날짜
                    String mainspot = result.getMainspot(); // 메인 장소
                    String subspot = result.getSubspot(); // 서브 장소
                    String type = result.getType(); // 여행 타입
                    String company = result.getCompany(); // 모집 인원
                    String content = result.getContent(); // 게시글 content
                    String upload_date = result.getUpload_date(); // 게시글 올린 날짜
                    String attend = result.getAttend(); // 동행 참여 인원

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentTime = new Date();
                    String date = format.format(currentTime);

                    try {
                        Date toDate = format.parse(date);
                        Date endDate = format.parse(finishdate);

                        int compare = endDate.compareTo(toDate);

                        System.out.println("compare:"+ compare);

                        // 게시글 동행 모집 현황
                        // 동행 마지막 날 시간이 지나서 동행이 종료되었는지 1순위
                        // 동행 모집이 아직 진행중인지, 동행 모집이 완료되었는지 2순위
                        if(compare >= 0) {
                            System.out.println("동행 모집 기간 유효");

                            // 동행 모집 진행중인지, 동행 모집 완료되었는지 여부 확인해야 함.
                            ChangeCondition(id);


                        } else {
                            System.out.println("동행 모집 기간 완료");

                            //동행 종료 되었다고 tx_condition 변경해주기
                            tx_condition.setText(" 동행 종료 ");
                            tx_condition.setBackgroundColor(Color.parseColor("#454540"));

                            // 동행이 종료 되었으니 동행 요청 버튼 비활성화 되야 한다.
                            bottomLine = findViewById(R.id.bottomLine);
                            bottomLin = findViewById(R.id.bottomLin);

                            bottomLin.setVisibility(View.GONE);
                            bottomLine.setVisibility(View.GONE);




                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    // 채팅방 이동을 위한 게시글 데이터 생성자에 삽입
                    companyDataArrayList = new CompanyData(bannerurl,title,startdate,finishdate,mainspot,subspot,company,type,content,upload_date);


                    // 배너 이미지 세팅
                    if(bannerurl.equals("basic"))
                    {
                        // 기본 이미지 세팅
                        banner.setImageResource(R.drawable.img);
                    }
                    else{
                        // 기본이미지가 아닐때 url로 이미지 세팅
                        String url = "http://3.39.168.165/companyimg/" + bannerurl;
                        Glide.with(RcompanyActivity.this).load(url).into(banner);

                    }

                    tx_title.setText(title);
                    tx_start.setText(startdate);
                    tx_finish.setText(finishdate);
                    tx_main.setText(mainspot);
                    tx_sub.setText(subspot);
                    tx_type.setText(type);
                    tx_content.setText(content);
                    tx_people.setText(company);
                    tx_attend.setText(attend);


                }
            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e("onresponse", "게시글 데이터 SET 에러 : " + t.getMessage());
            }
        });

        
        // 동행글 상세보기의 프로필 사진 클릭시 해당 유저의 프로필로 이동
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Log.e(TAG, "onClick: user email :" + user_email);
                // 동행글 상세보기에서 해당 유저의 프로필로 이동

                if(user_email.equals(now_user_email))
                {
                    // 프로필사진 누른 사람이랑 글 올린사람이 같다면 자기 프로필로 이동해야 한다.
                    Intent intent = new Intent(RcompanyActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    // 다른 사람이라면 그 사람 프로필로 이동해야 한다.
                    Intent intent = new Intent(RcompanyActivity.this, OtherUserProfileActivity.class);
                    intent.putExtra("user_email", user_email);
                    startActivity(intent);

                }

            }
        });



        // 동행글 상세보기 페이지에서 작성한 유저만 수정 삭제가 가능하도록 구현.

        Log.e(TAG, "onCreate: new_user_email :" + now_user_email );
        Log.e(TAG, "onCreate: user_email : " + user_email );

        // 현재 shared에 저장되어 있는 유저의 이메일과 현재 프로필에 동륵되어 있는 유저의 이메일이 다르면 버튼 gone 상태
        // 문자열 비교할 때 == , != 사용하지 X
        // == 연산자는 문자열 데이터 값 비교하는 것이 아니라 주소값을 비교한다.
        if(!now_user_email.equals(user_email))
        {
            btn_menu.setVisibility(btn_menu.GONE);
            Log.e(TAG, "onCreate: 통과 1" );
        }
        else {
            // 같으면 버튼 생성
            Log.e(TAG, "onCreate: 통과 2" );
            btn_menu.setVisibility(btn_menu.VISIBLE);
            btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "onClick: error : 1" );

                    Log.e(TAG, "onClick: error : 2" );
                    Log.e(TAG, "onClick: id : " + id );
                    showDialog01();

                }
            });
        }



        // 동행글을 작성한 사람이 아니라면 북마크와 동행 신청 버튼이 보여야 한다.
        // 동행글을 작성한 사람이면 북마크, 동행신청 버튼 GONE 되어야 하고 동행 완료하기 버튼만 보여야 한다.

        if(now_user_email.equals(user_email)){
            // 현재 로그인한 유저 = 동행글을 작성한 유저
            // 북마크, 동행 신청 버튼 GONE 해버려야 함.
            bookmark.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);



            // 여기서 서버 통신을 해서 이 사람이 해당 게시글 동행 모집을 완료했는지 완료하지 않았는지 여부 판단해서 버튼을 나눠야 겠음.
            Log.e(TAG, "onCreate: 동행 모집 완료 여부 확인 id : " + id );
            // 게시글 id 잘 받아옴
            retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
                @Override
                public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                    if(response.isSuccessful() && response.body() != null){
                        ChatRoomData result = response.body();
                        Log.e(TAG, "onResponse: 동행 모집 완료 여부 확인 result : "+result );

                        String status = result.getStatus();
                        // 동행 모집이 완료됐는지 안됐는지 확인 하기
                        // status = true 동행 모집완료가 안됨 completion = 0
                        // status = false 동행 모집완료가 됨. completion = 1
                        if (status.equals("true"))
                        {
                            // 동행모집 완료X
                            Log.e(TAG, "onResponse: 동행 모집 완료 안됨" );
                            // 레이아웃에 해당 동적버튼 만들기
                            View.addView(btn_company);
                            btn_company.setText("동행 모집 완료하기");
                            btn_company.setTextSize(20);
                            btn_company.setLayoutParams(params);
                            btn_company.setBackgroundColor(Color.parseColor("#B2C7FC"));

                            // 동행글 작성자가 직접 동행글 모집 완료를 누를 수 있음
                            btn_company.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(android.view.View view) {
                                    Log.e(TAG, "onClick: 동행 모집 완료 버튼 클릭" );
                                    // 동행 모집 완료 버튼을 누르면 유저에게 다이얼로그 띄워주기
                                    // 동행 모집 완료하게 되면 더이상 채팅 신청 받을 수 없다는거 알려주기
                                    ShowCaution();

                                }
                            });

                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 동행 모집 완료 됨" );

                            // 여기에서 참여 인원과 모집인원 수 비교하기.
                            String attend = tx_attend.getText().toString();
                            String people = tx_people.getText().toString();

                            // 현재 참여 인원과 동행 모집인원이 같다면
                            if(attend.equals(people))
                            {
                                // 레이아웃에 해당 동적버튼 만들기
                                View.addView(btn_company);
                                btn_company.setText("동행 모집 완료");
                                btn_company.setTextSize(20);
                                btn_company.setLayoutParams(params);
                                btn_company.setBackgroundColor(Color.parseColor("#E8ACAEAE"));


                            }
                            else{
                                // 레이아웃에 해당 동적버튼 만들기
                                View.addView(btn_company);
                                btn_company.setText("동행 모집 재개하기");
                                btn_company.setTextSize(20);
                                btn_company.setLayoutParams(params);
                                btn_company.setBackgroundColor(Color.parseColor("#71CF4F"));

                                // 동행 재개하기 버튼 클릭
                                btn_company.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(android.view.View view) {
                                        // 동행 재개시 다시 참여신청을 받을 수 있다고 유저에게 알려주기
                                        ShowCaution();
                                    }
                                });
                            }



                        }
                    }
                    else{
                        Log.e(TAG, "동행 모집 완료 여부 확인 onResponse is fail " );
                    }

                }

                @Override
                public void onFailure(Call<ChatRoomData> call, Throwable t) {
                    Log.e("onresponse", "동행 모집 완료 여부 확인 에러 : " + t.getMessage());
                }
            });


        }
        // 현재 로그인한 사람과 동행글 작성한 사람이 다르다면
        else{
            // 동행 모집 진행중인지 아닌지를 확인
            // 동행 모집 중이라면 동행요청 버튼 활성화
            // 동행 모집 완료되었다면 동행요청 버튼 비활성화
            ChangeBtnChat(id);
        }


        // 현재 로그인한 유저가 해당 게시글 북마크 했을 경우 아이콘 selected 된 상태로 되어 있어야한다.
        retrofitInterface.staybookmark(id,now_user_email).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful())
                {
                    CompanyData result = response.body();
                    String status = result.getStatus();
                    String message = result.getMessage();
                    Log.e(TAG, "onResponse: status : " + status );
                    Log.e(TAG, "onResponse: message :" + message );

                    if(status.equals("true"))
                    {
                        Log.e(TAG, "onResponse: bookmark stay");
                        bookmark.setChecked(true);
                    }
                    else
                    {
                        Log.e(TAG, "onResponse: bookmark do not stay" );
                        bookmark.setChecked(false);
                    }

                }
                else
                {
                    Log.e(TAG, "onResponse: bookmark stay response is fail" );
                }
            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });


        // 북마트 버튼 부분
        // bookmark 버튼은 toggle button 이다.

        bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked)
                {

                    retrofitInterface.clickbookmark(id,now_user_email, true).enqueue(new Callback<CompanyData>() {
                        @Override
                        public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                            if(response.isSuccessful())
                            {
                                CompanyData result = response.body();
                                String status = result.getStatus();
                                String message = result.getMessage();
                                Log.e(TAG, "onResponse: status : " + status );
                                Log.e(TAG, "onResponse: message :" + message );
                                if(status.equals("true"))
                                {
                                    //Toast.makeText(getApplicationContext(), "북마크 선택",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.e(TAG, "onResponse: bookmark insert status is fail" );
                                }

                            }
                            else
                            {
                                Log.e(TAG, "onResponse: bookmark insert response is fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<CompanyData> call, Throwable t) {
                            Log.e("onresponse", "에러 : " + t.getMessage());
                        }
                    });
                }else
                {
                    Toast.makeText(getApplicationContext(), "북마크 해제",Toast.LENGTH_SHORT).show();
                    retrofitInterface.clickbookmark(id,now_user_email,false).enqueue(new Callback<CompanyData>() {
                        @Override
                        public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                            if(response.isSuccessful())
                            {
                                CompanyData result = response.body();
                                String status = result.getStatus();
                                String message = result.getMessage();
                                Log.e(TAG, "onResponse: status : " + status );
                                Log.e(TAG, "onResponse: message :" + message );

                                if(status.equals("true"))
                                {
                                    Toast.makeText(getApplicationContext(), "북마크 해제",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Log.e(TAG, "onResponse: bookmark delete status is fail" );
                                }

                            }
                            else
                            {
                                Log.e(TAG, "onResponse: bookmark delete response is fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<CompanyData> call, Throwable t) {
                            Log.e("onresponse", "에러 : " + t.getMessage());
                        }
                    });
                }
            }
        });

        // 동행 참여인원 리스트 리사이클러뷰 위한 레트로핏 통신
        retrofitInterface.ChatUserInfo(id).enqueue(new Callback<List<ChatUser>>() {
            @Override
            public void onResponse(Call<List<ChatUser>> call, Response<List<ChatUser>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    List<ChatUser> result = response.body();
                    Log.e(TAG, "onResponse: 채팅 참여 유저들 정보 : " + result );
                    // 참여중인 유저들 정보 잘 불러와짐
                    for(int i=0; i < result.size(); i++){
                        String profile = result.get(i).getProfile(); // 참여중인 유저의 프로필 사진
                        String nick = result.get(i).getNick(); // 참여중인 유저의 닉네임

                        chatUsers.add(result.get(i));
                        userRecyclerview.setAdapter(chatUserAdapter);
                        userRecyclerview.setLayoutManager(new LinearLayoutManager(RcompanyActivity.this, RecyclerView.VERTICAL, false));

                        // 어댑터 클래스에 이상이 있는지 먼저 확인
                        // 어댑터 생성을 액티비티에서 생성이 잘 되는지
                        // 값이 잘 들어가는지
                    }

                }
                else
                {
                    Log.e(TAG, "onResponse: 동행 참여 유저들 정보 불러오기 fail");
                }
            }

            @Override
            public void onFailure(Call<List<ChatUser>> call, Throwable t) {
                Log.e(TAG, "동행 참여 유저들 정보 불러오기 에러 = " + t.getMessage());
            }
        });



        // 동행신청 버튼 -> 해당 게시글에 맞는 채팅방 이동
        // 동행신청 버튼 누르기 전에 현재 로그인한 유저 즉 게시글을 보는 사람이 채팅방에 들어가있는지 아닌지 확인
        // 해당 게시글 채팅방에 들어가있다면 동행 요청 버튼 비활성화 해야 함.
        Log.e(TAG, "onCreate: CheckUserList id :" + id );
        Log.e(TAG, "onCreate: CheckUserList now_user_email :" + now_user_email );

        // 채팅방 숫자(게시글id), 현재 로그인한 유저의 이메일 매개변수로 보내기
        retrofitInterface.CheckUserList(id, now_user_email).enqueue(new Callback<ChatUser>() {
            @Override
            public void onResponse(Call<ChatUser> call, Response<ChatUser> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    ChatUser result= response.body();
                    Log.e(TAG, "onResponse: CheckUserList 결과 : " + result );

                    // 참여 여부 확인
                    String status = result.getStatus();
                    if(status.equals("true"))
                    {
                        btn_chat.setEnabled(false);
                        btn_chat.setBackgroundColor(Color.parseColor("#E8ACAEAE"));
                        btn_chat.setTextColor(Color.WHITE);
                    }
                }
                else{
                    Log.e(TAG, "onResponse: 채팅방 참여여부 확인하기 response fail");
                }
            }

            @Override
            public void onFailure(Call<ChatUser> call, Throwable t) {
                Log.e("onresponse", "채팅방 참여여부 확인하기 에러 : " + t.getMessage());
            }
        });

        // 동행 신청하는 버튼 클릭 리스너
        // 동행 신청을 할 때 현재 이 동행글이 동행 모집 중인지 아닌지를 확인해야 한다.
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이 사람이 현재 이 동행글에서 내쫓겼는지 아닌지 확인해야 함.
                retrofitInterface.KickOutCheck(id, now_user_email).enqueue(new Callback<ChatUser>() {
                    @Override
                    public void onResponse(Call<ChatUser> call, Response<ChatUser> response) {

                        if(response.isSuccessful() && response.body() != null)
                        {
                            ChatUser result = response.body();
                            String status = result.getStatus();
                            Log.e(TAG, "해당 동행글에서 내쫒겨 졌는지 확인 onResponse: result = "+result );

                            // true = 내쫒겨졌다는 뜻
                            if(status.equals("true"))
                            {
                                Log.e(TAG, "onResponse: 해당 유저 내쫒겨짐" );

                                showKickOut();
                            }
                            else{
                                // 동행글이 아직 모집 중인지 아닌지 확인 해야 함.
                                BeforeChat(id);

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ChatUser> call, Throwable t) {
                        Log.e("onresponse", "해당 동행글에서 내쫒겨 졌는지 확인하기 에러 : " + t.getMessage());
                    }
                });



            }
        });







    }

    // 차단되었으니 동행 신청 불가하다는 다이얼로그 띄워줘야함.
    private void showKickOut() {

        AlertDialog.Builder dig_kick = new AlertDialog.Builder(RcompanyActivity.this);
        dig_kick.setMessage("해당 동행글에 동행신청이 불가합니다.");
        dig_kick.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dig_kick.show();
    }

    // 게시글 작성자가 아닌 다른 유저가 해당 동행글에 들어왔을 때
    // 동행글 모집 현황에 따라서 버튼 활성화 비활성화 해놓기
    private void ChangeBtnChat(int id) {

        retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    ChatRoomData result = response.body();

                    String status = result.getStatus();

                    if(status.equals("true"))
                    {
                        // 아직 동행모집한다는 것.

                    }
                    else{
                        // 동행 모집이 완료되었다는 것.
                        // 동행 요청 버튼 비활성화 해야함.
                        btn_chat.setEnabled(false);
                        btn_chat.setBackgroundColor(Color.parseColor("#E8ACAEAE"));
                        btn_chat.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Log.e(TAG, "onResponse: ChangeBtnChat is fail " );
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.e("onresponse", "ChangeBtnChat 동행 모집 현황 확인하기 에러 : " + t.getMessage());
            }
        });

    }

    // 유저가 동행 요청을 하기 전 해당 동행글이 동행 모집 중인지 완료인지 확인 해야 함.
    // 그리고 참여 인원이 다 찼는지 확인
    private void BeforeChat(int id) {

        // 동행 모집 현황 확인하기
        retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    ChatRoomData result = response.body();
                    String status = result.getStatus();

                    // 아직 동행글이 모집 완료되지 않았을 때
                    // 동행글에 참여하겠냐는 다이얼로그 보여주기
                    if(status.equals("true"))
                    {

                        // 동행 신청 하겠냐는 다이얼로그가 뜨고 확인 버튼을 누르면 채팅방으로 이동하게 하기.
                        AlertDialog.Builder chat_dig = new AlertDialog.Builder(RcompanyActivity.this);
                        // 다이얼로그 메세지
                        chat_dig.setMessage("해당 게시글에 동행신청을 하시겠습니까?");
                        // 다이얼로그 "예"
                        chat_dig.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // 동행신청을 하면서 채팅방 입장하는 거니까 채팅 유저에 추가시켜야 함.
                                Log.e(TAG, "onClick: now use email :" + now_user_email );
//                        // 현재 로그인한 사람 이메일, 게시글 id(채팅방 숫자)
                                retrofitInterface.ApplyCompany(now_user_email, id).enqueue(new Callback<ChatRoomData>() {
                                    @Override
                                    public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                                        if(response.isSuccessful() && response.body() != null)
                                        {
                                            ChatRoomData result = response.body();
                                            Log.e(TAG, "onResponse: 동행요청하기 result : "+ result );

                                            String status = result.getStatus();

                                            // 레트로핏 통신 status가 true 일때 채팅 액티비티로 이동하게 하기
                                            if(status.equals("true"))
                                            {
                                                // 채팅 액티비티로 이동하게 하기.
                                                // 동행글에 해당하는 채팅방으로 입장하게 하기.
                                                Intent intent = new Intent(RcompanyActivity.this, Chat_Activity.class);
                                                // 이렇게 인텐트로 정보를 보내는 것이 효율적인가?
                                                intent.putExtra("roomNum", id); // 동행글 id - 채팅방 번호
                                                String upload_date = companyDataArrayList.getUpload_date();
                                                Log.e(TAG, "onResponse: 채팅보내기 upload_date :" + upload_date );
                                                intent.putExtra("made_time", upload_date); // 동행글이 만들어진 시점 - 채팅방이 만들어진 시점
                                                intent.putExtra("manager",user_email); // 동행글 작성자 - 채팅방 방장
                                                String company =  companyDataArrayList.getCompany();
                                                Log.e(TAG, "onResponse: 채팅보내기 company:"+company );
                                                intent.putExtra("people", Integer.parseInt(company)); // 동행 참여 모집 인원 - 채팅방 인원
                                                String title = companyDataArrayList.getTitle();
                                                Log.e(TAG, "onResponse: 채팅보내기 title:"+title );
                                                intent.putExtra("room_name", title); // 동행글 제목 - 채팅방 제목
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                Log.e(TAG, "onResponse: 동행 요청하기 레트로핏 통신 status is false" );
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "onResponse: 동행요청하기 fail" );
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChatRoomData> call, Throwable t) {
                                        Log.e("onresponse", "동행 요청하기 에러 : " + t.getMessage());
                                    }
                                });

                            }
                        });
                        // 다이얼로그 "아니오"
                        chat_dig.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        // 다이얼로그 보여주기.
                        chat_dig.show();
                    }
                    else
                    {
                        // 동행글이 이미 모집 완료되었다면
                        // 동행글이 이미 모집 완료라는 다이얼로그 창이 띄워져야 한다.
                        AlertDialog.Builder dig_confirm = new AlertDialog.Builder(RcompanyActivity.this);
                        // 다이얼로그 메세지
                        dig_confirm.setMessage("해당 게시글에 동행 모집이 완료되었습니다.");
                        dig_confirm.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dig_confirm.show();

                    }
                }
                else{

                    Log.e(TAG, "BeforeChat onResponse is fail " );
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.e("onresponse", "BeforeChat 채팅방 참여여부 확인하기 에러 : " + t.getMessage());
            }
        });


    }

    // 동행 모집 중인지 모집 완료인지 여부를 확인하기 위한 메서드
    private void ChangeCondition(int id) {
        Log.e(TAG, "ChangeCondition: id :" + id );
        // id 확인

        retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    ChatRoomData result = response.body();

                    String status = result.getStatus();
                    if(status.equals("true"))
                    {
                        // 아직 동행글이 모집완료 되지 않았음.
                        // 동행글 모집 중
                        tx_condition.setText(" 동행 모집 중 ");
                        tx_condition.setBackgroundColor(Color.parseColor("#0f40DB"));
                    }
                    else{
                        // 동행글이 모집 완료 됨.
                        // 동행글 모집 완료
                        tx_condition.setText(" 동행 모집 완료 ");
                        tx_condition.setBackgroundColor(Color.parseColor("#71CF4F"));
                    }

                }
                else
                {
                    Log.e(TAG, "ChangeCondition onResponse is fail " );
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.e("onresponse", "ChangeCondition 동행 모집 현황 확인 에러 : " + t.getMessage());
            }
        });

    }

    // 동행 모집 완료 버튼 눌렀을때, 동행 모집 재개 버튼을 눌렀을 때 나오는 다이얼로그 메서드
    private void ShowCaution() {


        // 이런이런...클릭을 했을 때 서버 통신을 또 해야겠구만..
        // 클릭했을 때 동행 모집 완료인지 아닌지 확인하기.
        retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    ChatRoomData result = response.body();

                    String status = result.getStatus();

                    if(status.equals("true"))
                    {
                        // 동행 모집이 완료되지 않았음-동행 모집 완료하시겠습니까? 다이얼로그
                        // 모집 완료 버튼 눌렀다면
                        // 모집 완료 하면 더이상 채팅신청 못받는다고 유저에게 알리기
                        AlertDialog.Builder dig_completion = new AlertDialog.Builder(RcompanyActivity.this);
                        // 다이얼로그 메세지
                        dig_completion.setTitle("동행 모집을 완료하시겠습니까?");
                        dig_completion.setMessage("동행 모집을 완료 상태로 변경하면\n더 이상 동행 신청을 받으실 수 없어요.");
                        // 취소 버튼
                        dig_completion.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        // 동행 모집 완료 버튼
                        dig_completion.setNegativeButton("동행 모집 완료하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Log.e(TAG, "동행 모집 완료하기 onClick: id : "+ id );
                                Log.e(TAG, "동행 모집 완료하기 onClick: status : "+ status );
                                retrofitInterface.CompleteOrResume(id,status).enqueue(new Callback<ChatRoomData>() {
                                    @Override
                                    public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                                        if(response.isSuccessful() && response.body() != null)
                                        {
                                            ChatRoomData result = response.body();
                                            Log.e(TAG, "동행 모집 완료하기 onResponse: result :" + result );

                                            // 동행 모집이 완료되었는지 확인.
                                            String status = result.getStatus();

                                            if(status.equals("success"))
                                            {
                                                // 동행 모집이 완료되었으니 동행 재개버튼으로 바꾸기
                                                // 계속 바꾸려고 할 때 한번만 바뀌고 연달아서 색상과 텍스트가 바뀌지 않았음.
                                                // 버튼 색상과 텍스트를 서버에서 모집 여부를 확인해서 바꿔줘야 할거같다.
                                                ChangeButton();
                                            }
                                            else{
                                                Log.e(TAG, "동행 모집 완료하기 onResponse : status is not success " );
                                            }

                                        }
                                        else
                                        {
                                            Log.e(TAG, "동행 모집 완료하기  onResponse is fail" );
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChatRoomData> call, Throwable t) {
                                        Log.e("onresponse", "동행 요청 완료하기 에러 : " + t.getMessage());
                                    }
                                });

                            }
                        });
                        dig_completion.show();
                    }
                    else
                    {
                        // 동행 모집이 완료 되었음-동행 모집 재개하시겠습니까? 다이얼로그

                    // 모집 재개 버튼을 눌렀다면
                    // 모집 재개 하면 다시 동행신청 받을 수 있다고 유저에게 알리기
                    AlertDialog.Builder dig_resume = new AlertDialog.Builder(RcompanyActivity.this);
                    // 다이얼로그 메세지
                    dig_resume.setTitle("동행 모집을 재개하시겠습니까?");
                    dig_resume.setMessage("동행 모집을 재개하시면\n새로운 동행 신청을 받을 수 있어요.");
                    // 취소 버튼
                    dig_resume.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    // 동행 재개 버튼
                    dig_resume.setNegativeButton("동행 모집 재개하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.e(TAG, "동행 모집 재개하기 onClick: id : "+ id );
                            Log.e(TAG, "동행 모집 재개하기 onClick: status : "+ status );
                            retrofitInterface.CompleteOrResume(id,status).enqueue(new Callback<ChatRoomData>() {
                                @Override
                                public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {
                                    if(response.isSuccessful() && response.body() != null)
                                    {
                                        ChatRoomData result = response.body();
                                        Log.e(TAG, "동행 재개하기 onResponse: result :" + result );

                                        // 동행 모집이 재개되었는지 확인.
                                        String status = result.getStatus();

                                        if(status.equals("success"))
                                        {
                                            // 동행 모집 재개하는 버튼으로 수정하기
                                            ChangeButton();


                                        }
                                        else{
                                            Log.e(TAG, "동행 모집 재개하기 onResponse : status is not success " );
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "동행 모집 재개하기  onResponse is fail" );
                                    }
                                }

                                @Override
                                public void onFailure(Call<ChatRoomData> call, Throwable t) {
                                    Log.e("onresponse", "동행 요청 재개하기 에러 : " + t.getMessage());
                                }
                            });
                        }
                    });
                    dig_resume.show();
                }

                }
                else
                {
                    Log.e(TAG, "다이얼로그 띄우기 전 동행 모집 완료 여부 확인하기 onResponse is fail " );
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {
                Log.e("onresponse", "다이얼로그 띄우기 전 동행 모집 완료 여부 확인하기 에러 : " + t.getMessage());
            }
        });
    }

    // 동행이 모집완료되었는지 재개되었는지 여부에 따른 버튼 색상, 텍스트 변경해주는 메서드
    private void ChangeButton() {
        Log.e(TAG, "ChangeButton: id :"+ id );

        // 동행이 모집 완료되었는지 재개되었는지 게시글 id를 넘겨서 확인해보기
        retrofitInterface.CheckComplete(id).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                ChatRoomData result = response.body();

                String status = result.getStatus();

                // 모집 완료가 되지 않았으니 모집 완료하기 버튼으로 만들어야함.
                if(status.equals("true"))
                {
                    btn_company.setText("동행 모집 완료하기");
                    btn_company.setTextSize(20);
                    btn_company.setLayoutParams(params);
                    btn_company.setBackgroundColor(Color.parseColor("#B2C7FC"));

                    tx_condition.setText(" 동행 모집 중 ");
                    tx_condition.setBackgroundColor(Color.parseColor("#0f40DB"));

                }
                // 모집 완료가 되었으니 모집 재개하기 버튼으로 만들어야 함.
                else{
                    btn_company.setText("동행 모집 재개하기");
                    btn_company.setTextSize(20);
                    btn_company.setLayoutParams(params);
                    btn_company.setBackgroundColor(Color.parseColor("#71CF4F"));

                    tx_condition.setText(" 동행 모집 완료 ");
                    tx_condition.setBackgroundColor(Color.parseColor("#71CF4F"));
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {

            }
        });
    }

    // 다이얼로그 보여주는 메서드
    public void showDialog01(){
        dialog01.show();

        // 수정하기 버튼
        Button button_modify = dialog01.findViewById(R.id.btn_modify);
        button_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: modify" );
                // 다이얼로그를 꼭 dismiss 해줘야 액티비티 실행됨. dismiss하지 않은상태에서 액티비티 이동 X
                dialog01.dismiss();
                Intent intent = new Intent(RcompanyActivity.this, UcompanyActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }

        });

        // 삭제하기 버튼
        Button button_delete = dialog01.findViewById(R.id.btn_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: delete " );

                dialog01.dismiss();
                // 제일 밑에 있던 다이얼로그 사라지고 새로운 다이얼로그 생성
                // 정말 삭제할 것이지 확인하는 확인 다이얼로그임.
                AlertDialog.Builder builder = new AlertDialog.Builder(RcompanyActivity.this);
                builder.setMessage("정말 삭제하시겠습니까?");
                // 예 버튼 클릭시 db에서 해당 게시글의 record가 delete됨.
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Log.e(TAG, "onClick: id : " + id);

                        // db에서 게시글 record 지우기 위한 레트로핏 통신
                retrofitInterface.deletecontent(id).enqueue(new Callback<CompanyData>() {
                    @Override
                    public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                        if(response.isSuccessful())
                        {
                           CompanyData result = response.body();
                           String status = result.getStatus();
                           String message = result.getMessage();

                           if(status.equals("true"))
                           {
                               Intent intent = new Intent(RcompanyActivity.this, HomeActivity.class);
                               Log.e(TAG, "onResponse: message : "  + message);
                               startActivity(intent);
                               finish();
                           }
                           else{
                               Log.e(TAG, "onResponse: status is false" );
                               Log.e(TAG, "onResponse: message " + message );
                           }
                        }
                        else
                        {
                            Log.e(TAG, "onResponse: false" );
                        }
                    }

                    @Override
                    public void onFailure(Call<CompanyData> call, Throwable t) {

                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });
                    }
                });
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
                builder.show();
            }
        });


        // 취소 버튼
        Button button_cancel = dialog01.findViewById(R.id.btn_cancle);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog01.dismiss();
            }
        });

    }


}