package com.example.ourtravel.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourtravel.R;
import com.example.ourtravel.Search_rv.ComAdapter;
import com.example.ourtravel.Search_rv.ComData;
import com.example.ourtravel.diary.DiaryActivity;
import com.example.ourtravel.retrofit.CompanyData;
import com.example.ourtravel.retrofit.ResearchData;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    final String TAG = getClass().getSimpleName();

    private ImageButton backbtn; // 뒤로가기 버튼
    private EditText et_search; // 검색 창
    private ImageButton btn_search; // 검색 버튼
    private TextView tx_remove; // 최근 검색어 지우기
    private TextView tx_recent; // 최근 검색어 리스트

    String research; // 검색어 변수

    // 레트로핏 통신 위한 client객체, interface 객체 생성
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    private PreferenceHelper preferenceHelper; // 현재 로그인한 유저 정보 담고있는 shared
    String now_user_email; // 현재 로그인한 유저의 이메일

    RecyclerView recyclerView = null;
    ComAdapter adapter = null;
    ArrayList<ComData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        backbtn = findViewById(R.id.backbtn); // 뒤로가기 버튼
        et_search = findViewById(R.id.et_search); // 검색 창
        btn_search = findViewById(R.id.btn_search); // 검색 버튼
        tx_recent = findViewById(R.id.tx_recent); // 최근 검색어
        tx_remove = findViewById(R.id.tx_remove); // 검색어 지우기

        // 레트로핏 통신 준비
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        preferenceHelper = new PreferenceHelper(this); // shared 객체 만들기
        now_user_email = preferenceHelper.getEmail(); // shared에서 이메일 가져오기.

        //리사이클러뷰 세팅
        recyclerView = findViewById(R.id.rv_recent);
        list = new ArrayList<>();
        adapter = new ComAdapter(list, getApplicationContext());

        adapter.setOnItemClickListener(new ComAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                // 아이템을 선택했을 때 list에서 데이터를 삭제하기 전에 해당 position의 id 값을 가져와야 한다.
                int id = list.get(pos).getId();
                Log.e(TAG, "onItemClick: id : " + id );
                // 최근 검색어 삭제할때 db에 있는 검색어도 삭제시켜줘야 함.
                retrofitInterface.DeleteSearch(id).enqueue(new Callback<ResearchData>() {
                    @Override
                    public void onResponse(Call<ResearchData> call, Response<ResearchData> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            ResearchData result=  response.body();
                            String status = result.getStatus();
                            String research = result.getResearch();

                            if(status.equals("true"))
                            {
                                Toast.makeText(SearchActivity.this, "["+ research + "] 검색어를 삭제했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "onResponse: delege response fail "  );
                        }
                    }

                    @Override
                    public void onFailure(Call<ResearchData> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });

                // id 값을 가져온 다음 list에서 해당 검색어를 지운다.
                list.remove(pos);
                adapter.notifyItemRemoved(pos);
                adapter.notifyDataSetChanged();


            }
        });
        //리사이클러뷰에 어댑터 장착
        recyclerView.setAdapter(adapter);
        // 리니어레이아웃메니저 -> 리사이클러뷰 수직으로 장착
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        //최근 검색어가 db에 없다면 최근 검색어가 없습니다. tx_recent 보이게 하고
        //최근 검색어가 db에 있다면 tx_recent gone 해버리고 recyclerview 띄우기.
        retrofitInterface.SetRecentSearch(now_user_email).enqueue(new Callback<List<ResearchData>>() {
            @Override
            public void onResponse(Call<List<ResearchData>> call, Response<List<ResearchData>> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    List<ResearchData> result = response.body();

                    if(result.isEmpty())
                    {
                        // 최근에 검색한 검색어가 없다면.
                        tx_recent.setVisibility(View.VISIBLE);
                        Log.e(TAG, "onResponse: 검색어X" );
                    }
                    else {
                        // 최근에 검색한 검색어가 있다면.
                        tx_recent.setVisibility(View.GONE);
                        Log.e(TAG, "onResponse: result : " + result);

                        // 응답받은 response의 갯수만큼 for문 돌려서 리사이클러뷰 데이터리스트에 add 시키기.
                        for (int i = 0; i < result.size(); i++){
                            ResearchData researchData = result.get(i);
                            ComData comData = new ComData(researchData);
                            list.add(comData);
                        }
                        // 리사이클러뷰 새로고침하기.
                        adapter.notifyDataSetChanged();

                    }
                }
                else{
                    Log.e(TAG, "onResponse: 최근 검색어 list 불러오기 fail" );
                }


            }

            @Override
            public void onFailure(Call<List<ResearchData>> call, Throwable t) {
                Log.e("onresponse", "에러 : " + t.getMessage());
            }
        });



        // 검색창에 검색어를 입력하고 검색 버튼을 눌렀을 때 검색어로 서버통신을 해서 해당 검색어를 포함하고 있는
        // 동행글 리스트를 받아와서 리사이클러뷰에 뿌려준다.
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 유저가 작성한 검색어
                // 맨 처음에는 research 변수를 클릭리스너 밖에 두었다.
                // 그러면 버튼을 클릭했을때 edittext안에 있는 text를 가져오는 것이 아니라.
                // 버튼을 누르기 전에 edittext에 있는 text를 가져오기 떄문에 자꾸만 null값이 들어갔던 것이다.

                research = et_search.getText().toString();
                Log.e(TAG, "onCreate: research : " + research );
                // 검색어 저장을 하기위해 서버통신 함.
                // 서버통신에 필요한 변수들을 전해주기 위한 메서드 호출
                search(research, now_user_email);
            }
        });

        // 검색어 모두 지우기 클릭 리스터
        tx_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 로그인한 유저의 이메일을 column으로 갖고있는 데이터들 모두 삭제.
                retrofitInterface.DeleteAll(now_user_email).enqueue(new Callback<ResearchData>() {
                    @Override
                    public void onResponse(Call<ResearchData> call, Response<ResearchData> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            ResearchData result = response.body();
                            String status = result.getStatus();

                            if(status.equals("true"))
                            {
                                Log.e(TAG, "onResponse: 모두 삭제" );
                                list.clear();

                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Log.e(TAG, "onResponse: 검색어 모두 삭제 status is false" );
                            }
                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 검색어 모두 삭제하기 fail" );
                        }
                    }

                    @Override
                    public void onFailure(Call<ResearchData> call, Throwable t) {
                        Log.e("onresponse", "에러 : " + t.getMessage());
                    }
                });
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다시 홈 화면으로 이동 하게 됨.
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    // 검색어 db에 저장하기 위한 레트로핏 통신.
    // 이 검색어를 검색한 유저의 이메일과 검색어를 서버로 보낸다.
    private void search(String research, String now_user_email){

        retrofitInterface.Research(research, now_user_email).enqueue(new Callback<ResearchData>() {
            @Override
            public void onResponse(Call<ResearchData> call, Response<ResearchData> response) {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        // 응답 body 설정해주기
                        ResearchData result = response.body();
                        String status = result.getStatus();
                        // 검색어가 db에 저장이 잘 된다면 status : true 이다.
                        if(status.equals("true"))
                        {
                            Toast.makeText(SearchActivity.this, "["+research +"] 를 검색하셨습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                            intent.putExtra("research", research);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "onResponse: 검색어 입력 status is false" );
                        }

                    }
                    else
                    {
                        Log.e(TAG, "onResponse: research fail" );
                    }
            }

            @Override
            public void onFailure(Call<ResearchData> call, Throwable t) {
                Log.e("onresponse", "에러 다이어리 리스트 : " + t.getMessage());
            }
        });
    }
}