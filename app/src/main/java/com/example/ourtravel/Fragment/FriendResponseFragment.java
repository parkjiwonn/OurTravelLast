package com.example.ourtravel.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.Adapter.FriendRequestAdapter;
import com.example.ourtravel.Adapter.FriendResponseAdapter;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.FriendData;
import com.example.ourtravel.retrofit.OngoingData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 받은 친구 요청
public class FriendResponseFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    RecyclerView recyclerView = null;
    FriendResponseAdapter adapter = null;
    ArrayList<OngoingData> list ;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 현재 로그인한 유저의 이메일 변수
    String now_user_email;
    String user_email ; // 친구 요청 보낸 유저

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.e(TAG, "onCreateView: 들어옴" );

        View view = inflater.inflate(R.layout.friend_response_fragment, container, false);

        recyclerView = view.findViewById(R.id.rv_list);
        //recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new FriendResponseAdapter(list, getContext());
        // 현재 활성화된 activity의 context가 되기 때문에 getContex해준것.
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "onViewCreated: 들어옴" );

        adapter.setOnItemClickListener(new FriendResponseAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(View v, int pos) {
                // 친구 요청 수락 버튼

                user_email = list.get(pos).getFrom();
                Log.e(TAG, "onAcceptClick: user_email : "+user_email );
                // 누가 친구요청 보냈는지 잘 받아옴.
                // 친구 요청
                int border = 1;
                retrofitInterface.AcceptFriend(now_user_email,user_email,border).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result = response.body();

                            Log.e(TAG, "onResponse: 받은 친구 요청 수락 result = " + result );

                            String status = result.getStatus();

                            if(status.equals("true"))
                            {
                                AlertDialog.Builder dig_accept = new AlertDialog.Builder(getActivity());
                                dig_accept.setMessage("서로 이웃 신청을 수락했습니다.");
                                dig_accept.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        list.remove(pos);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                dig_accept.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendData> call, Throwable t) {

                    }
                });



            }

            @Override
            public void onCancleClick(View v, int pos) {
                // 친구 요청 거절 버튼
                AlertDialog.Builder dig_deny = new AlertDialog.Builder(getActivity());
                dig_deny.setTitle("서로이웃 신청을 거절하시겠습니까?");
                dig_deny.setMessage("신청을 거절해도 거절 알림이 전달되지는 않습니다.\n다만, 이웃으로 추가됩니다.");
                dig_deny.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int border = 0;
                        user_email = list.get(pos).getFrom();
                        retrofitInterface.DeleteRequest(now_user_email, user_email, border).enqueue(new Callback<FriendData>() {
                            @Override
                            public void onResponse(Call<FriendData> call, Response<FriendData> response) {
                                if(response.isSuccessful() && response.body() != null)
                                {
                                    FriendData result = response.body();
                                    Log.e(TAG, "onResponse: 서로이웃 신청 거절 result = " + result );

                                    String status = result.getStatus();

                                    if(status.equals("true"))
                                    {
                                        AlertDialog.Builder dig_confirm = new AlertDialog.Builder(getActivity());
                                        dig_confirm.setMessage("서로이웃 신청을 거절했습니다.");
                                        dig_confirm.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                list.remove(pos);
                                                adapter.notifyDataSetChanged();
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
                dig_deny.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dig_deny.show();


            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: 들어옴" );
        getMysql();

    }

    private void getMysql(){

        preferenceHelper = new PreferenceHelper(getContext());

        // shared에서 유저 이메일 추출
        now_user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 보낸 친구 요청 리스트 불러오기
        retrofitInterface.ResponseList(now_user_email).enqueue(new Callback<List<OngoingData>>() {
            @Override
            public void onResponse(Call<List<OngoingData>> call, Response<List<OngoingData>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    List<OngoingData> result = response.body();

                    for(int i =0; i<result.size(); i++)
                    {

                        list.add(result.get(i));
                    }

                    adapter.notifyDataSetChanged();

                    Log.e(TAG, " response body : " + result );
                }
                else
                {
                    Log.e(TAG, "onResponse: 받은 친구 요청 response false" );
                }
            }

            @Override
            public void onFailure(Call<List<OngoingData>> call, Throwable t) {
                Log.e("onresponse", "받은 친구 요청 프레그 먼트 에러 : " + t.getMessage());
            }
        });

    }

}
