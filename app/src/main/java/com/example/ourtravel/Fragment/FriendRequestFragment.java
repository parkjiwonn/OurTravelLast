package com.example.ourtravel.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.Adapter.DiaryAdpater;
import com.example.ourtravel.Adapter.FriendRequestAdapter;
import com.example.ourtravel.Adapter.HomeAdapter;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.HomeData;
import com.example.ourtravel.RecyclerView.RecyclerDiaryData;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.DiaryData;
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

// 보낸 친구 요청
public class FriendRequestFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    RecyclerView recyclerView = null;
    FriendRequestAdapter adapter = null;
    ArrayList<OngoingData> list ;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 현재 로그인한 유저의 이메일 변수
    String now_user_email;
    // 친구요청 취소당할 유저
    String user_email;
    Dialog dialog_ask; // 서로이웃 이미 요청한 상태에서 나오는 다이얼로그

    // onCreate 이후에 onCreateView, onViewCreated 함수가 이어서 호출된다.
    // onCreateView() 를 통해 반환된 View 객체는 onViewCreated() 의 파라미터로 전달되는데,
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.e(TAG, "onCreateView: 들어옴" );

        View view = inflater.inflate(R.layout.friend_request_fragment, container, false);

        recyclerView = view.findViewById(R.id.rv_list);
        //recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new FriendRequestAdapter(list, getContext());
        // 현재 활성화된 activity의 context가 되기 때문에 getContex해준것.
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 서로이웃 이미 요청한 상태에서 나오는 다이얼로그
        dialog_ask = new Dialog(getActivity());
        dialog_ask.setContentView(R.layout.dialog_askfriend);


        // onCreateView를 통해 반환된 View 객체이다.
        // onViewCreated 의 파라미터로 전달된다.
        // 이 시점부터 fragment View의 lifecycle이 INITIALIZED(초기화) 상태로 업데이트 됐다.

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "onViewCreated: 들어옴" );
        adapter.setOnItemClickListener(new FriendRequestAdapter.OnItemClickListener() {
            @Override
            public void onCancleClick(View v, int pos) {
                Log.e(TAG, "onCancleClick: 보낸 요청 취소 버튼 클릭" );
                user_email = list.get(pos).getTo();
                Log.e(TAG, "onCancleClick: user_email :"+user_email );
                showConfirm(pos);
            }
        });
    }

    // 보낸 친구요청을 취소할건지 확인하는 다이얼로그
    private void showConfirm(int pos) {
        dialog_ask.show();

        Button btn_bfcancle = dialog_ask.findViewById(R.id.btn_bfcancle); // 서로이웃 취소
        btn_bfcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofitInterface.CancleFriend(now_user_email, user_email).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result = response.body();
                            Log.e(TAG, "onResponse: 서로이웃 신청 취소 result = " + result );

                            String status = result.getStatus();
                            if(status.equals("true"))
                            {
                                dialog_ask.dismiss();

                                AlertDialog.Builder dig_confirm = new AlertDialog.Builder(getActivity());
                                dig_confirm.setMessage("서로이웃 신청이 취소되었습니다.");
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

        Button btn_neighbor = dialog_ask.findViewById(R.id.btn_neighbor); // 이웃으로 전환
        btn_neighbor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int border = 0;

                retrofitInterface.ChangeBfToNeighbor(now_user_email, user_email, border).enqueue(new Callback<FriendData>() {
                    @Override
                    public void onResponse(Call<FriendData> call, Response<FriendData> response) {

                        if(response.isSuccessful() && response.body() != null)
                        {
                            FriendData result = response.body();
                            Log.e(TAG, "onResponse: 서로이웃 신청 취소 result = " + result );

                            String status = result.getStatus();
                            if(status.equals("true"))
                            {
                                dialog_ask.dismiss();

                                AlertDialog.Builder dig_confirm = new AlertDialog.Builder(getActivity());
                                dig_confirm.setMessage("이웃으로 전환되었습니다.");
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

        Button btn_cancle = dialog_ask.findViewById(R.id.btn_cancle); // 그냥 취소
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_ask.dismiss();
            }
        });
    }

    // 이미 상대방이 친구 요청 수락해서 친구 취소가 안된다는 것을 알리는 다이얼로그
    private void showAlreadyFriend() {

        AlertDialog.Builder dig_already = new AlertDialog.Builder(getActivity());
        dig_already.setTitle("잠깐!");
        dig_already.setMessage("이미 친구요청 수락이 완료된 요청입니다!");
    }


    // Fragment만 Created된 상황
    // 이 시점에는 fragment view 가 생성되지 않았기 때문에 fragment의 view와 관련된 작업을 두기에 적절하지 않다.
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
        retrofitInterface.RequestList(now_user_email).enqueue(new Callback<List<OngoingData>>() {
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
                    Log.e(TAG, "onResponse: 보낸 친구 요청 response false" );
                }
            }

            @Override
            public void onFailure(Call<List<OngoingData>> call, Throwable t) {
                Log.e("onresponse", "보낸 친구 요청 프레그 먼트 에러 : " + t.getMessage());
            }
        });

    }
}
