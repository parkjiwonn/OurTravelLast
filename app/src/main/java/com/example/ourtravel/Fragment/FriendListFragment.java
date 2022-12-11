package com.example.ourtravel.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.Adapter.DiaryAdpater;
import com.example.ourtravel.Adapter.FriendListAdapter;
import com.example.ourtravel.Adapter.FriendRequestAdapter;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.FriendData;
import com.example.ourtravel.retrofit.NeighborData;
import com.example.ourtravel.retrofit.OngoingData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 친구리스트
public class FriendListFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    RecyclerView recyclerView = null;
    FriendListAdapter adapter = null;
    ArrayList<FriendData> list ;

    private PreferenceHelper preferenceHelper;
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 현재 로그인한 유저의 이메일 변수
    String now_user_email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // 프래그먼트 뷰
        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);

        // 다이어리 리스트 뿌려줄 다이어리 리사이클러뷰
        recyclerView = view.findViewById(R.id.rv_list);
        list = new ArrayList<>();
        // 어댑터에 다이어리 arraylist 넣어준다.
        adapter = new FriendListAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        // 리사이클러뷰 레이아웃 매니저 만들기
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 어댑터 클릭 인터페이스는 여기서 관장함.

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 서버 통신하기 위한 메서드 호출
        getMysql();

    }

    private void getMysql() {

        preferenceHelper = new PreferenceHelper(getContext());

        // shared에서 유저 이메일 추출
        now_user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 친구 리스트 불러오기
        retrofitInterface.FriendList(now_user_email).enqueue(new Callback<List<FriendData>>() {
            @Override
            public void onResponse(Call<List<FriendData>> call, Response<List<FriendData>> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    List<FriendData> result = response.body();
                    Log.e(TAG, "onResponse: 친구 리스트 result = " + result );

                    for(int i=0; i<result.size(); i++)
                    {
                        list.add(result.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    Log.e(TAG, "onResponse: 친구 리스트 요청 response false" );
                }
            }

            @Override
            public void onFailure(Call<List<FriendData>> call, Throwable t) {
                Log.e("onresponse", "친구 리스트 프레그 먼트 에러 : " + t.getMessage());
            }
        });

    }


}
