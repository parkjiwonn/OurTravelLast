package com.example.ourtravel.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ourtravel.Map.Model.ListClass;
import com.example.ourtravel.Map.Model.MainPojo;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.diary.DiaryActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchLocationActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); //현재 액티비티 이름 가져오기 TAG
    EditText editText;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        editText = findViewById(R.id.edittext);
        recyclerView = findViewById(R.id.recyclerview);
        relativeLayout = findViewById(R.id.notdata_found);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                getData(editable.toString());
            }
        });


    }

    private void getData(String text){
        apiInterface.getPlace(text, getString(R.string.api_key)).enqueue(new Callback<MainPojo>() {
            @Override
            public void onResponse(Call<MainPojo> call, Response<MainPojo> response) {

                if(response.isSuccessful())
                {
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    // 레트로핏으로 받아온 구글 주소 결과들 리사이클려뷰에 바로 넣어주기
                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(response.body().getPredictions());
                    recyclerView.setAdapter(recyclerviewAdapter);
                    Log.e(TAG, "onResponse: response.body =" + response.body().getPredictions() );

                    ArrayList<ListClass> mainPojoList = response.body().getPredictions();

                    recyclerviewAdapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int pos) {
                            Log.e(TAG, "onItemClick: item = " + pos );

                            // 리사이클러뷰 아이템 클릭했을 때 해당 주소 가져오기
                            Log.e(TAG, "onItemClick: item array = " + mainPojoList.get(pos) );

                            Log.e(TAG, "onItemClick: item value =  " + mainPojoList.get(pos).getDescription() );

                            // 주소 정보 분할해야 됨.
                            String value = mainPojoList.get(pos).getDescription();
                            String[] valuelist = value.split(",");
                            // 주소 분할해 마지막 문자
                            String lastword = valuelist[valuelist.length -1];
                            Log.e(TAG, "onItemClick: lastword = " + lastword );

                            Intent intent = new Intent(SearchLocationActivity.this, LocationResultActivity.class);
                            // 다른 액티비티로 이동
                            intent.putExtra("location", lastword); // 검색한 주소
                            startActivity(intent);
                            finish();

                        }
                    });



                }
                else{
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainPojo> call, Throwable t) {

                Toast.makeText(SearchLocationActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}