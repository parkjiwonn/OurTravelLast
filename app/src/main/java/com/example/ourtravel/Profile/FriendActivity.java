package com.example.ourtravel.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourtravel.Fragment.FriendListFragment;
import com.example.ourtravel.Fragment.FriendRequestFragment;
import com.example.ourtravel.Fragment.FriendResponseFragment;
import com.example.ourtravel.None.NoneFriendList;
import com.example.ourtravel.None.NoneFriendRequest;
import com.example.ourtravel.None.NoneFriendResponse;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.FriendData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;
    final String TAG = "FriendActivity";

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String now_user_email;


    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;
    private final int Fragment_3 = 3;

    private Button view_list, view_response, view_request; // 친구 목록, 받은 친구 요청, 보낸 친구 요청
    private TextView tx_list, tx_response, tx_request; // 친구 목록 선, 받은 친구 요청 선, 보낸 친구 요청 선

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //textview
        tx_list = findViewById(R.id.tx_list); // 모두
        tx_response = findViewById(R.id.tx_response); // 받은 친구 요청
        tx_request = findViewById(R.id.tx_request); // 보낸 친구 요청

        //button
        view_list = findViewById(R.id.view_list); // 모두
        view_response = findViewById(R.id.view_response); // 받은 친구 요청
        view_request = findViewById(R.id.view_request); // 보낸 친구 요청

        //처음 셋팅은 모두 선택
        //text밑에 표시
        view_list.setVisibility(View.VISIBLE); // 친구 목록 밑 표시 보이게 하기
        view_response.setVisibility(View.INVISIBLE); // 받은 요청 밑 표시 안보이게 하기
        view_request.setVisibility(View.INVISIBLE); // 보낸 요청 밑 표시 안보이게 하기


        // shared 객체 생성
        preferenceHelper = new PreferenceHelper(this);
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저 이메일

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 처음 friend 액티비티 들어왔을때 모두 리스트가 클릭되어있어야 함.
        FragmentView(Fragment_1);

        // 친구 모두 클릭
        tx_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_1);
                view_list.setVisibility(View.VISIBLE);
                view_response.setVisibility(View.INVISIBLE);
                view_request.setVisibility(View.INVISIBLE);
            }
        });

        // 받은 요청
        tx_response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_2);
                view_response.setVisibility(View.VISIBLE);
                view_list.setVisibility(View.INVISIBLE);
                view_request.setVisibility(View.INVISIBLE);
            }
        });

        // 보낸 요청
        tx_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(Fragment_3);
                view_request.setVisibility(View.VISIBLE);
                view_list.setVisibility(View.INVISIBLE);
                view_response.setVisibility(View.INVISIBLE);
            }
        });



    }

    // 프레그 먼트 뷰
    private void FragmentView(int fragment) {

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:

                // 현재 로그인한 유저와 친구인 사람이 있는지 없는지 부터 확인
                retrofitInterface.CheckFriendList(now_user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result= response.body();
                            Log.e(TAG, "onResponse: 유저 친구리스트 유무 : " + result );

                            String status = result.getStatus();

                            if(status.equals("true"))
                            {
                                // 유저의 친구리스트가 존재한다
                                // 첫번 째 프래그먼트 호출 - 친구 모두 리스트
                            FriendListFragment fragment1 = new FriendListFragment();
                            transaction.replace(R.id.fragment_container, fragment1);
                            transaction.commit();


                            }
                            else{
                                // 유저의 친구리스트가 존재하지 않는다.
                                NoneFriendList fragment1 = new NoneFriendList();
                                transaction.replace(R.id.fragment_container, fragment1);
                                transaction.commit();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {
                        Log.e("onresponse", "1선택 에러 : " + t.getMessage());
                    }
                });
                break;

            case 2:
                // 받은 친구 요청 프래그 먼트
                // 현재 로그인한 유저와 친구인 사람이 있는지 없는지 부터 확인
                retrofitInterface.CheckApplyList(now_user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result= response.body();
                            Log.e(TAG, "onResponse: 유저 친구요청 받았는지 유무 : " + result );

                            String status = result.getStatus();

                            if(status.equals("true"))
                            {

                                // 받은 친구 요청
                                FriendResponseFragment fragment2 = new FriendResponseFragment();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();


                            }
                            else{
                                // 친구 요청 받은적없다.
                                NoneFriendResponse fragment2 = new NoneFriendResponse();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {
                        Log.e("onresponse", "2 선택 에러 : " + t.getMessage());
                    }
                });
                break;

            case 3:

                // 보낸 친구요청 확인하기
                retrofitInterface.CheckRequest(now_user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result= response.body();
                            Log.e(TAG, "onResponse: 유저 친구요청 보냈는지 유무 : " + result );

                            String status = result.getStatus();

                            if(status.equals("true"))
                            {
                                // 보낸 친구요청
                                FriendRequestFragment fragment2 = new FriendRequestFragment();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();


                            }
                            else{
                                // 친구 요청 보내지도 않았다.
                                NoneFriendRequest fragment2 = new NoneFriendRequest();
                                transaction.replace(R.id.fragment_container, fragment2);
                                transaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {
                        Log.e("onresponse", "3 선택 에러 : " + t.getMessage());
                    }
                });




        }

    }
}