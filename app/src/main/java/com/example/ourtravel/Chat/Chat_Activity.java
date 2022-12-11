package com.example.ourtravel.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ourtravel.Adapter.ChatAdapter;
import com.example.ourtravel.Adapter.ChatUserAdapter;
import com.example.ourtravel.Adapter.ChatViewType;
import com.example.ourtravel.Object.Chat;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.Service.MyService;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.home.RcompanyActivity;
import com.example.ourtravel.retrofit.ChatData;
import com.example.ourtravel.retrofit.ChatRoomData;
import com.example.ourtravel.retrofit.ChatUser;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// 채팅을 주고 받는 곳
// 채팅방 리스트에서 하나를 클릭했을때 입장하는 액티비티이다.
// 이곳에서 어떤 기능이 있는지 어떤 것이 발생하는지 요약해서 적는다.
// 소켓으로 메세지를 전달한다. 소켓으로부터 메세지를 받아서 ~~해준다.

//

public class Chat_Activity extends AppCompatActivity {

    public final String TAG = getClass().getName();

    private ImageButton btn_send, btn_info,btn_add; // 채팅 보내기, 채팅방 상세보기 버튼, 사진 보내기 버튼
    private TextView tx_chatname, tx_date; // 채팅방 이름, 채팅방 생성 시간
    private EditText et_chat; // 채팅 메세지 쓰는 edittext
    private ScrollView scrollView; // 채팅 스크롤 뷰

    private TextView tv_people;

    private static String NOTIFICATION_CHANNEL_ID = "channel1";
    String url;

    public static int roomNum; // 채팅방 번호
    String made_time;// 채팅방 생성시간
    String manager;// 채팅방 방장
    int people;// 채팅방 총 인원
    String room_name; // 채팅방 이름

    String time; // 채팅 보낸 시간
    String content; // 채팅 내용 - chat_msg 이런식으로 변수 지으면 더 가독성 좋다.

    int lastposition; // 마지막 포지션 값 (이전 메세지)
    String chat_date; // 채팅 보낸 날짜 (년월일)
    String pre_date; // 이전 메세지 보낸 날짜 (년월일)
    ArrayList<String> photolist = new ArrayList<>(); // 사진 리스트

    // 현재 로그인한 회원 정보 담고있는 shared 선언
    private PreferenceHelper preferenceHelper;
    String now_user_email; // 현재 로그인한 유저의 이메일
    String now_user_profile; // 현재 로그인한 유저의 프로필 사진
    String now_user_nick; // 현재 로그인한 유저의 닉네임

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // 채팅방 상세 정보
    String mainspot ; // 메인 장소
    String subspot ; // 서브 장소
    String startdate ; // 시작 날짜
    String finishdate ; // 마지막 날짜
    String company ; // 동행 모집 인원
    String now_people; // 현재 채팅방에 있는 유저 수


    Gson gson;

    String gallery;
    String unitime;


    // 레트로핏
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // ========================채팅 리사이클러뷰======================
    private RecyclerView chatrecyclerView ;
    //  팅해줄 리사이클러뷰
    private ChatAdapter chatAdapter ;
    // 채팅 리사이클러뷰 어댑터
    private ArrayList<ChatData> chatDataArrayList;
    // 채팅 데이터를 담을 arraylist
    //==============================================================



    //======================채팅에 참여중인 유저 리스트 리사이클러뷰==================
    RecyclerView userRecyclerview = null;
    ChatUserAdapter chatUserAdapter = null;
    ArrayList<ChatUser> chatUsers ;
    //=========================================================================


    //=======================소켓 통신===============================
    private Handler mHandler; // 핸들러 객체 생성
    Socket socket;  // 소켓 객체 생성
    PrintWriter sendWriter; // 출력 스트림
    BufferedReader input;  // 입력 스트림
    private String ip = "3.39.168.165"; // 통신할 서버 ip
    private int port = 8888; // 서버 포트 지정
    String read;

    // 액티비티가 stop 됐을 때 출력 스트림, 소켓 close
    // onDestroy에 넣는 것이 좋다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(sendWriter != null)
            {
                sendWriter.close(); // 출력 스트림 close
            }
            if(input != null)
            {
                input.close(); // 입력 스트림 close
            }
            if(socket != null)
            {
                socket.close(); // 소켓 close
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        System.out.println("oncreate 들엉옴");
        // 컴포넌트 선언 부
        // Text View
        tx_chatname = findViewById(R.id.tx_chatname); // 채팅방 이름 = 동행글 제목
        tx_date = findViewById(R.id.tx_date); // 채팅방 생성 시간 = 동행글 작성 시간

        // Edittext
        et_chat = findViewById(R.id.et_chat); // 채팅 보내는 edittext


        // Image button
        btn_info = findViewById(R.id.btn_info); // 채팅방 - 동행글 상세보기로 이동 버튼
        btn_send = findViewById(R.id.btn_send); // 보내기 버튼 -> 채팅 보내기 버튼
        btn_add = findViewById(R.id.btn_add); // 사진 선택 버튼

        // ScrollView
        scrollView = findViewById(R.id.chat_sv);

        // Gson 객체
        gson = new GsonBuilder().create();

        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저의 이멜 = 아이디
        now_user_profile = preferenceHelper.getPROFILE(); // 현재 로그인한 유저의 프로필 사진
        now_user_nick = preferenceHelper.getNick();  // 현재 로그인한 유저의 닉네임


        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 리사이클러뷰 아이템 클릭 리스너에서 보내온 데이터 인텐트 받기
        Intent intent = getIntent();
        roomNum = (int)intent.getExtras().get("roomNum"); // 채팅방 번호


        //==================================================채팅 데이터 리사이클러뷰=====================================================
        chatrecyclerView = findViewById(R.id.rv_chat); // xml에 리사이클러뷰 연결
        chatDataArrayList = new ArrayList<>(); // 리사이클러뷰에 넣을 데이터 리스트 생성
        chatAdapter = new ChatAdapter(chatDataArrayList, getApplicationContext()); // 어뎁터에 데이터 리스트 넣어주기
        chatrecyclerView.setAdapter(chatAdapter); // 리사이클러뷰에 어댑터 넣어주기
        LinearLayoutManager manager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        chatrecyclerView.setLayoutManager(manager1); // 리사이클러뷰 레이아웃 수직으로 설정해주기.




        // 채팅방 기본 정보 서버에서 받아오기.
        retrofitInterface.ChatRoominfo(roomNum).enqueue(new Callback<ChatRoomData>() {
            @Override
            public void onResponse(Call<ChatRoomData> call, Response<ChatRoomData> response) {

                if(response.isSuccessful() && response.body() != null){
                    ChatRoomData result = response.body();
                    Log.e(TAG, "onResponse: result =" + result );
                    made_time = result.getMade_time();
                    manager = result.getManager();
                    people = result.getPeople();
                    room_name = result.getRoom_name();
                    // 1. 채팅방 이름
                    tx_chatname.setText(room_name);
                    Log.e(TAG, "onResponse: roomname =" + room_name );
                }
            }

            @Override
            public void onFailure(Call<ChatRoomData> call, Throwable t) {

            }
        });



        //==================================================================================================================

        //=================================================채팅에 참여중인 유저 리스트 리사이클러뷰================================================
        userRecyclerview = findViewById(R.id.rv_chatlist2);
        chatUsers = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(chatUsers, getApplicationContext());

        //===================================================================================================================================


        et_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });

            }
        });


        //======================================채팅 사이드 메뉴 관련==================================================

        // 채팅방 더보기에서 동행글 상세보기로 이동하기
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 채팅 액티비티에서 동행글 상세보기로 이동함.
                Intent intent = new Intent(Chat_Activity.this, RcompanyActivity.class);
                intent.putExtra("id", roomNum);
                intent.putExtra("user_email", manager);
                startActivity(intent);
                finish();
            }
        });

        // 사진 버튼 선택시 갤러리에서 사진 선택 할 수 있음.
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Chat_Activity.this, ChatGalleryActivity.class);
                intent.putExtra("roomNum", roomNum); // 채팅방 번호

                startActivityForResult(intent, 2000);
//                startActivity(intent);
//                finish();

            }
        });

        // 채팅 방 상세 정보 불러오기
        retrofitInterface.ChatInfo(roomNum).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    CompanyData result = response.body();
                    Log.e(TAG, "onResponse: 채팅방 상세 정보 :" + result);
                    mainspot = result.getMainspot();
                    Log.e(TAG, "onResponse: 레트로핏 통신 안에 mainspot:"+ mainspot );
                    subspot = result.getSubspot();
                    startdate = result.getStartdate();
                    finishdate = result.getFinishdate();
                    company = result.getCompany();
                    now_people = result.getMessage(); // 현재 이 채팅방에 참여하고 있는 유저 수

                    // 네비게이션 drawer의 header 안에 넣어줄 데이터들 처리하는 메서드
                    setHeader(startdate,finishdate,mainspot,subspot,company, now_people);
                }
                else
                {
                    Log.e(TAG, "onResponse: 채팅방 상세 정보 response fail" );
                }
            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {
                Log.e(TAG, "채팅방 상세정보 불러오기 에러 = " + t.getMessage());
            }
        });
        toolbar = (Toolbar)findViewById(R.id.toolbar); // 채팅 툴바
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 앱바 타이틀 비활성화 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); //왼쪽 상단 버튼 아이콘 지정 - 햄버거 아이콘

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout); // drawer layout 가져오기
        navigationView = (NavigationView)findViewById(R.id.navigation_view); // navigationview 가져오기


        //===============================================================================================================



        //레트로핏 통신으로 저장됐던 채팅 데이터 불러오기
        retrofitInterface.ChatData(roomNum).enqueue(new Callback<List<ChatData>>() {
            @Override
            public void onResponse(Call<List<ChatData>> call, Response<List<ChatData>> response) {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        List<ChatData> result = response.body();

                        Log.e(TAG, "onResponse: 채팅데이터 불러오기 : "+ result );


                        for(int i=0; i < result.size(); i++)
                        {
                            String chatuser = result.get(i).getChatUser(); // 채팅을 보낸 유저
                            String chat_message = result.get(i).getChat_message(); // 채팅 내용
                            String chat_time = result.get(i).getChat_time(); // 채팅 보낸 시간
                            int roomNum = result.get(i).getRoomNum(); // 방 숫자
                            String nick = result.get(i).getNick(); // 채팅 보낸 사람의 닉넴
                            String profile = result.get(i).getProfile(); // 채팅 보낸 사람 프로필
                            String chat_date = result.get(i).getChat_date(); // 채팅 보낸 최초 시간, 년월일
                            ArrayList<String> photolist = result.get(i).getPhoto_list(); // 사진 리스트 .
                            // 사진 리스트를

                                if(now_user_email.equals(chatuser))
                                {
                                    // 텍스트인지 이미지인지 구분 먼저 하기
                                    if(chat_message.equals("img"))
                                    {
                                        //내가 보냈던 메세지가 이미지라면
                                        for(int j=0; j<photolist.size(); j++)
                                        {
                                            chatDataArrayList.add(new ChatData(chatuser, photolist.get(j), chat_time, roomNum, nick, profile, chat_date, ChatViewType.right_photo));
                                            // 리사이클러뷰 가장 밑에 insert하기
                                            chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                        }

                                    }
                                    else{
                                        //내가 보냈던 메세지가 텍스트라면
                                        // 현재 채팅방에 접속해있는 유저와 채팅을 보낸 유저가 같다는 뜻.
                                        // viewtype right
                                        chatDataArrayList.add(new ChatData(chatuser, chat_message, chat_time, roomNum, nick, profile, chat_date, ChatViewType.right));
                                        // 리사이클러뷰 가장 밑에 insert하기
                                        chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                        // 리사이클러뷰 getItemCount & getItemCount -1 로그
                                        Log.e(TAG, "onResponse: chatAdapter.getItemCount() = " + chatAdapter.getItemCount());
                                        Log.e(TAG, "onResponse: chatAdapter.getItemCount()-1 = " + (chatAdapter.getItemCount()-1));
                                    }



                                }
                                else if(chatuser.equals("admin"))
                                {
                                    // 채팅방 일자, 채팅방이 생성되었습니다 같은 admin 채팅인 경우
                                    chatDataArrayList.add(new ChatData(chatuser, chat_message, chat_time, roomNum, nick, profile, chat_date, ChatViewType.date));
                                    // 리사이클러뷰 가장 밑에 insert하기
                                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                }
                                else
                                {
                                    // 수신 받은 메세지가 이미지인지 텍스트인지 먼저 구분하기
                                    if(chat_message.equals("img"))
                                    {
                                        // 이미지 라면
                                        //내가 보냈던 메세지가 이미지라면
                                        for(int j=0; j<photolist.size(); j++)
                                        {
                                            chatDataArrayList.add(new ChatData(chatuser, photolist.get(j), chat_time, roomNum, nick, profile, chat_date, ChatViewType.left_photo));
                                            // 리사이클러뷰 가장 밑에 insert하기
                                            chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                        }
                                    }
                                    else{
                                        // 현재 채팅방에 접속해있는 유저와 채팅을 보낸 유저가 다르다는 뜻
                                        // viewtype left
                                        chatDataArrayList.add(new ChatData(chatuser, chat_message, chat_time, roomNum, nick, profile, chat_date, ChatViewType.left));
                                        // 리사이클러뷰 가장 밑에 insert하기
                                        chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                    }


                                }

                                // 스크롤을 리사이클러뷰 맨 마지막으로 이동시키기
                                chatrecyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
                                //Log.e(TAG, "onResponse: THE LAST chatAdapter.getItemCount()-1 = " + (chatAdapter.getItemCount()-1));
                                // 리사이클러뷰 맨 마지막으로 이동이 깔끔하게 안되서 추가적으로 스크롤 한번 더 해주기
                                showItemList();



                         }
                    }
                    else
                    {
                        Log.e(TAG, "onResponse: 채팅데이터 불러오기 fail" );
                    }
            }

            @Override
            public void onFailure(Call<List<ChatData>> call, Throwable t) {
                Log.e(TAG, "채팅 데이터 불러오기 에러 = " + t.getMessage());
            }
        });

        // =================================서버에서 보낸 채팅 클라이언트에서 받기=========================================
        // 소켓 통신 위한 핸들러
        mHandler = new Handler(); // 핸들러 객체 생성

        new Thread() {
            public void run() {
                try {
//                    // ip 주소를 표현하기 위함.
//                    InetAddress serverAddr = InetAddress.getByName(ip); // getByName : 매개변수 ip에 대응되는 inetAddress 객체를 반환한다.
//                    socket = new Socket(serverAddr, port);
//                    sendWriter = new PrintWriter(socket.getOutputStream());
                    // 누가 접속했는지 확인하기 위한 코드
//                    // 유저 id 보내기
//                    sendWriter.println(now_user_email);
//                    sendWriter.flush();
                    MyService.input = new BufferedReader(new InputStreamReader(MyService.socket.getInputStream()));

                    // 무한 루프를 돌면서 서버에서 오는 메세지를 받기 위함.
                    while(true){
                        // read는 string 변수. 서버에서 보낸 메세지 읽는 것.
                        read = MyService.input.readLine();
                        System.out.println(read);
                        // 서버에서 전달 받은 메세지
                        // ex ) monspirit34@naver.com///hi///06 : 05 오후///95///coffee///20220819010947.jpg 로 메세지가 리턴 된다.



                        if(read!=null){
                            // post()는 Message 객체가 아닌 Runnable 객체를 Message queue에 전달한다.
                            Log.e(TAG, "run: 서버받은 메세지" );
                            mHandler.post(new msgUpdate(read));
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();
        // ===========================================================================================





        // =================================채팅 보내기==================================================
        // 채팅보내려고 edittext 선택하면 리사이클러뷰 맨 마지막으로 스크롤 하기. -> 채팅 데이터 불러오고 그 이후에 구현하기.

        // 채팅 보내기 버튼을 클릭했을 때 -> 채팅 보내기
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //===========================채팅 보낸 시간==============================================
                // 한국 표준시 가져오기
                TimeZone tz;
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("hh : mm aa", Locale.KOREA);
                // dateformat은 dateformat 클래스에서 추상 클래스 이므로 simpledateformat와 같은 서브 클래스를 사용해 날짜/ 시간 형식화, 구문 분석등의 목적으로 주로 사용한다.

                TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
                tz = TimeZone.getTimeZone("Asia/Seoul");
                dateFormat.setTimeZone(tz);
                time = dateFormat.format(date);
                Log.e(TAG, "onCreate: 시간 타입 :"+ dateFormat.format(date).getClass().getName() );
                Log.e(TAG, "onCreate: date : " + dateFormat.format(date) );
                //===========================================================================================

                //================================채팅 시간 : 년 월 일 가져오기=================================================
                // 현재 시간 가져오기
                long now = System.currentTimeMillis();
                // Date 형식으로 고치기
                chat_date = now_date(now);


                //==============================================================================================

                content = et_chat.getText().toString(); // 유저가 쓴 채팅 내용
                lastposition = chatrecyclerView.getAdapter().getItemCount() -1;
                Log.e(TAG, "onClick: lastposition :"+lastposition );
                pre_date = chatDataArrayList.get(lastposition).getChat_date();

                Log.e(TAG, "onClick: chat_date : " + chat_date );
                Log.e(TAG, "onClick: pre_date :"+ pre_date );
                // chat_date, pre_date 비교해봤을 때 다른거 확인함.

                // java 객체에 해당 데이터를 담아서
                // gson으로 해당 객체를 json표현으로 변환시키고 -> string으로 변환
                // 해당 string을 json으로 변환시키고
                //

                // json object로 채팅 데이터들을 넣어서 전송하기
                JsonObject textobject = new JsonObject();


                // 채팅 보낸 사람, 채팅 보낸 시간, 채팅 보낸 사람의 프로필, 채팅 보낸 사람의 닉네임, 채팅 내용, 어떤 채팅방인지
                unitime = "text";
                Log.e(TAG, "onClick: text unitime = " + unitime );
                Chat chat = new Chat(now_user_email, time, content, now_user_nick, now_user_profile, chat_date, pre_date, roomNum, photolist, unitime);
                String chatdata = gson.toJson(chat);
                Log.e(TAG, "onClick: chatdata = " + chatdata );
                // json 형식으로 string 출력 잘 됨.

                sendChat(chatdata);

                // 이미지는 다르게 해서 보내기

            }
        });

        // ============================================================================================




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2000)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                gallery = data.getStringExtra("gallery");
                unitime = data.getStringExtra("unitime");

                Log.e(TAG, "onActivityResult: gallery = " + gallery);
                Log.e(TAG, "onActivityResult: unitime =" + unitime );

                getChatImg(roomNum, unitime, gson);


            }
        }
    }

    // db에 저장돼 있는 사진 리스트를 가져와야 함.
    private void getChatImg(int roomNum, String unitime, Gson gson) {
        // 채팅보낼때 필요한 것들
        //  sendChat(now_user_email, time, content, roomNum, now_user_nick, now_user_profile, chat_date, pre_date);
        // 일단 이미지 리스트를 가져와야 한다.
        retrofitInterface.GetChatImg(roomNum, unitime).enqueue(new Callback<List<ChatData>>() {
            @Override
            public void onResponse(Call<List<ChatData>> call, Response<List<ChatData>> response) {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<ChatData> result = response.body();
                    Log.e(TAG, "onResponse: 서버에 있는 이미지 result = " +result );
                    // 이미지 리스트 잘 받아와짐.
                    ArrayList<String> photolist = new ArrayList<>();
                    photolist = result.get(0).getPhoto_list();
                    Log.e(TAG, "onResponse: photolist = " + photolist );

                    //json object를 생성하고
                    //json object에 key value put 해줘야 함.
                    //채팅때 서버로 보내야할 정보들 , 이미지 list
                    //json array만들어서 인자로 이미지list 넣고
                    //json array를 json object안에 넣고 다른 value 값들도 넣어서 보내기
                    //sendchat 메서드에 보내기

                    //자바 객체를 만들어서 gson으로 json형식의 문자열로 변형해야 함.
                    //chatdata는 이미 생성자가 만들어져서 채팅 불러와서 set할때 사용중임.
                    //새로운 자바객체를 만들어야 할 듯 하다.

                    sendphoto(gson, photolist);






                }
                else
                {
                    Log.e(TAG, "onResponse: 서버에 있는 이미지 result response is fail" );
                }
            }

            @Override
            public void onFailure(Call<List<ChatData>> call, Throwable t) {
                Log.e(TAG, "서버에 있는 이미지 result 불러오기 에러 = " + t.getMessage());
            }
        });


    }



    // 사진 보낼떄 전송 메서드
    private void sendphoto(Gson gson, ArrayList<String> photolist) {

        //===========================채팅 보낸 시간==============================================
        // 한국 표준시 가져오기
        TimeZone tz;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("hh : mm aa", Locale.KOREA);
        // dateformat은 dateformat 클래스에서 추상 클래스 이므로 simpledateformat와 같은 서브 클래스를 사용해 날짜/ 시간 형식화, 구문 분석등의 목적으로 주로 사용한다.

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        tz = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(tz);
        time = dateFormat.format(date);
        Log.e(TAG, "sendphoto onCreate: 시간 타입 :"+ dateFormat.format(date).getClass().getName() );
        Log.e(TAG, "sendphoto onCreate: date : " + dateFormat.format(date) );
        //===========================================================================================

        //================================채팅 시간 : 년 월 일 가져오기=================================================
        // 현재 시간 가져오기
        long now = System.currentTimeMillis();
        // Date 형식으로 고치기
        chat_date = now_date(now);


        //==============================================================================================
        Log.e(TAG, "sendphoto: chatrecyclerView.getAdapter().getItemCount() = " + chatrecyclerView.getAdapter().getItemCount() );
        lastposition = chatrecyclerView.getAdapter().getItemCount() -1;
        Log.e(TAG, "onClick: lastposition :"+lastposition );
        pre_date = chatDataArrayList.get(lastposition).getChat_date();

        Log.e(TAG, "sendphoto onClick: chat_date : " + chat_date );
        Log.e(TAG, "sendphoto onClick: pre_date :"+ pre_date );
        // chat_date, pre_date 비교해봤을 때 다른거 확인함.

        // 지금 보내는 chat data는 이미지라는 사실을 알려주기 위함.
        content = "img";

        Chat chat = new Chat(now_user_email, time, content, now_user_nick, now_user_profile, chat_date, pre_date, roomNum, photolist, unitime);
        String chatdata = gson.toJson(chat);
        Log.e(TAG, "onClick: chatdata = " + chatdata );
        // json 형식으로 string 출력 잘 됨.
        // chatdata 잘 받아와짐

        sendChat(chatdata);
        // 서버에서 사진 list 보내지는거 확인함.

    }

    // 채팅방 처음 들어왔을때 가장 최근에 보낸 메세지로 포커싱 되도록 하는 메서드
    private void showItemList() {
      scrollView.post(new Runnable() {
          @Override
          public void run() {
            scrollView.scrollTo(0, 60);
          }
      });
    }

    // 네비게이션 drawer 안의 header에 들어갈 데이터 세팅해주기 위한 메서드
    public void setHeader(String startdate, String finishdate, String mainspot, String subspot, String company, String now_people){

        LinearLayout  ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navi_header, null);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(150, 150);
        LinearLayout.LayoutParams text_parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // 구분선
        final View view = new View(this);
        final View view1 = new View(this);
        final View view2 = new View(this);
        view.setBackgroundColor(Color.BLACK);
        view1.setBackgroundColor(Color.BLACK);
        view2.setBackgroundColor(Color.BLACK);

        // 공백
        final TextView gong = new TextView(this);
        final TextView gong1 = new TextView(this);
        final TextView menu_title = new TextView(this);
        menu_title.setTextColor(Color.parseColor("#FFFFFF"));
        menu_title.setTextSize(25);
        menu_title.setTypeface(Typeface.DEFAULT_BOLD);

        final TextView party_title = new TextView(this);
        party_title.setTextColor(Color.parseColor("#FFFFFF"));
        party_title.setTextSize(17);
        party_title.setTypeface(Typeface.DEFAULT_BOLD);

        // 동행글 제목
        final TextView tv_title = new TextView(this);
        tv_title.setTextColor(Color.parseColor("#FFFFFF"));
        tv_title.setTextSize(17);
        tv_title.setTypeface(Typeface.DEFAULT_BOLD); //텍스트 스타일 변경

        // 동행 지역
        final TextView tv_where = new TextView(this);
        tv_where.setTextColor(Color.parseColor("#FFFFFF"));
        tv_where.setTextSize(17);
        tv_where.setTypeface(Typeface.DEFAULT_BOLD);

        // 여행 동행 기간
        final TextView tv_term = new TextView(this);
        tv_term.setTextColor(Color.parseColor("#FFFFFF"));
        tv_term.setTextSize(17);
        tv_term.setTypeface(Typeface.DEFAULT_BOLD);

        // 동행 모집 인원
        tv_people = new TextView(this);
        tv_people.setTextColor(Color.parseColor("#FFFFFF"));
        tv_people.setTextSize(17);
        tv_people.setTypeface(Typeface.DEFAULT_BOLD);

        menu_title.setText("\n 채팅방 더보기\n" );
        tv_title.setText(" \n 동행 제목   " + room_name);
        Log.e(TAG, "onCreate: mainspot :" + mainspot );
        tv_where.setText(" 동행 지역   [ " + mainspot + "  -  " + subspot + " ]");
        tv_term.setText(" 여행 기간   "+ startdate + "  ~  " + finishdate);
        tv_people.setText(" 모집 인원   "+ now_people +"  /  " + company +"\n\n");
       // party_title.setText("\n 대화 상대\n");


        // ll_navigation_container에 만든 요소들을 담음
        ll_navigation_container.addView(menu_title);
        ll_navigation_container.addView(view, params);
        ll_navigation_container.addView(tv_title);
        ll_navigation_container.addView(tv_where);
        ll_navigation_container.addView(tv_term);
        ll_navigation_container.addView(tv_people);
        ll_navigation_container.addView(view1, params);
        //ll_navigation_container.addView(party_title);
        ll_navigation_container.addView(view2, params);
        ll_navigation_container.addView(gong);


        Log.e(TAG, "setHeader: roomNum :"+ roomNum );
        retrofitInterface.ChatUserInfo(roomNum).enqueue(new Callback<List<ChatUser>>() {
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
                        userRecyclerview.setLayoutManager(new LinearLayoutManager(Chat_Activity.this, RecyclerView.VERTICAL, false));

                        // 어댑터 클래스에 이상이 있는지 먼저 확인
                        // 어댑터 생성을 액티비티에서 생성이 잘 되는지
                        // 값이 잘 들어가는지



                    }

                    chatUserAdapter.setOnItemClickListener(new ChatUserAdapter.OnItemClickListener() {

                        // 유저 프로필 사진 클릭하면 해당 유저의 프로필로 이동하는 것.
                        @Override
                        public void onImgClick(View v, int pos) {

                            String user_email = result.get(pos).getUser_email();
                            Log.e(TAG, "onImgClick: user_email =" + user_email );



                            // 클릭한 채팅방 참여 유저가 현재 로그인한 유저라면
                            if(now_user_email.equals(user_email))
                            {
                                Intent intent = new Intent(Chat_Activity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(Chat_Activity.this, OtherUserProfileActivity.class);
                                intent.putExtra("user_email", user_email);
                                startActivity(intent);
                                finish();

                            }

                            // 현재 채팅방에서 유저 프로필 사진을 클릭한 후 각각의 프로필로 이동한 다음에
                            // 뒤로가기 버튼을 눌렀을 때 채팅방 리스트로 이동하게 하기
                        }

                        // 매니저가 유저 내쫒기
                        @Override
                        public void onKickOutClick(View v, int pos) {

                            // 현재 로그인한 사람이 매니저가 아니라면 아무일도 생기면 안된다.
                            if(now_user_email.equals(manager))
                            {
                                // 현재 로그인한 유저는 매니저이다.
                                Log.e(TAG, "onKickOutClick: 현재 유저 매니저임" );

                                // 현재 유저가 클릭한 사람이 매니저인지 다른 참여인원인지 확인
                                String user_email = result.get(pos).getUser_email();
                                String manager = result.get(pos).getManager();

                                if(user_email.equals(manager))
                                {
                                    // 현재 클릭한 유저가 매니저라면 아무일도 생기면 안된다.
                                    Log.e(TAG, "onKickOutClick: 현재 클릭한 유저 매니저임" );
                                }
                                else{
                                    Log.e(TAG, "onKickOutClick: 현재 클릭한 유저 매니저 아님" );
                                    // 여기서 클릭을 했을 때 이벤트가 발생해야 한다.

                                    // 클릭을 했을 때
                                    // 동행 참여 인원 중 비매너 혹은 잠수(유령)유저와 더이상 대화하고 싶지 않을 경우
                                    // 해당 유저를 채팅방에서 내보낼수 있습니다.
                                    // 한번 내보내기 처리된 유저는 같은 동행 모집 글에 다시 참여할 수 없습니다.
                                    // 내쫒긴 유저는 해당 사실을 몰라야하나..?

                                    // 해당유저 내보내겠냐고 물어보는 다이얼로그 띄우기
                                    showKickOutConfirm(pos, user_email);
                                }

                            }
                            else{
                                // 현재 로그인한 유저는 매니저가 아니다.
                                Log.e(TAG, "onKickOutClick: 현재 유저 매니저 아님" );
                            }
                        }
                    });

                }
                else
                {
                    Log.e(TAG, "onResponse: 채팅 참여 유저들 정보 불러오기 fail");
                }
            }

            @Override
            public void onFailure(Call<List<ChatUser>> call, Throwable t) {
                Log.e(TAG, "채팅 참여 유저들 정보 불러오기 에러 = " + t.getMessage());
            }
        });

        // ll_navigation_container.addView(img, img_params);


        navigationView.addHeaderView(ll_navigation_container);
    }

    // 해당 유저 내보낼꺼냐고 물어보는 다이얼로그
    private void showKickOutConfirm(int pos, String user_email) {

        //    // 동행 참여 인원 중 비매너 혹은 잠수(유령)유저와 더이상 대화하고 싶지 않을 경우
        //                                    // 해당 유저를 채팅방에서 내보낼수 있습니다.
        //                                    // 한번 내보내기 처리된 유저는 같은 동행 모집 글에 다시 참여할 수 없습니다.
        AlertDialog.Builder dig_kickout = new AlertDialog.Builder(Chat_Activity.this);
        dig_kickout.setTitle("해당 유저를 내보내겠습니까?");
        dig_kickout.setMessage("다만, 한번 내보내기 처리된 유저는 같은 동행 모집 글에 다시 참여할 수 없습니다.");
        dig_kickout.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 내보냈으니까 유저 리스트에서 해당 유저는 없어져야 하며

                chatUsers.remove(pos);
                chatUserAdapter.notifyItemRemoved(pos);
                // 리스트에서 선택한 유저 없애기

                // 해당 유저는 해당 동행글에 다시 참여할 수가 없다.
                retrofitInterface.KickOutUser(roomNum, user_email).enqueue(new Callback<ChatUser>() {
                    @Override
                    public void onResponse(Call<ChatUser> call, Response<ChatUser> response) {

                        // chatuser table에서 해당 유저 삭제해야하고
                        // kickoutuser table에서 해당 유저 추가해야한다.

                        if(response.isSuccessful() && response.body() != null)
                        {
                            ChatUser result= response.body();
                            String status = result.getStatus();
                            String people = result.getMessage(); // 현재 남은 사람
                            Log.e(TAG, "유저 내보내기 후 onResponse: result = " + result );

                            if(status.equals("true"))
                            {
                                // 유저 내보내기 성공
                                tv_people.setText(" 모집 인원   "+ people +"  /  " + company +"\n\n");
                                // 현재 참여 인원 수 줄어들게 하기.


                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 유저내보내기 status is false" );
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ChatUser> call, Throwable t) {
                        Log.e(TAG, "유저 내보내기 에러 = " + t.getMessage());
                    }
                });

            }
        });
        dig_kickout.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dig_kickout.show();
    }

    // 툴바의 메뉴 버튼 클릭시 - navigation view 나오는 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 했을 때 drawer가 열려있으면 닫고 아니면 뒤로가기.
    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String now_date(long now) {
        Date mdate = new Date(now);
        // 날짜, 시간을 가져오고 싶은 형태로 가져오기
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getTime = simpleDate.format(mdate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mdate);

        int dayOfWeekNumber = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "onCreate: 요일 : " + dayOfWeekNumber );
        String str_week ="";

        switch (dayOfWeekNumber){
            case 1:
                str_week ="일요일";
                break;

            case 2 :
                str_week ="월요일";
                break;

            case 3:
                str_week ="화요일";
                break;

            case 4:
                str_week ="수요일";
                break;

            case 5:
                str_week ="목요일";
                break;

            case 6:
                str_week ="금요일";
                break;

            case 7:
                str_week ="토요일";
                break;
        }

        Log.e(TAG, "onCreate: getTime :"+ getTime);
        String chat_date = getTime +" "+ str_week;
        Log.e(TAG, "onClick: 채팅보낸 년월일 chat_date : " + chat_date );
        return chat_date;
    }


    public void sendChat(String chatdata){
        Log.e(TAG, "sendChat: 들어옴" );

//        // printwriter null값 떴음
//        MyService myService = new MyService();
//        myService.sendmsg(chatdata);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e(TAG, "run: userId :"+ now_user_email );
                    Log.e(TAG, "run: sendmsg: "+content );
                    // userid, sendmsg 잘 찍힘.
                    Log.e(TAG, "run: sendWriter :"+ sendWriter );
                    // 메세지를 보낼 때 시간과 현재의 시간을 비교해야 한다.

                    // 서버에 데이터들 json 형식으로 보내기
                    MyService.sendWriter.println(chatdata);

                    // 사진 -> list에 넣어서 json 형식으로 보내기

                    MyService.sendWriter.flush();
                    // 채팅을 보낸 다음 et_chat이 공백이 되어야 한다.
                    et_chat.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 서버에서 받은 메세지를 받아서 채팅부분에 세팅해주기 위해서 돌고 있는 스레드
    class msgUpdate implements Runnable{
        private String msg;

        private static final String TAG = "msgUpdate";


        // 생성자 -> 여기서 str은 json 형식으로 데이터 정리된 채팅 데이터
        public msgUpdate(String str) {
            Log.e(TAG, "msgUpdate: 서버에서 메세지 받음 :"+ str );
            this.msg=str;
        }




        @Override
        public void run() {

            // 지금 보낸 채팅 메세지가 제일 마지막에 보내진 채팅 메세지와 일자가 다르다면
            // 바뀐 일자를 표시해준다.

            try{
                JSONObject jsonObject = new JSONObject(msg);

                String user_email = jsonObject.getString("now_user_email"); // 채팅 보낸 유저 이메일 0
                String content = jsonObject.getString("content"); // 채팅 내용 1
                String time = jsonObject.getString("time"); // 채팅 보낸 시간 2
                int room_num = Integer.parseInt(jsonObject.getString("roomNum")); // 문자열 -> 정수 변환 , 채팅방 숫자 표헌으로 구분 3
                String nick = jsonObject.getString("now_user_nick"); // 채팅 보낸 사람 닉네임 4
                String profile = jsonObject.getString("now_user_profile"); // 채팅 보낸 사람 프로필 사진 5
                String chat_date = jsonObject.getString("chat_date"); // 채팅보낸 년월일 6
                String pre_date = jsonObject.getString("pre_date"); // 바로전에 보낸 채팅 시간, 년월일 7
                JSONArray photolist = jsonObject.getJSONArray("photolist");

                Log.e(TAG, "run: photolist = " + photolist );
                // photolist = ["photo0_20220920093828.jpg","photo1_20220920093828.jpg"]

                // json에서 list를 받아서 for문으로 add를 시키는데 사진 이름 하나를 content로 넣어서 add 시키기
                // 안드로이드에서 사진을 보낼때는 content에 img라고 보낼것임.
                // content가 img라면 jsonobject에서 array 빼기?

                // 채팅서버에서 온 채팅메세지가 내 채팅방에서 온 건지 아닌지 확인먼저 해야함
                if(roomNum == room_num)
                {

                    if(!pre_date.equals(chat_date))
                    {
                        chatDataArrayList.add(new ChatData(user_email, chat_date, time, room_num, nick, profile, chat_date, ChatViewType.date));
                        chatAdapter.notifyDataSetChanged();

                        if(user_email.equals(now_user_email))
                        {
                            // 보낸 사람이랑 같은데 이미지를 보냈는지 텍스트를 보냈는지 먼저 확인
                            if(content.equals("img"))
                            {
                                // 현재 로그인한 유저가 이미지를 보냈다면
                                for(int i=0; i < photolist.length(); i++)
                                {
                                    // content에 사진 이름이 들어가야 한다.
                                    Log.e(TAG, "run: 사진 하나 = " + photolist.get(i) );
                                    chatDataArrayList.add(new ChatData(user_email, photolist.get(i).toString(), time, room_num, nick, profile, chat_date, ChatViewType.right_photo));
                                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                    SendMsgScroll();
                                }

                            }
                            else{

                                // 현재 로그인한 유저가 텍스트를 보냈다면

                                // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 같다면 오른쪽 뷰홀더 바인딩
                                chatDataArrayList.add(new ChatData(user_email, content, time, room_num, nick, profile, chat_date, ChatViewType.right));
                                // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                // 리사이클러뷰 맨 마지막으로 스크롤 이동이 안되고 있음..
                                chatrecyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
                                SendMsgScroll();

                            }



                        }
                        else
                        {
                            if(content.equals("img"))
                            {
                                // 수신 받은 메세지가 이미지라면
                                for(int i=0; i < photolist.length(); i++)
                                {
                                    // content에 사진 이름이 들어가야 한다.
                                    Log.e(TAG, "run: 사진 하나 = " + photolist.get(i) );
                                    chatDataArrayList.add(new ChatData(user_email, photolist.get(i).toString(), time, room_num, nick, profile, chat_date, ChatViewType.left_photo));
                                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                }
                            }
                            else{
                                // 수신 받은 메세지가 이미지가 아니라 텍스트라면
                                // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 다르다면 왼쪽 뷰홀더 바인딩
                                chatDataArrayList.add(new ChatData(user_email, content, time, room_num, nick, profile, chat_date, ChatViewType.left));
                                // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                chatAdapter.notifyItemInserted(chatDataArrayList.size());
                            }

                        }
                    }
                    else {
                        // 현재 발신, 수신 하는 상황이 맨 마지막에 보내진 메세지와 시간을 비교했을 때 다르지 않다면 = 시간이 흐르지 않았다면

                        if(user_email.equals(now_user_email))
                        {
                            if(content.equals("img"))
                            {
                                // 발신한 메세지가 이미지라면
                                for(int i=0; i < photolist.length(); i++)
                                {
                                    // content에 사진 이름이 들어가야 한다.
                                    Log.e(TAG, "run: 사진 하나 = " + photolist.get(i) );
                                    chatDataArrayList.add(new ChatData(user_email, photolist.get(i).toString(), time, room_num, nick, profile, chat_date, ChatViewType.right_photo));
                                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                    SendMsgScroll();
                                }

                            }
                            else{
                                // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 같다면 오른쪽 뷰홀더 바인딩
                                chatDataArrayList.add(new ChatData(user_email, content, time, room_num, nick, profile, chat_date, ChatViewType.right));
                                // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                chatAdapter.notifyItemInserted(chatDataArrayList.size());
                            }



                        }
                        else
                        {

                            if(content.equals("img"))
                            {
                                // 수신 받은 메세지가 이미지라면

                                for(int i=0; i < photolist.length(); i++)
                                {
                                    // content에 사진 이름이 들어가야 한다.
                                    Log.e(TAG, "run: 사진 하나 = " + photolist.get(i) );
                                    chatDataArrayList.add(new ChatData(user_email, photolist.get(i).toString(), time, room_num, nick, profile, chat_date, ChatViewType.left_photo));
                                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
                                    SendMsgScroll();
                                }

                            }
                            else{
                                // 수신 받은 메세지가 텍스트라면

                                // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 다르다면 왼쪽 뷰홀더 바인딩
                                chatDataArrayList.add(new ChatData(user_email, content, time, room_num, nick, profile, chat_date, ChatViewType.left));
                                // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
                                chatAdapter.notifyItemInserted(chatDataArrayList.size());
                            }

                        }
                    }
                }
                else
                {
                    // room_num 채팅에서 온 채팅방 id
                    // 서버에서 온 메세지가 현재 내 채팅방에서 온건 아니지만
                    // 내가 속해 있는 채팅방인 경우

                    retrofitInterface.CheckUserList(room_num, now_user_email).enqueue(new Callback<ChatUser>() {
                        @Override
                        public void onResponse(Call<ChatUser> call, Response<ChatUser> response) {

                            ChatUser result = response.body();

                            Log.e(TAG, "다른 채팅방에서 채팅 온 후 onResponse: result = " + result );
                            // 참여하고 있는지 아닌지에 대한 상태값
                            String status = result.getStatus();
                            String img = result.getMessage(); // 채팅방 이미지
                            String title= result.getNick(); // 채팅방 제목
                            Log.e(TAG, "onResponse: result = " + result );
                            Log.e(TAG, "onResponse: status =" + status );
                            // 채팅방에 참여하고 있지 않으면 false, 참여하고 있으면 true

                            if(status.equals("true"))
                            {
                                // 채팅이 온 채팅방에 내가 참여하고 있다는 뜻
                                // 알림을 띄워줘야 한다.
                                NotificationSomethings(img, title, nick, content,room_num);

                            }
                            else{
                                // 알림 띄워주지 X
                            }

                        }

                        @Override
                        public void onFailure(Call<ChatUser> call, Throwable t) {

                        }
                    });
                }




            }catch (JSONException e)
            {
                e.printStackTrace();
            }


//
//            if(!strings[7].equals(strings[6]))
//            {
//                // 채팅방 일자, 채팅방이 생성되었습니다. 나타내주는 뷰홀더
//                chatDataArrayList.add(new ChatData(strings[0],strings[6],strings[2],Integer.parseInt(strings[3]),strings[4],strings[5], strings[6], ChatViewType.date));
//                chatAdapter.notifyDataSetChanged();
//
//
//                if(strings[0].equals(now_user_email)) {
//
//                    // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 같다면 오른쪽 뷰홀더 바인딩
//                    chatDataArrayList.add(new ChatData(strings[0],strings[1],strings[2],Integer.parseInt(strings[3]),strings[4],strings[5], strings[6], ChatViewType.right));
//                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
//                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
//                    // 리사이클러뷰 맨 마지막으로 스크롤 이동이 안되고 있음..
//                    chatrecyclerView.scrollToPosition(chatAdapter.getItemCount()-1);
//                    SendMsgScroll();
//
//
//                }
//                else
//                {
//                    // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 다르다면 왼쪽 뷰홀더 바인딩
//                    chatDataArrayList.add(new ChatData(strings[0],strings[1],strings[2],Integer.parseInt(strings[3]),strings[4],strings[5], strings[6], ChatViewType.left));
//                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
//                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
//                }
//            }
//            else
//            {
//                if(strings[0].equals(now_user_email)) {
//
//                    // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 같다면 오른쪽 뷰홀더 바인딩
//                    chatDataArrayList.add(new ChatData(strings[0],strings[1],strings[2],Integer.parseInt(strings[3]),strings[4],strings[5], strings[6], ChatViewType.right));
//                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
//                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
//
//                }
//                else
//                {
//                    // 현재 로그인한 사람과 서버에서 return된 메세지를 보낸 사람이 다르다면 왼쪽 뷰홀더 바인딩
//                    chatDataArrayList.add(new ChatData(strings[0],strings[1],strings[2],Integer.parseInt(strings[3]),strings[4],strings[5], strings[6], ChatViewType.left));
//                    // 리사이클러뷰 맨 마지막에 새로운 데이터 넣기
//                    chatAdapter.notifyItemInserted(chatDataArrayList.size());
//                }
//            }


        }
    }

    private void NotificationSomethings(String img, String title, String nick, String content, int roomnum) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, Chat_Activity.class);
        notificationIntent.putExtra("roomNum", roomnum); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) //BitMap 이미지 요구
                .setContentTitle(title)
                .setContentText(nick +"  :  " +content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 채팅 액티비티로 이동하도록 설정
                .setAutoCancel(true);




        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;

        // 경로는 userimg로 설정해줘야 함.

        if(img.equals("basic"))
        {
            img = "img.webp";
            url = "http://3.39.168.165/companyimg/" + img;
        }
        else{
            url = "http://3.39.168.165/companyimg/" + img;
        }

        //채팅방 사진으로 나와야 함.
        Glide.with(getApplicationContext()).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {



                builder.setLargeIcon(resource);
                notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }

    private void SendMsgScroll() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 4060);
            }
        });
    }


}