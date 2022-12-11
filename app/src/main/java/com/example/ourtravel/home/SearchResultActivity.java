package com.example.ourtravel.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ourtravel.R;
import com.example.ourtravel.Adapter.HomeAdapter;
import com.example.ourtravel.RecyclerView.HomeData;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    private ImageButton backbtn; // 뒤로가기 버튼
    private TextView tx_search; // 검색어

    final String TAG = "SearchResultActivity";

    // 레트로핏 통신 위한 client객체, interface 객체 생성
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    // 리사이클러뷰 준비
    RecyclerView recyclerView= null;
    HomeAdapter adapter = null;
    ArrayList<HomeData> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 보낸 인텐트 받는 부분
        Intent intent = getIntent();

        //TextView
        tx_search = findViewById(R.id.tx_search);
        //유저가 검색한 검색어 변수
        String research = (String)intent.getExtras().get("research");
        tx_search.setText(research);

        // 리사이클러뷰 준비
        recyclerView = findViewById(R.id.rv_searchresult);
        list = new ArrayList<>();
        adapter = new HomeAdapter(list , getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 레트로핏 통신 준비
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        // 유저가 검색한 검색어를 토대로 db에서 데이터들을 select 하기 위한 레트로핏
        retrofitInterface.SetSearchResult(research).enqueue(new Callback<List<CompanyData>>() {
            @Override
            public void onResponse(Call<List<CompanyData>> call, Response<List<CompanyData>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    List<CompanyData> result = response.body();

                    for(int i=0; i<result.size(); i++)
                    {
                        CompanyData companyData = result.get(i);
                        HomeData homeData = new HomeData(companyData);
                        list.add(homeData);
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Log.e(TAG, "onResponse: 검색결과 fail");
                }
            }

            @Override
            public void onFailure(Call<List<CompanyData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });


        // 뒤로가기 버튼
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다시 홈 화면으로 이동 하게 됨.
                Intent intent = new Intent(SearchResultActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}