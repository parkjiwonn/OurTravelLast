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

import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.DiaryAdpater;
import com.example.ourtravel.RecyclerView.RecyclerDiaryData;
import com.example.ourtravel.retrofit.DiaryData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 다이어리 프래그먼트
public class CompanyFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG

    // 리사이클러뷰, 어댑터, arraylist 선언
    RecyclerView recyclerView = null;
    DiaryAdpater diaryAdpater = null;
    ArrayList<RecyclerDiaryData> diaryDataArrayList;

    // 서버통신위해서 필요한 현재 로그인한 유저의 이메일 가져올수 있는 shared
    // 레트로핏 통신하기 위한 레트로핏 client, interface 객체들
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
        View view = inflater.inflate(R.layout.companyfragment, container, false);

        // 다이어리 리스트 뿌려줄 다이어리 리사이클러뷰
        recyclerView = view.findViewById(R.id.DiaryRecyclerView);
        diaryDataArrayList = new ArrayList<>();
        // 어댑터에 다이어리 arraylist 넣어준다.
        diaryAdpater = new DiaryAdpater(diaryDataArrayList, getContext());
        recyclerView.setAdapter(diaryAdpater);
        // 리사이클러뷰 레이아웃 매니저 만들기
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 서버 통신하기 위한 메서드 호출
        getMysql();

    }

    // 서버 통신하기 위한 메서드 생성
    private void getMysql(){
        preferenceHelper = new PreferenceHelper(getContext());

        // shared에서 유저 이메일 추출
        now_user_email = preferenceHelper.getEmail();

        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 유저가 쓴 다이어리 리스트 불러오기.
        retrofitInterface.diaryofuser(now_user_email).enqueue(new Callback<List<DiaryData>>() {
            @Override
            public void onResponse(Call<List<DiaryData>> call, Response<List<DiaryData>> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    List<DiaryData> result = response.body();

                    for(int i =0; i<result.size(); i++)
                    {
                        DiaryData diaryData = result.get(i);
                        RecyclerDiaryData recyclerDiaryData = new RecyclerDiaryData(diaryData);
                        diaryDataArrayList.add(recyclerDiaryData);
                    }

                    diaryAdpater.notifyDataSetChanged();

                    Log.e(TAG, " response body : " + result );
                }
                else
                {
                    Log.e(TAG, "onResponse: 유저가 쓴 다이어리 response false" );
                }
            }

            @Override
            public void onFailure(Call<List<DiaryData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });
    }
}
