package com.example.ourtravel.Profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Chat.Chat_Main_Activity;
import com.example.ourtravel.None.NoneCompany;
import com.example.ourtravel.None.NoneDiary;
import com.example.ourtravel.Fragment.OtherUserCompany;
import com.example.ourtravel.Fragment.OtherUserDiary;
import com.example.ourtravel.None.NoneOtherCompany;
import com.example.ourtravel.None.NoneOtherDiary;
import com.example.ourtravel.R;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.diary.RdiaryActivity;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.DiaryData;
import com.example.ourtravel.retrofit.FriendData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.retrofit.UserData;
import com.example.ourtravel.setting.SettingActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserProfileActivity extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;
    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String now_user_email; // 현재 로그인한 유저 이메일
    String user_nick; // 프로필 유저 닉네임
    String user_email; // 프로필 유저 이메일

    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;

    Dialog dialog01; // 프로필 메뉴 선택시 나오는 다이얼로그
    Dialog dialogreport; // 신고 선택시 나오는 다이얼로그

    private TextView tx_nick, tx_produce, tx_company, tx_diary, tx_phone, tx_rate, tx_profiletitle;
    private ToggleButton btn_friend;
    private ImageButton btn_menu;
    private Button view_company, view_diary, btn_score;
    private ImageView profile_img, img_check,img_announce;
    private ImageButton btn_home, btn_diary, btn_chat, btn_setting ;

    RatingBar mediumRatingBar; // 동행 점수 남기기 rating bar

    public ArrayList<Integer> room_array;

    Dialog dialog_ask; // 서로이웃 이미 요청한 상태에서 나오는 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);


        //text view
        tx_nick = findViewById(R.id.tx_nick); // 유저 닉네임
        tx_produce = findViewById(R.id.tx_produce); // 유저 자기소개
        tx_company = findViewById(R.id.tx_company); // 동행글
        tx_diary = findViewById(R.id.tx_diary); // 일기
        tx_phone = findViewById(R.id.tx_phone); // 휴대폰 인증
        tx_rate = findViewById(R.id.tx_rate); // 동행 점수
        tx_profiletitle = findViewById(R.id.tx_profiletitle);


        //ImageButton - 밑 메뉴 이동 버튼들

        btn_friend = findViewById(R.id.btn_friend); // 친구 신청 버튼
        btn_menu = findViewById(R.id.btn_menu); // 프로필 메뉴 버튼

        //ImageButton - 밑 메뉴 이동 버튼들
        btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_diary = (ImageButton) findViewById(R.id.btn_diary);
        btn_chat = (ImageButton) findViewById(R.id.btn_chat);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);

        //imageview
        profile_img = findViewById(R.id.profile_img); // 유저 프로필 사진
        img_check = findViewById(R.id.img_check);
        img_announce = findViewById(R.id.img_announce); // 동행 점수 설명해주는 이모지

        //button
        view_company = findViewById(R.id.view_company); // 동행글 선택시 표시 선
        view_diary = findViewById(R.id.view_diary); // 일기 선택시 표시 선
        btn_score = findViewById(R.id.btn_score); // 동행점수 남기기 버튼


        mediumRatingBar = findViewById(R.id.mediumRatingBar); // 동행 점수 남기기 rating bar
        mediumRatingBar.setRating(Float.parseFloat("2.5"));
        // 동행 점수가 셋팅이 되어있어야 함.
        // 처음 동행 점수는 5점의 반인 2.5 점 부터 시작한다.
        // 동행 점수가 더해지면 더해질수록 점수가 바뀐다.

        // 동행 점수 남기기 버튼을 누르고 동행 점수를 입력한후 입력한 점수와 기존에 있던 점수들을 합쳐 평균낸 값으로
        // 동행 점수 세팅을 해줘야 함.
        // 동행 점수 평균은 반올림해서 소수점 첫째자리까지 표현해주기


        //처음 셋팅은 동행글 선택
        //text밑에 표시
        view_company.setVisibility(View.VISIBLE);
        view_diary.setVisibility(View.INVISIBLE);

        // 프로필 메뉴 다이얼로그
        dialog01 = new Dialog(OtherUserProfileActivity.this);
        dialog01.setContentView(R.layout.dialog_profilemenu);

        // 신고하기 선택시 다이얼로그
        dialogreport = new Dialog(OtherUserProfileActivity.this);
        dialogreport.setContentView(R.layout.dialog_report);

        // 서로이웃 이미 요청한 상태에서 나오는 다이얼로그
        dialog_ask = new Dialog(OtherUserProfileActivity.this);
        dialog_ask.setContentView(R.layout.dialog_askfriend);

        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저 이메일

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 인텐트 선언부
        Intent intent = getIntent();

        // 프로필 유저 이메일 받아오기
        user_email = (String) intent.getExtras().get("user_email");

        // 유저 정보 세팅하기
        retrofitInterface.UpUserInfo(user_email).enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                if (response.isSuccessful()) {
                    UserData result = response.body();

                    String status = result.getStatus();
                    String nick = result.getUser_nick();
                    String produce = result.getUser_produce();
                    String photo = result.getPhoto();
                    String phone = result.getUser_phone();
                    float rate = result.getScore();

                    if (status.equals("true")) {

                        // 유저 닉네임, 자기소개 세팅하기
                        tx_profiletitle.setText(nick + " 님의 프로필");
                        tx_nick.setText(nick);
                        user_nick = tx_nick.getText().toString();
                        tx_produce.setText(produce);
                        tx_rate.setText(String.valueOf(rate) + " 점");

                        // 유저 프로필 셋팅하기
                        if (photo.equals("basic")) // 프로필 기본일때
                        {
                            profile_img.setImageResource(R.drawable.profile2);
                        } else { // 프로필 기본이미지 아닐때

                            String url = "http://3.39.168.165/userimg/" + photo;
                            //Log.e(TAG, "onResponse: url : " + url );
                            Glide.with(OtherUserProfileActivity.this).load(url).into(profile_img);
                        }

                        // 유저 휴대번호 인증 유무 확인하기
                        if (phone.equals("0")) {
                            // 인증하지 않았다는 뜻
                            // 전화번호 인증을 하지 않았다는 것.
                            // 전화번호 인증이 안되있다는 text set을 해줘야 함.
                            tx_phone.setVisibility(tx_phone.GONE);
                            img_check.setVisibility(img_check.GONE);
                        } else {
                            // 인증했다는 뜻
                            tx_phone.setVisibility(tx_phone.VISIBLE);
                            img_check.setVisibility(img_check.VISIBLE);


                        }


                    } else {
                        Log.e(TAG, "fail");
                    }
                } else {
                    Log.e(TAG, "onResponse fail");
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

        // 다른 사람의 프로필에서 이 유저와 내가 동행을 한 이력이 있는지 없는지 확인해야 한다.
        //1. 단순히 동행을 했는지 안했는지
        //2. 동행 점수를 남겼는지 안남겼는지

//                이사람과 동행을 같이 했는데 동행점수를 남겼는지 안남겼는지 확인하고
//
//                동행을 같이 했는데 동행점수를 안남겼다면 점수 남기기 버튼 생성
//
//                동행을 같이 했는데 동행점수를 이미 남겼다면 점수 남기기 버튼 없어지기
//
//                애초에 동행을 한적이 없을땐 점수 남기기 버튼 없어지기

        // 동행이 완료되었는지 확인 먼저해야 할 거 같음.
        // 현재 이 유저와 같이 동행을 했는지 확인
        //
        // 이 동행이 끝났는지 확인
        //

        Log.e(TAG, "onCreate: my_email = " + now_user_email ); //현재 로그인한 유저
        Log.e(TAG, "onCreate: other_email =" + user_email ); // 프로필 유저
        retrofitInterface.CheckCompanyFinish(now_user_email,user_email).enqueue(new Callback<CompanyData>() {
            @Override
            public void onResponse(Call<CompanyData> call, Response<CompanyData> response) {

                CompanyData result = response.body();
                Log.e(TAG, "onResponse: 유저가 참여했고 동행이 종료된 동행글 list = " + result );

                room_array = result.getRoom_array();
                Log.e(TAG, "onResponse: room_array = " + room_array );

                // room_array가 있다는 것은 현재 유저가 프로필 유저와 같이 동행한적이 있다는 것이고 그 동행은 종료되었다는 것.
                // 만약 array가 비어있다면 동행한 적도 없다는 것 or 동행은 같이 하지만 동행이 아직 종료 되지 않았다는 것.

                // 추가한 사항
                // 현재 로그인한 유저가 프로필 유저에게 동행 점수를 남기지 않았다면 버튼 생기고
                // 이미 동행 점수를 남긴 상황이라면 버튼 생성되게 함함

               if(room_array.size() == 0)
                {
                    Log.e(TAG, "onResponse: room_array size == 0" );
                    // 동행 점수 남기기 버튼 감추기
                    btn_score.setVisibility(btn_score.GONE);
                }
                else{
                    Log.e(TAG, "onResponse: room_array size != 0" );
                    // 동행 점수 남기기 버튼 생기게 하기

                }

            }

            @Override
            public void onFailure(Call<CompanyData> call, Throwable t) {

            }
        });




        // 현재 프로필 유저와 동행한 이력이 있는지 확인을 한 후 버튼의 생성 유무를 결정 해야 한다.

        img_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dig_announce = new AlertDialog.Builder(OtherUserProfileActivity.this);
                dig_announce.setTitle("동행 점수");
                dig_announce.setMessage("동행 일정이 끝난 후 남긴 평가를 합산해\n동행 점수가 계산됩니다.\n기본 동행점수는 2.5점 입니다.");
                dig_announce.show();
            }
        });


        // 동행 점수 남기기 버튼 클릭 이벤트
        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: 동행 점수 남기기 버튼 클릭" );

                // 동행 점수 남기는 액티비티로 이동시키기
                Log.e(TAG, "onClick: 채팅방 array = " + room_array );
                // 해당 array를 intent로 동행 점수 남기는 액티비티로 넘겨야 한다.

                Intent arrayintent = new Intent(OtherUserProfileActivity.this, CompanyScoreActivity.class);
                arrayintent.putExtra("room_array", room_array); // 채팅방 array 보내고
                arrayintent.putExtra("user_email", user_email); // 프로필 유저의 이메일도 보내고고
                startActivity(arrayintent);
            }
        });


        // 처음 동행글이 선택되서 동행글 프레그먼트
        FragmentView(Fragment_1);

        // 일기 선택시 일기 프레그먼트만 보이게끔
        tx_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_1);
                view_company.setVisibility(View.VISIBLE);
                view_diary.setVisibility(View.INVISIBLE);


            }
        });

        // 동행글 선택시 동행글 프레그먼트
        tx_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_2);
                view_diary.setVisibility(View.VISIBLE);
                view_company.setVisibility(View.INVISIBLE);
            }
        });

        // 다이어리 이동 버튼
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, DiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 채팅 이동 버튼
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, Chat_Main_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // 설정 버튼
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //홈 버튼
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 프로필 메뉴 선택시 차단하기 다이얼로그 나와야 함.
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
            }
        });

        // 친구신청 버튼 클릭
        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 친구 신청 버튼이 클릭상태 파란색 - 이미 친구 요청을 보냈다는 것.
                // 친구 신청 버튼이 클릭되지 않은 상태 검은색 - 친구 요청을 보내지 않았다는 것.

                // 클릭했을 때 단순 이웃인 상태인지 서로 이웃 신청을 보낸 상태인지 확인해야 한다.
                // 이웃 이면 이웃 취소 할 건지
                // 1. 이웃 신청도 , 서로이웃 신청도 안한 상태인지
                // 2. 이웃 신청만 한 상태인지
                // 3. 서로이웃 신청을 한 상태인지

                retrofitInterface.CheckApply(now_user_email, user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            FriendData result = response.body();
                            Log.e(TAG, "친구 신청 전적 확인 onResponse: result : " + result);

                            String status = result.getStatus();

                            // 현재 로그인한 유저가 해당 프로필의 유저에게 서로 이웃 신청을 한 전적이 있다면
                            if (status.equals("true")) {
                                // 이미 서로 이웃 신청을 요청했으니
                                // 친구신청 취소하겠냐는 다이얼로그 띄우기

                                showCancleFriendDialog();
                            } else if (status.equals("just neighbor")) {
                                Log.e(TAG, "onResponse: 서로 그냥 이웃");
                            } else if (status.equals("bf")) {
                                Log.e(TAG, "onResponse: 서로 서로이웃");
                            }
                            // 서로 단순이웃도 서로이웃도 아니라는 뜻
                            else {
                                // 친구신청 하겠냐는 다이얼로그 띄우기

                                // 신청 보내려는 유저의 이메일 갖고 넘어가야 함.
                                Intent intent = new Intent(OtherUserProfileActivity.this, ApplyFriendActivity.class);
                                intent.putExtra("user_email", user_email);
                                startActivity(intent);
                                finish();

                            }
                        } else {
                            Log.e(TAG, "친구 신청 전적 확인 onResponse is fail ");
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {

                    }
                });

            }
        });


    }

    // 서로 이웃 신청 이미 했다는 다이얼로그
    private void showCancleFriendDialog() {

        dialog_ask.show();

        Button btn_bfcancle = dialog_ask.findViewById(R.id.btn_bfcancle); // 서로이웃 취소
        btn_bfcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofitInterface.CancleFriend(now_user_email, user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            FriendData result = response.body();
                            Log.e(TAG, "onResponse: 서로이웃 신청 취소 result = " + result);

                            String status = result.getStatus();
                            if (status.equals("true")) {
                                dialog_ask.dismiss();

                                AlertDialog.Builder dig_confirm = new AlertDialog.Builder(OtherUserProfileActivity.this);
                                dig_confirm.setMessage("서로이웃 신청이 취소되었습니다.");
                                dig_confirm.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                dig_confirm.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {

                    }
                });
            }
        });

        Button btn_neighbor = dialog_ask.findViewById(R.id.btn_neighbor); // 이웃으로 전환
        btn_neighbor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int border = 0;

                retrofitInterface.ChangeBfToNeighbor(now_user_email, user_email, border).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            FriendData result = response.body();
                            Log.e(TAG, "onResponse: 서로이웃 신청 취소 result = " + result);

                            String status = result.getStatus();
                            if (status.equals("true")) {
                                dialog_ask.dismiss();

                                AlertDialog.Builder dig_confirm = new AlertDialog.Builder(OtherUserProfileActivity.this);
                                dig_confirm.setMessage("이웃으로 전환되었습니다.");
                                dig_confirm.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                dig_confirm.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {

                    }
                });

            }
        });

        Button btn_cancle = dialog_ask.findViewById(R.id.btn_cancle); // 그냥 취소
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_ask.dismiss();
            }
        });

    }


    // A -> B 한테 친구요청한 상황인데 알고보니
    // B -> A 한테 친구 요청을 이미 한 상황에서
    // 이미 B가 A한테 친구요청을 했다는 것을 알려주는 다이얼로그
    private void showAlreadyApply() {
        AlertDialog.Builder dig_already = new AlertDialog.Builder(OtherUserProfileActivity.this);
        dig_already.setTitle("잠깐!");
        dig_already.setMessage(user_nick + " 님이 보낸 친구요청이 있어요!\n[내 프로필]에서 " + user_nick + " 님께서 보낸 친구요청을 수락해주세요!");
        dig_already.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dig_already.show();
    }

    private void showDialog() {
        dialog01.show();

        // 차단하기 버튼
        Button btn_block = dialog01.findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 메뉴 다이얼로그 없애기
                dialog01.dismiss();

                // 차단시킬건지 물어보는 다이얼로그 띄우기
                AlertDialog.Builder dig_confirm = new AlertDialog.Builder(OtherUserProfileActivity.this);
                Log.e(TAG, "onClick: usernick :" + user_nick);
                dig_confirm.setTitle(user_nick + "님을 차단하시겠습니까?");
                dig_confirm.setMessage("OurTravle에서 회원님의 동행, 일기 등에서 해당 이용자를 찾을 수 없게 됩니다.\n 해당 이용자에게 회원님이 차단한 사실을 알리지 않습니다.");
                // 차단하기 선택
                dig_confirm.setNegativeButton("차단하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                // 취소 선택
                dig_confirm.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig_confirm.show();
            }
        });

        // 신고하기 버튼 - 기능 구현 보류 해놓기
        Button btn_report = dialog01.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog01.dismiss();
                dialogreport.show();
            }
        });

        // 취소 버튼
        Button btn_cancle = dialog01.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog01.dismiss();
            }
        });
    }


    // 다른 유저의 프레그먼트 따로 만들어야 할듯
    private void FragmentView(int fragment) {

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1:
                // 첫번 째 프래그먼트 호출 - 동행글
                // 동행글이 있다면 동행글 프레그먼트가 나오고 없다면 다른 프레그먼트를 넣기.
                retrofitInterface.listofuser(user_email).enqueue(new Callback<List<CompanyData>>() {
                    @Override
                    public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            List<CompanyData> result = response.body();
                            Log.e(TAG, "동행글 리스트 response body : " + result);

                            // 동행글이 없다면
                            if (result.size() == 0) {
                                // 동행글 없는 프레그먼트 띄우기
                                NoneOtherCompany noneCompany = new NoneOtherCompany();
                                transaction.replace(R.id.fragment_container, noneCompany);
                                transaction.commit();

                            }
                            // 동행글이 있다면
                            else {
                                Log.e(TAG, "FragmentView: 1");
                                OtherUserCompany fragment1 = new OtherUserCompany(user_email);
                                transaction.replace(R.id.fragment_container, fragment1);
                                transaction.commit();
                            }
                        } else {
                            Log.e(TAG, "동행글 리스트 onResponse is fail ");
                        }


                    }

                    @Override
                    public void onFailure(Call<List<CompanyData>> call, Throwable t) {

                    }
                });


                break;

            case 2:
                // 두번 째 프래그먼트 호출 - 일기

                // 일기가 없다면 다른 프레그먼트 일기가 있다면 일기 프레그 먼트
                retrofitInterface.diaryofuser(user_email).enqueue(new Callback<List<DiaryData>>() {
                    @Override
                    public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<DiaryData> result = response.body();
                            Log.e(TAG, "다이어리 리스트 onResponse: result :" + result);

                            // 다이어리가 없다면
                            if (result.size() == 0) {
                                // 다이어리 없는 프래그먼트 띄우기
                                NoneOtherDiary noneDiary = new NoneOtherDiary();
                                transaction.replace(R.id.fragment_container, noneDiary);
                                transaction.commit();

                            }
                            // 다이어리가 있다면
                            else {
                                OtherUserDiary fragment2 = new OtherUserDiary(user_email);
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();
                            }
                        } else {
                            Log.e(TAG, "다이어리 리스트 onResponse is fail ");
                        }

                    }

                    @Override
                    public void onFailure(Call<List<DiaryData>> call, Throwable t) {

                    }
                });

                break;
        }

    }

}