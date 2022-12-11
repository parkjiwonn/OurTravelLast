package com.example.ourtravel.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.ScoreData;

import java.util.ArrayList;

// 동행글 점수 남기기 액티비티에서 유저가 참여했고 현재 동행이 종료된 동행 리스트를 불러오기 위해
// 리사이클러뷰로 동행글리스트를 나타내주려고 함.

public class CompanyScoreAdapter extends RecyclerView.Adapter<CompanyScoreAdapter.ViewHolder> {

    final String TAG = "CompanyScoreAdapter";

    // 데이터 리스트 객체 생성
    private final ArrayList<ScoreData> scoreDataArrayList;
    private Context context;

    public static float rate_score=0;

    public CompanyScoreAdapter(ArrayList<ScoreData> scoreDataArrayList, Context context) {
        // 데이터 리스트 객체를 전달 받음.
        this.scoreDataArrayList = scoreDataArrayList;
        this.context = context;
    }

    //---------------------별점 change 리스너--------------------------------
    public interface OnItemChangeListerner{
        void onScoreChange(float v, int pos);
        void onCheckClick(View v, int pos);
    }
    private CompanyScoreAdapter.OnItemChangeListerner cListerner = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemChangeListerner(OnItemChangeListerner listerner){
        this.cListerner = listerner;
    }
    //---------------------별점 change 리스너--------------------------------

    // viewtype 형태의 아이템 뷰를 위한 뷰 홀더 객체 생성하는 부분.
    @NonNull
    @Override
    public CompanyScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.companyscore_item, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        CompanyScoreAdapter.ViewHolder dvh = new CompanyScoreAdapter.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return dvh;
    }

    //실제 각 뷰 홀더에 데이터를 연결해주는 메서드
    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시하는 부분.
    @Override
    public void onBindViewHolder(@NonNull CompanyScoreAdapter.ViewHolder holder, int position) {

        Log.e(TAG, "onBindViewHolder:  + 들어옴 ");
        ScoreData score = scoreDataArrayList.get(position);
        int visibility = score.getMessage();

        holder.tx_title.setText(score.getTitle());
        holder.tx_start.setText(score.getStart() + "      ~");
        holder.tx_finish.setText(score.getFinish());
        holder.tx_score.setText(String.valueOf(rate_score));

        //사용자 데이터 리스트의 position 값을 가져와라.
        //그래야 사용자데이터리스트의 positon에 해당하는 데이터를 바인딩해줄수있기때문에.
        if(visibility == 1)
        {
            // 완료되었다는 뜻
            holder.btn_check.setEnabled(false);
            holder.btn_check.setText("완료");
            holder.mediumRatingBar.setIsIndicator(true);
        }
        else if(visibility == 0){

            // rating bar를 건드리지 않았다는 뜻
            holder.btn_check.setVisibility(View.GONE);
        }
        else{

            // rating bar를 건들였다는 뜻
            holder.btn_check.setVisibility(View.VISIBLE);
        }


    }

    // 전체 아이템 갯수 리턴
    @Override
    public int getItemCount() {
        // 리사이클러뷰에 셋팅될 아이템들의 갯수
        return scoreDataArrayList.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    // 리사이클러뷰 안에 들어갈 뷰홀더, 그리고 그 뷰 홀더에 들어갈 아이템을 지정한다.
    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView tx_title, tx_start, tx_finish, tx_score;
        RatingBar mediumRatingBar;
        Button btn_check;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.e(TAG, "ViewHolder: 들어옴" );
            // 뷰홀더 셋팅
            //뷰 객체에 대한 참조
            // 부모 아이템 영역
            this.tx_title = itemView.findViewById(R.id.tx_title);
            this.tx_start = itemView.findViewById(R.id.tx_start);
            this.tx_finish = itemView.findViewById(R.id.tx_finish);
            this.tx_score = itemView.findViewById(R.id.tx_score);
            this.mediumRatingBar = itemView.findViewById(R.id.mediumRatingBar);
            this.btn_check = itemView.findViewById(R.id.btn_check);

            mediumRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListerner != null)
                        {
                            cListerner.onScoreChange(rating, pos);
                            tx_score.setText(String.valueOf(rate_score));
                        }
                    }

                }
            });



            btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListerner != null)
                        {
                            cListerner.onCheckClick(view, pos);



                        }
                    }
                }
            });

        }
    }
}
