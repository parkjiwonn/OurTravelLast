package com.example.ourtravel.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.Adapter.HomeAdapter;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.HomeData;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserCompany extends Fragment {

    final String TAG = "OtherUserCompany";

    RecyclerView recyclerView = null;
    HomeAdapter adapter = null;
    ArrayList<HomeData> list ;


    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    String user_email;

    public OtherUserCompany(String user_email) {
        this.user_email = user_email;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.diaryfragment, container, false);



        recyclerView = view.findViewById(R.id.HomeRecyclerView);
        //recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext());
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


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMysql();

    }

    private void getMysql(){



        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        retrofitInterface.listofuser(user_email).enqueue(new Callback<List<CompanyData>>() {
            @Override
            public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    List<CompanyData> result = response.body();
                    Log.e(TAG, " response body : " + result );


                        for (int i=0; i < result.size(); i++) {

                            //응답이 companydata로 온다. list형태로
                            //근데 adapter에 담아야하는 데이터는 home data이다.
                            //이 둘을 같이 묶어줘야 한다.
                            CompanyData companyData = result.get(i);
                            Log.e(TAG, "onResponse: 프로필 동행글 리스트 result :" +result );
                            HomeData homeData = new HomeData(companyData);
                            list.add(homeData);

                        }

                        adapter.notifyDataSetChanged();


                }
                else
                {
                    Log.e(TAG, "onResponse: response false2" );
                }

            }

            @Override
            public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });

    }
}
