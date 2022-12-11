package com.example.ourtravel.diary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.Profile.ProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.CommentAdapter;
import com.example.ourtravel.Adapter.DiaryImageAdapter;
import com.example.ourtravel.RecyclerView.DiaryImageData;
import com.example.ourtravel.home.RcompanyActivity;
import com.example.ourtravel.retrofit.CommentData;
import com.example.ourtravel.retrofit.DiaryData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RdiaryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    // 다이어리 상세 보기 액티비티 구성하고 있는 컴포넌트 들
    private ImageView img; // 유저 프로필 사진
    private TextView tx_nick, tx_date, tx_content, tx_likecnt, tx_commentcnt;// 닉네임, 작성시간, 작성 내용, 좋아요 수, 댓글 수
    private TextView tx_toNick;// ...님에게 답글 남기는 중
    private ImageButton back_btn, btn_menu, btn_send; // 뒤로가기 버튼, 메뉴 버튼, 댓글 작성버튼
    // 유저 닉네임, 작성시간, 작성내용, 좋아요 갯수, 댓글 수
    private ToggleButton btn_like; // 좋아요 버튼
    private EditText et_comment; // 댓글 작성하는 부분
    private ScrollView scrollView;

    private InputMethodManager imm; // 입력받는 방법을 관리하는 manager 객체를 요청하여 inputmethodmanager에 반환하기 위함.


    int id; //게시글 id
    String nick; //작성자 닉네임
    String content; //작성 내용
    String date; // 작성 시간
    int like; //좋아요 개수
    int comment_cnt; //댓글 개수
    String profile; // 작성자 프로필
    String user_email; // 유저 이메일
    String message; // 좋아요 선택 여부 확인(처음 다이어리 데이터 세팅할때)
    boolean status; // 좋아요 클릭 여부 (유저가 해당 페이지에서 클릭할떄)
    String comment; // 댓글 변수
    String now_user_profile; // 현재 유저의 프로필

    boolean comment_status; // 댓글 좋아요 클릭 여부

    Dialog dialog01; // 메뉴버튼 선택시 나오게 되는 다이얼로그 선언

    //-------------------------------다중이미지 리사이클러뷰------------------------------
    RecyclerView recyclerView = null;
    // 이미지 셋팅해줄 리사이클러뷰
    DiaryImageAdapter diaryImageAdapter = null;
    // 다이어리 이미지 어댑터
    ArrayList<DiaryImageData> list;
    //-------------------------------다중이미지 리사이클러뷰------------------------------


    //-------------------------------댓글 리사이클러뷰------------------------------
    RecyclerView commentRecyclerView = null;
    // 댓글 세팅해줄 리사이클러뷰
    CommentAdapter commentAdapter = null;
    // 댓글 리사이클러뷰 어댑터
    ArrayList<CommentData> commentDataArrayList;
    // 댓글 데이터 담을 arraylist
    //-------------------------------댓글 리사이클러뷰------------------------------


    // 현재 로그인한 회원 정보 담고있는 shared 선언
    private PreferenceHelper preferenceHelper;
    String now_user_email; // 현재 로그인한 유저의 이메일


    // 레트로핏
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdiary);


        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail();

        // 다이얼로그 ( 게시글 수정, 삭제 )
        dialog01 = new Dialog(RdiaryActivity.this);
        dialog01.setContentView(R.layout.dialog_default);


        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        //-------------------------------다중이미지 리사이클러뷰------------------------------
        recyclerView = findViewById(R.id.photoRecyclerview);
        list = new ArrayList<>();
        diaryImageAdapter = new DiaryImageAdapter(list, getApplicationContext());
        recyclerView.setAdapter(diaryImageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //-------------------------------다중이미지 리사이클러뷰------------------------------

        //----------------------------------------------댓글 리사이클러뷰------------------------------
        commentRecyclerView = findViewById(R.id.rv_comment);
        commentDataArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentDataArrayList, getApplicationContext());
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //-------------------------------------------------댓글 리사이클러뷰------------------------------


        // imageview
        img = findViewById(R.id.img);

        // textview
        tx_nick = findViewById(R.id.tx_nick);
        tx_date = findViewById(R.id.tx_date);
        tx_content = findViewById(R.id.tx_content);
        tx_likecnt = findViewById(R.id.tx_likecnt);
        tx_commentcnt = findViewById(R.id.tx_commentcnt);
        tx_toNick = findViewById(R.id.tx_toNick);

        tx_toNick.setVisibility(View.GONE);

        // scrollView
        scrollView = findViewById(R.id.scrollView);

        // 입력받는 방법을 관리하는 Manager객체를  요청하여 InputMethodmanager에 반환한다.
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


//        // edittext
        et_comment = findViewById(R.id.et_comment); // 댓글 작성 edittext

        // 댓글 작성하려고 edittext 눌렀을 때 스크롤 밑으로 이동
        et_comment.setOnClickListener(new View.OnClickListener() {
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



        // toggle button
        btn_like = findViewById(R.id.btn_like);


        // imagebutton
        btn_send = findViewById(R.id.btn_send); // 댓글 작성 버튼
        // 메뉴 버튼
        btn_menu = findViewById(R.id.show_dialog_btn);
        // 뒤로 가기 버튼 클릭
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RdiaryActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 인텐트 선언부 (diary 어댑터에서 아이템 클릭시 아이템 데이터들 intent 로 보내는 것 받는 부분)
        Intent intent = getIntent();
        id = (int) intent.getExtras().get("id"); // 게시글 id

        nick = (String) intent.getExtras().get("nick"); // 게시글 작성자 닉네임
        tx_nick.setText(nick); // 닉네임 세팅

        user_email = (String) intent.getExtras().get("user_email"); // 작성한 유저의 이메일
        // 현재 로그인한 유저의 이메일과 일기를 작성한 유저의 이메일이 같을 시에 수정 삭제가 가능하도록 구현 해야 한다.
        // 같지 않으면 버튼 gone 상태로 같으면 visible 상태로 변환시킨다.
        if (!now_user_email.equals(user_email)) {
            btn_menu.setVisibility(btn_menu.GONE);
        } else {
            btn_menu.setVisibility(btn_menu.VISIBLE);
            btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog01();
                }
            });
        }

        // 작성한 유저의 프로필 세팅
        profile = getIntent().getStringExtra("profile");
        // 기본이미지 일때
        if (profile.equals("basic")) {
            img.setImageResource(R.drawable.profile2);
        } else {
            // 기본이미지가 아닐때 url로 이미지 세팅
            String url = "http://3.39.168.165/userimg/" + profile;
            Glide.with(this).load(url).into(img);
        }

        content = (String) intent.getExtras().get("content"); // 게시글 작성 내용
        tx_content.setText(content); // 작성 내용 세팅

        date = (String) intent.getExtras().get("date"); // 게시글 작성 시간
        tx_date.setText(date); // 작성 시간 세팅

        like = (int) intent.getExtras().get("like"); // 게시글 좋아요 개수
        tx_likecnt.setText(String.valueOf(like)); // 좋아요 개수 세팅

        comment_cnt = (int) intent.getExtras().get("comment"); // 게시글 댓글 개수
        tx_commentcnt.setText(String.valueOf(comment_cnt)); // 댓글 개수 세팅

        message = (String) intent.getExtras().get("message"); // 좋아요 클릭 여부

        // 좋아요 선택했다면 선택한 상태로 초기화 하고
        if (message.equals("true")) {
            btn_like.setChecked(true);
        } else // 좋아요 선택하지 않았다면 빈 좋아요 상태로 초기화 해라
        {
            btn_like.setChecked(false);
        }

        // 다이어리 이미지 리스트 리사이클러뷰
        // 다이어리 이미지 db에서 갖고 와서 리사이클러뷰에 뿌려줘야 함.
        retrofitInterface.setdiaryimg(id).enqueue(new Callback<List<DiaryData>>() {
            @Override
            public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DiaryData> result = response.body();
                    Log.e(TAG, "다중이미지 response body : " + result);

                    // 가져온 이미지 리스트 갯수만큼 for문 돌리기
                    for (int i = 0; i < result.size(); i++) {
                        DiaryData diaryData = result.get(i);
                        DiaryImageData diaryImageData = new DiaryImageData(diaryData);
                        // 이미지 리사이클러뷰에 이미지 리스트 담기
                        list.add(diaryImageData);

                    }

                    diaryImageAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onResponse: fail");
                }
            }

            @Override
            public void onFailure(Call<List<DiaryData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

        // 일기 작성한 유저 프로필 사진 클릭시 해당 유저의 프로필로 이동하기
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 선택한 프로필 사진이 자신의 프로필 사진이라면 자신의 프로필로 이동
                if(user_email.equals(now_user_email))
                {
                    Intent intent = new Intent(RdiaryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                // 다른 사람의 프로필 사진을 선택했다면 그 사람의 프로필로 이동동
               else{
                    Intent intent = new Intent(RdiaryActivity.this, OtherUserProfileActivity.class);
                    intent.putExtra("user_email", user_email);
                    startActivity(intent);
                    finish();
                }

            }
        });

        // 댓글 리스트 불러오는 서버 통신
        retrofitInterface.commentlist(id, now_user_email).enqueue(new Callback<List<CommentData>>() {
            @Override
            public void onResponse(Call<List<CommentData>> call, Response<List<CommentData>> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    Log.e(TAG, "onResponse: 댓글2 :"+ response.body() );
                    List<CommentData> result = response.body();

                    for(int i=0; i<result.size(); i++)
                    {
                        commentDataArrayList.add(result.get(i));

                    }

                    commentAdapter.notifyDataSetChanged();
                }
                else
                {
                    Log.e(TAG, "onResponse: 댓글 response fail" );
                }
            }

            @Override
            public void onFailure(Call<List<CommentData>> call, Throwable t) {
                Log.e("onresponse", "댓글 리스트 불러오기 에러 : " + t.getMessage());
            }
        });

        // 댓글 리스트에서 좋아요, 답글 달기를 클릭했을때 클릭 인터페이스 정의 해줘야 함.
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {

            @Override
            public void onImgClick(View v, int pos) {

                String user_email = commentDataArrayList.get(pos).getUser_email(); // 댓글 작성한 사람

                // 자신의 프로필 사진을 클릭했을 때
                if(user_email.equals(now_user_email))
                {
                    Intent intent = new Intent(RdiaryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                //다른 사람의 프로필 사진을 선택했을 떄
                else{

                    Intent intent = new Intent(RdiaryActivity.this, OtherUserProfileActivity.class);
                    intent.putExtra("user_email", user_email);
                    startActivity(intent);

                }
            }

            @Override
            public void onLikeClick(View v, int pos) {
                Log.e(TAG, "onLikeClick: 좋아요 클릭" );
                // 좋아요 선택했을 때 버튼 상태변화가 되어야 하며, 좋아요 갯수가 늘어나야 한다.
                if(commentDataArrayList.get(pos).getMessage().equals("true"))
                {
                    comment_status = false;
                    Log.e(TAG, "onLikeClick: 좋아요 선택되어 있음" );
                    // 좋아요 선택되어 있는데 좋아요 한번 더 누른 상태
                    int like_cnt = commentDataArrayList.get(pos).getFavorite();
                    like_cnt--;
                    commentDataArrayList.get(pos).setFavorite(like_cnt);
                    commentDataArrayList.get(pos).setMessage("false");
                    commentAdapter.notifyDataSetChanged();

                    int comment_id = commentDataArrayList.get(pos).getComment_id();
                    // 좋아요 갯수 하나 줄어들었으니 db에 저장해야 한다.
                    retrofitInterface.clickcommentlike(now_user_email, comment_id, comment_status).enqueue(new Callback<CommentData>() {
                        @Override
                        public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                            if(response.isSuccessful() && response.body() != null)
                            {
                                CommentData result = response.body();
                                Log.e(TAG, "onResponse: 채워진 좋아요 선택 result : " + result );
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 채워진 좋아요 선택 fail" );
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentData> call, Throwable t) {
                            Log.e("onresponse", "에러 다이어리 좋아요 해제 : " + t.getMessage());
                        }
                    });

                }
                else
                {
                    Log.e(TAG, "onLikeClick: 좋아요 선택되어 있지 않음" );
                    // 좋아요 선택되어 있지 않은데 좋아요 누른 상태
                    // 좋아요 갯수가 늘어나야 하고 버튼의 상태가 true로 되어야 한다.
                    comment_status = true;
                    int comment_id = commentDataArrayList.get(pos).getComment_id(); // 유저가 좋아요 누른 댓글의 id 가져오기
                    Log.e(TAG, "onLikeClick: comment_id :" + comment_id );

                    // 서버 연결
                    retrofitInterface.clickcommentlike(now_user_email, comment_id, comment_status).enqueue(new Callback<CommentData>() {
                        @Override
                        public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                                if(response.isSuccessful() && response.body() != null){
                                    CommentData result = response.body();
                                    Log.e(TAG, "onResponse: 빈 좋아요 선택 result :"+ result );
                                    int like_cnt = commentDataArrayList.get(pos).getFavorite();
                                    like_cnt++;

                                    commentDataArrayList.get(pos).setFavorite(like_cnt);
                                    commentDataArrayList.get(pos).setMessage("true");
                                    commentAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    Log.e(TAG, "onResponse: 빈 좋아요 선택 fail" );
                                }
                        }

                        @Override
                        public void onFailure(Call<CommentData> call, Throwable t) {
                            Log.e("onresponse", "에러 다이어리 좋아요 해제 : " + t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onReplyClick(View v, int pos) {

                et_comment.requestFocus();
                imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);

                float y = commentRecyclerView.getY() + commentRecyclerView.getChildAt(pos).getY()*2;
                Log.e(TAG, "onReplyClick: commentRecyclerView.getY() :"+ commentRecyclerView.getY() );
                Log.e(TAG, "onReplyClick:commentRecyclerView.getChildAt(pos + 1).getY() :" + commentRecyclerView.getChildAt(pos ).getY() );

                scrollView.scrollTo(0, (int)y);

                // ...님에게 댓글 남기는 중 보이게 설정하기.
                tx_toNick.setVisibility(View.VISIBLE);
                String toNick = "  "+commentDataArrayList.get(pos).getNick()+"님에게 답글 남기는 중...";
                tx_toNick.setText(toNick);

                // 대댓글 남길때 버튼 클릭
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 대댓글 남겼을 때 db 저장.
                        // 대댓글 내용, 작성시간, 일기 id, 작성한 유저 이메일
                        String recomment = et_comment.getText().toString(); // 작성한 내용
                        long now = System.currentTimeMillis(); // 작성한 시간
                        int top = commentDataArrayList.get(pos).getTop(); // 해당 댓글의 top
                        tx_toNick.setVisibility(View.GONE);

                        Log.e(TAG, "onReplyClick: recomet :" + recomment );
                        Log.e(TAG, "onClick: top : " + top );

                        // 대댓글 작성 매서드
                        sendrecomment(recomment, now, top, pos);
                    }
                });


            }

            // 댓글, 대댓글 메뉴 클릭했을 때
            @Override
            public void onMenuClick(View v, int pos) {

                Log.e(TAG, "onMenuClick: 댓글 메뉴 클릭" );
                String content = commentDataArrayList.get(pos).getContent(); // 댓글 내용



                // 댓글 edittext 에 포커싱 주기
                et_comment.requestFocus();

                // 댓글, 대댓글 메뉴 다이얼로그 만들기
                AlertDialog.Builder dig = new AlertDialog.Builder(RdiaryActivity.this);

                // 댓글, 대댓글 수정하기.
                dig.setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // 유저가 작성한 내용 세팅
                       et_comment.setText(content);
                       // 키보드 올려서 유저가 작성한 댓글 내용 edittext에 세팅되도록 함.
                       imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);

                       btn_send.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               // 일단 시간은 수정 X, 내용만 수정되게 진행
                               // 댓글 id와 수정된 댓글 내용만 보내면 됨.

                               String updateReply = et_comment.getText().toString(); // 수정한 댓글 내용
                               int comment_id = commentDataArrayList.get(pos).getComment_id();
                               Log.e(TAG, "onClick: updateReply :"+updateReply );
                               Log.e(TAG, "onClick: 수정 댓글 id :" + id );
                               // 댓글 수정하기 위한 서버 통신
                               retrofitInterface.updatecomment(updateReply, comment_id).enqueue(new Callback<CommentData>() {
                                   @Override
                                   public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                                       if(response.isSuccessful() && response.body() != null)
                                       {
                                           CommentData result = response.body();
                                           Log.e(TAG, "onResponse: 댓글 수정 response result :"+ result );

                                           // 수정이 되었다면 status 값이 true 가 된다.
                                           String status = result.getStatus();
                                           if(status.equals("true")){
                                               // 수정 되었다는 알람창
                                               AlertDialog.Builder updatecheck = new AlertDialog.Builder(RdiaryActivity.this);
                                               updatecheck.setMessage("댓글이 수정되었습니다.");
                                               updatecheck.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialogInterface, int i) {

                                                       // 키보드 내려가는거 확인.
                                                       imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                                       // edittext 공백으로 처리
                                                       et_comment.setText("");
                                                       // 리사이클러뷰 갱신해줘야 함.
                                                       commentDataArrayList.get(pos).setContent(updateReply);
                                                       commentAdapter.notifyDataSetChanged();
                                                   }
                                               });
                                               updatecheck.show();
                                           }

                                           // 댓글이 수정되었다는 알람창과 함께 키보드가 내려가야 한다.
                                       }
                                       else {
                                           Log.e(TAG, "onResponse: 댓글 수정 response fail" );
                                       }
                                   }

                                   @Override
                                   public void onFailure(Call<CommentData> call, Throwable t) {
                                       Log.e("onresponse", "에러 다이어리 좋아요 해제 : " + t.getMessage());
                                   }
                               });
                           }
                       });

                    }
                });

                // 댓글, 대댓글 삭제하기.
                dig.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 대댓글이 삭제될 때 부모 댓글의 child가 1씩 감소해야 됨.
                        // 부모댓글의 child값이 0이 아니라면 원댓글 삭제되면 안된다.
                        // 부모댓글의 child 값이 0이라면 원댓글 삭제가능
                        // 대댓글은 삭제 가능.

                        // 서버 통신 할 때 댓글의 top 과 level 값을 서버로 보내면 된다.
                        // 값 비교 하면 됨.
                        // level이 0이라면 부모댓글이라는 뜻, 0이 아니라면 자식댓글이라는 뜻
                        // 서버로 보낼 값들 = 댓글의 id, 댓글의 level

                        // 리사이클러뷰 상에서 삭제 되

                        int level = commentDataArrayList.get(pos).getLevel(); // 댓글의 level
                        int comment_id = commentDataArrayList.get(pos).getComment_id(); // 댓글의 id
                        int child = commentDataArrayList.get(pos).getChild();// 댓글의 child
                        int diary_id = commentDataArrayList.get(pos).getId(); // 댓글이 달린 다이어리의 id
                        int top = commentDataArrayList.get(pos).getTop(); // 댓글의 top

                        Log.e(TAG, "onClick: comment_id :"+comment_id );

                        // 댓글 삭제하기 위한 서버 통신.
                        retrofitInterface.deletecomment(level, comment_id, child, diary_id, top).enqueue(new Callback<CommentData>() {
                            @Override
                            public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                                    if(response.isSuccessful() && response.body() != null)
                                    {
                                        CommentData result = response.body();
                                        Log.e(TAG, "onResponse: 댓글 삭제 response result : " + result );

                                        String status = result.getStatus();
                                        // 삭제 후 변화 된 댓글 수
                                        int comment_cnt = result.getCnt();
                                        int child = result.getChild();
                                        int viewType = result.getViewType();
                                        int del = result.getDel();

                                        if(status.equals("true"))
                                        {
                                            // 유저가 댓글을 삭제한다고 한다면 정말 댓글을 삭제할 건지 확인 메세지가 나오게 한다.

                                            AlertDialog.Builder deletecheck = new AlertDialog.Builder(RdiaryActivity.this);
                                            deletecheck.setMessage("댓글을 삭제하시겠습니까?");
                                            deletecheck.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    // 댓글이 삭제된 후 변경된 댓글 수 세팅
                                                    tx_commentcnt.setText(String.valueOf(comment_cnt));

                                                    // 자식 댓글이 없는 경우 child 가 0일 경우에 바로 삭제
                                                    // 댓글 리사이클러뷰 아이템에서 해당 포지션에 맞는 댓글 아이템 삭제해주고
                                                    // 리사이클러뷰 업데이트 해줌.

                                                    // 삭제된 이력이 있고 자식 댓글이 있다면 삭제된 댓글 입니다.
                                                    // 그렇지 않다면 바로 삭제
                                                    Log.e(TAG, "onClick: child :" + child );

                                                    // 한번 더 서버통신을 해야할 거 같다... 자꾸만 오류가 너무 난다.
                                                    // 자식이 있는지 없는지
                                                    // del이 1인 상태에서 자식이 있는지 없는지 확인해야 한다.

                                                    if(child == 0)
                                                    {
                                                        commentDataArrayList.remove(pos);
                                                        commentAdapter.notifyDataSetChanged();

                                                        if(del == 1)
                                                        {
                                                            commentDataArrayList.remove(pos-1);
                                                            commentAdapter.notifyDataSetChanged();
                                                        }
                                                        // 자식 댓글이 삭제되는 상황에서 자식댓글의 level이 1이고
                                                        // 부모 댓글의 자식이 0이 되는 순간 부모 댓글 리사이클러뷰에서 삭제되어야 한다.
                                                        // 그리고 해당 부모댓글 삭제되어야 함.

                                                    }
                                                    else
                                                    {
                                                        // 실시간으로 대댓글 생기고 자식댓글부터 삭제하고 제일 마지막으로 부모댓글 삭제할 때 child가 줄어들지 않아서
                                                        // 뷰 홀더 타입에서 자꾸 에러가 남..
                                                        // 부모 댓글 삭제에서 문제가 생기고 있는 상황임.

                                                        retrofitInterface.deletestatus(level,comment_id,child,diary_id,top).enqueue(new Callback<CommentData>() {
                                                            @Override
                                                            public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                                                                if(response.isSuccessful() && response.body() != null)
                                                                {
                                                                    CommentData result = response.body();
                                                                    String status = result.getStatus();
                                                                    int comment_cnt = result.getCnt();

                                                                    if(status.equals("true"))
                                                                    {
                                                                        // 자식댓글이 있다는 뜻
                                                                        Log.e(TAG, "onClick: child > 0 : 1" );
                                                                        Log.e(TAG, "onClick: viewType : " + viewType );
                                                                        commentDataArrayList.get(pos).setViewType(viewType);
                                                                        Log.e(TAG, "onClick: child > 0 : 2" );
                                                                        commentAdapter.notifyDataSetChanged();
                                                                        Log.e(TAG, "onClick: child > 0 : 3" );
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.e(TAG, "onResponse: no child" );
                                                                        commentDataArrayList.remove(pos);
                                                                        commentAdapter.notifyDataSetChanged();

                                                                        // 댓글이 삭제된 후 변경된 댓글 수 세팅
                                                                        tx_commentcnt.setText(String.valueOf(comment_cnt));
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Log.e(TAG, "onResponse: 댓글 삭제 상태 확인 response fail" );
                                                                }

                                                            }

                                                            @Override
                                                            public void onFailure(Call<CommentData> call, Throwable t) {
                                                                Log.e("onresponse", "에러 댓글 삭제 상태 확인 : " + t.getMessage());
                                                            }
                                                        });

                                                    }

                                                }
                                            });
                                            deletecheck.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });

                                            deletecheck.show();
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "onResponse: 댓글 삭제 response fail" );
                                    }
                            }

                            @Override
                            public void onFailure(Call<CommentData> call, Throwable t) {
                                Log.e("onresponse", "에러 댓글 삭제 : " + t.getMessage());
                            }
                        });

                    }
                });

                // 다이얼로그 취소 버튼 클릭
                dig.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                dig.show();


            }
        });


        // 좋아요 선택하는 부분
        // 좋아요 하트를 선택했다면.
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "좋아요 onClick: message : " + message);
                if (message.equals("true")) {
                    // 이미 좋아요가 눌러져 있는 상태에 클릭하게 되면 좋아요 수가 줄어들어야 한다.
                    // btn_like.setChecked(false); // 좋아요를 해제한거니까 빈 하트로 변해야 한다.
                    like--; // 좋아요 수 줄어들어야 한다.
                    Log.e(TAG, "onClick: like 수 :" + like);
                    tx_likecnt.setText(String.valueOf(like)); // 좋아요수 세팅해줘야 함.
                    status = false; // 좋아요 해제한 상태
                    retrofitInterface.clicklike(like, id, now_user_email, status).enqueue(new Callback<DiaryData>() {
                        @Override
                        public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                DiaryData result = response.body();
                                String status = result.getStatus();

                                if (status.equals("true")) {
                                    // 좋아요 삭제 됐다는 얘기니까 좋아요 빈하트로 변경하기
                                    message = "false";

                                } else {
                                    Log.e(TAG, "onResponse: 좋아요 해제 status is false ");
                                }
                            } else {
                                Log.e(TAG, "onResponse: 좋아요 해제 response fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<DiaryData> call, Throwable t) {
                            Log.e("onresponse", "에러 다이어리 좋아요 해제 : " + t.getMessage());
                        }
                    });


                } else {
                    // 좋아요가 눌러져 있지 않은 상태에서 클릭하게 되면 좋아요 수가 증가하게 된다.
                    //btn_like.setChecked(true); // 좋아요를 선택한거니까 채워진 하트로 변해야한다.
                    like++; // 좋아요 수가 증가해야 한다.
                    tx_likecnt.setText(String.valueOf(like)); // 좋아요수 세팅해줘야 함.
                    Log.e(TAG, "onClick: like 수 :" + like);
                    status = true; // 좋아요를 선택한 상태

                    retrofitInterface.clicklike(like, id, now_user_email, status).enqueue(new Callback<DiaryData>() {
                        @Override
                        public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                DiaryData result = response.body();
                                String status = result.getStatus();

                                if (status.equals("true")) {
                                    // 좋아요 삭제 됐다는 얘기니까 좋아요 빈하트로 변경하기
                                    message = "true";

                                } else {
                                    Log.e(TAG, "onResponse: 좋아요 선택 status is false ");
                                }
                            } else {
                                Log.e(TAG, "onResponse: 좋아요 선택 response fail");
                            }
                        }

                        @Override
                        public void onFailure(Call<DiaryData> call, Throwable t) {
                            Log.e("onresponse", "에러 다이어리 좋아요 선택 : " + t.getMessage());
                        }
                    });
                }
            }
        });

        // 댓글 작성하는 부분
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment = et_comment.getText().toString(); // 작성한 내용
                long now = System.currentTimeMillis(); // 작성한 시간

                sendcomment(comment, now);
            }
        });

        // 현재 작성하고 있는 유저의 프로필을 가져와야 함.
        retrofitInterface.nowuser(now_user_email).enqueue(new Callback<DiaryData>() {
            @Override
            public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                if(response.isSuccessful() && response.body() !=null)
                {
                    DiaryData result= response.body();
                    Log.e(TAG, "onResponse: 유저 프로필 불러오기 result :" + result );
                    now_user_profile = result.getProfile();
                }
                else
                {
                    Log.e(TAG, "onResponse: 유저 프로필 불러오기 fail" );
                }
            }

            @Override
            public void onFailure(Call<DiaryData> call, Throwable t) {
                Log.e("onresponse", "에러 다이어리 유저 프로필 불러오기 : " + t.getMessage());
            }
        });


    }


    // 대댓글 남기기 메서드
    public void sendrecomment(String comment, long now, int top, int pos){

        retrofitInterface.sendrecomment(comment, now, id, now_user_email, top).enqueue(new Callback<CommentData>() {
            @Override
            public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    CommentData result = response.body();
                    int repos =  result.getLevel() - 1 ;
                    Log.e(TAG, "onResponse: pos :"+ pos );
                    Log.e(TAG, "onResponse: level :"+ result.getLevel() );
                    Log.e(TAG, "onResponse: repos :"+ repos );
                    commentDataArrayList.add(repos, result);
                    Log.e(TAG, "onResponse: 대댓글 result :" + result );

                    commentDataArrayList.get(pos).setChild(result.getChild());

                    // 리사이클러뷰 아이템에 어떤 데이터를 어떻게 넣을지 공부
                    commentAdapter.notifyDataSetChanged();
                    int comment_cnt = result.getCnt();
                    Log.e(TAG, "onResponse: comment_cnt:" + comment_cnt );
                    tx_commentcnt.setText(String.valueOf(comment_cnt));

                    et_comment.setText("");
                }
                else
                {
                    Log.e(TAG, "대댓글 onResponse: fail");
                }
            }

            @Override
            public void onFailure(Call<CommentData> call, Throwable t) {
                Log.e("onresponse", "에러 댓글 입력 : " + t.getMessage());
            }
        });
    }

    // 댓글 남기기 메서드
    public void sendcomment(String comment, long now){

        retrofitInterface.sendcomment(comment, now, id, now_user_email).enqueue(new Callback<CommentData>() {
            @Override
            public void onResponse(Call<CommentData> call, Response<CommentData> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    CommentData result = response.body();
                    Log.e(TAG, "onResponse: 댓글 :" + result );
                    commentDataArrayList.add(0, result);


                    // 리사이클러뷰 아이템에 어떤 데이터를 어떻게 넣을지 공부
                    commentAdapter.notifyDataSetChanged();
                    int comment_cnt = result.getCnt();
                    Log.e(TAG, "onResponse: comment_cnt:" + comment_cnt );
                    tx_commentcnt.setText(String.valueOf(comment_cnt));

                    et_comment.setText("");
                }
                else
                {
                    Log.e(TAG, "onResponse: 댓글 status is faile" );
                }
            }

            @Override
            public void onFailure(Call<CommentData> call, Throwable t) {
                Log.e("onresponse", "에러 댓글 입력 : " + t.getMessage());
            }
        });

    }


    // 다이어리 게시글 메뉴 메서드
    public void showDialog01() {
        // 다이얼로그 보여주기
        dialog01.show();

        // 수정하기 버튼
        Button button_modify = dialog01.findViewById(R.id.btn_modify);
        button_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: modify");
                // 다이얼로그를 꼭 dismiss 해줘야 액티비티 실행됨. dismiss하지 않은상태에서 액티비티 이동 X
                dialog01.dismiss();
                Intent intent = new Intent(RdiaryActivity.this, UdiaryActivity.class);
                intent.putExtra("id", id); // 게시글 id를 인텐트로 전달 시킨다.
                startActivity(intent);
                finish();
            }

        });

        // 삭제하기 버튼
        Button button_delete = dialog01.findViewById(R.id.btn_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: delete ");

                dialog01.dismiss();
                // 제일 밑에 있던 다이얼로그 사라지고 새로운 다이얼로그 생성
                // 정말 삭제할 것이지 확인하는 확인 다이얼로그임.
                AlertDialog.Builder builder = new AlertDialog.Builder(RdiaryActivity.this);
                builder.setMessage("정말 삭제하시겠습니까?");
                // 예 버튼 클릭시 db에서 해당 게시글의 record가 delete됨.
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Log.e(TAG, "onClick: id : " + id);

                        // db에서 게시글 record 지우기 위한 레트로핏 통신
                        retrofitInterface.deletediary(id).enqueue(new Callback<DiaryData>() {
                            @Override
                            public void onResponse(Call<DiaryData> call, Response<DiaryData> response) {
                                if (response.isSuccessful()) {
                                    DiaryData result = response.body();
                                    String status = result.getStatus();
                                    String message = result.getMessage();

                                    if (status.equals("true")) {
                                        Intent intent = new Intent(RdiaryActivity.this, DiaryActivity.class);
                                        Log.e(TAG, "onResponse: message : " + message);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(TAG, "onResponse: status is false");
                                        Log.e(TAG, "onResponse: message " + message);
                                    }
                                } else {
                                    Log.e(TAG, "onResponse: false");
                                }
                            }

                            @Override
                            public void onFailure(Call<DiaryData> call, Throwable t) {

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