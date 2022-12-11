package com.example.ourtravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.RecyclerDiaryData;
import com.example.ourtravel.diary.RdiaryActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;

public class DiaryAdpater extends RecyclerView.Adapter<DiaryAdpater.ViewHolder> {

    // 중첩리사이클러뷰 사이에서 뷰들을 공유할수있게 해주는 inner class이다.
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    // 데이터 리스트 객체 생성
    private final ArrayList<RecyclerDiaryData> diaryDataArrayList;
    private Context context;
    int getDiaryNum;
    int pos;


    private final String TAG = this.getClass().getSimpleName();

    public DiaryAdpater(ArrayList<RecyclerDiaryData> diaryDataArrayList, Context context) {
        Log.e(TAG, "DiaryAdpater: 생성자 들어옴" );
        Log.e(TAG, "DiaryAdpater: array size = " + diaryDataArrayList.size() );
        // 데이터 리스트 객체를 전달 받음.
        this.diaryDataArrayList = diaryDataArrayList;
        this. context = context;
    }

    //--------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onLikeClick(View v, int pos);
        void onImgClick(View v, int pos);

    }
    // 커스텀 리스너 인터페이스 정의

    private DiaryAdpater.OnItemClickListener dListener = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.dListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //--------------------Custom Click Listener-----------------------


    // viewtype 형태의 아이템 뷰를 위한 뷰 홀더 객체 생성하는 부분.
    @NonNull
    @Override
    public DiaryAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.diaryitem, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        DiaryAdpater.ViewHolder dvh = new DiaryAdpater.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return dvh;
    }

    //실제 각 뷰 홀더에 데이터를 연결해주는 메서드
    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시하는 부분.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.e(TAG, "onBindViewHolder: 들어옴" );

        RecyclerDiaryData diary = diaryDataArrayList.get(position);
        //사용자 데이터 리스트의 position 값을 가져와라.
        //그래야 사용자데이터리스트의 positon에 해당하는 데이터를 바인딩해줄수있기때문에.
        holder.tx_nick.setText(diary.getNick()); // 작성자 닉네임
        holder.tx_content.setText(diary.getContent()); // 작성 내용
        holder.tx_date.setText(formatTimeString(diary.getDate())); // 작성 시간
        holder.tx_likecnt.setText(String.valueOf(diary.getLike()));    // 좋아요 갯수
        holder.tx_commentcnt.setText(String.valueOf(diary.getComment()));  // 댓글 수

        if(diary.getMessage().equals("true"))
        {
            holder.btn_like.setChecked(true);
        }
        else
        {
            holder.btn_like.setChecked(false);
        }


        String imageUrl = diary.getProfile(); // 유저 프로필 이미지 url
        Log.e("TAG", "onBindViewHolder: " + imageUrl );

        // Userinfo db에서 유저 닉네임, 프로필 사진 정보 받아와서 응답 보내기
        // 유저 닉네임, 사진 url 받아서
        // 기본 사진인지 아닌지 분기처리해서 아이템 세팅해주기.

        if(imageUrl.equals("basic"))
        {
            holder.img.setImageResource(R.drawable.profile2);

        }
        else
        {
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.39.168.165/userimg/" + imageUrl;

            Glide.with(holder.itemView.getContext()).load(url).into(holder.img);
        }
        // 데이터를 삭제, 수정, 추가 모두 어댑터에 진행한다.
        // 모든 CRUD하는 거는 어댑터에서 한다.
        if(diary.getSubItemList().size() != 0)
        {
            // 자식 레이아웃 매니저 설정
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.rvSubitem.getContext(), LinearLayoutManager.HORIZONTAL, false);
            //prefetch 란 미리 불러오기 기능.
            //inneritem 에 들어갈 item이 몇개인지 미리 연산해보는 매서드.
            layoutManager.setInitialPrefetchItemCount(diary.getSubItemList().size());

            // 자식 어댑터 생성
            SubItemAdapter subItemAdapter = new SubItemAdapter(diary.getSubItemList());

            holder.rvSubitem.setLayoutManager(layoutManager);
            holder.rvSubitem.setAdapter(subItemAdapter);
            holder.rvSubitem.setRecycledViewPool(viewPool);
        }



    }



    // 게시글 등록 시간 (long타입) 받아와서 변형해주는 함수.
    public static String formatTimeString(long regTime) {

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR= 24;
        final int DAY= 30;
        final int MONTH= 12;

        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < SEC) {
            msg = "방금 전";
        } else if ((diffTime /= SEC) < MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            msg = (diffTime) + "일 전";

        } else if ((diffTime /= DAY) < MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }

    // 전체 아이템 갯수 리턴
    @Override
    public int getItemCount() {
        // 리사이클러뷰에 셋팅될 아이템들의 갯수
        Log.e(TAG, "getItemCount: 들어옴" );
        return diaryDataArrayList.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    // 리사이클러뷰 안에 들어갈 뷰홀더, 그리고 그 뷰 홀더에 들어갈 아이템을 지정한다.
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tx_nick , tx_date, tx_content, tx_likecnt, tx_commentcnt;
        TextView tx_rereply;
        ImageView img;
        // viewholder에서 뷰페이저 선언
        RecyclerView rvSubitem;
        ToggleButton btn_like;

        public ViewHolder(@NonNull View view) {
            super(view);
            // 뷰홀더 셋팅
            //뷰 객체에 대한 참조
            // 부모 아이템 영역
            // 닉네임, 작성시간, 내용, 좋아요 개수, 댓글개수, 이미지 -> 리사이클러뷰에 셋팅할 데이터들
            this.tx_nick = itemView.findViewById(R.id.tx_nick);
            this.tx_date = itemView.findViewById(R.id.tx_date);
            this.tx_content = itemView.findViewById(R.id.tx_content);
            this.tx_likecnt = itemView.findViewById(R.id.tx_likecnt);
            this.tx_commentcnt = itemView.findViewById(R.id.tx_commentcnt);
            this.img = itemView.findViewById(R.id.img);
            this.rvSubitem = itemView.findViewById(R.id.rv_sub_item);
            this.btn_like = itemView.findViewById(R.id.btn_like); // 좋아요 toggle버튼


            // 좋아요 버튼 눌렀을 때
            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onLikeClick(view, pos);
                        }
                    }
                }
            });

            // 일기 리스트에서 유저 프로필 사진 클릭하면 해당유저의 프로필로 이동하기
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onImgClick(view, pos);
                        }
                    }
                }
            });




            // 아이템 클릭 상태 true
            view.setClickable(true);
            // 아이템 뷰 클릭리스너
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    // 리사이클러뷰 아이템 클릭시 다이어리 상세보기 페이지로 넘어가야함.

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        RecyclerDiaryData item = diaryDataArrayList.get(pos);
                        getDiaryNum = item.getId(); // 게시글의 id 보내야 한다.
                        // nick, profile, content, likecnt, commentcnt, date
                        String profile = item.getProfile(); // 작성자 프로필
                        String nick = item.getNick(); // 작성자 닉네임
                        String date = formatTimeString(item.getDate()); // 작성 시간
                        String content = item.getContent(); // 작성 내용
                        int likecnt = item.getLike(); // 좋아요 개수
                        int commentcnt = item.getComment(); // 댓글 개수
                        String user_email = item.getUser_email();
                        String message = item.getMessage();// 좋아요 클릭 여부 확인 메세지

                        Intent intent = new Intent(context, RdiaryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id", getDiaryNum);
                        intent.putExtra("profile", profile);
                        intent.putExtra("nick", nick);
                        intent.putExtra("date", date);
                        intent.putExtra("content", content);
                        intent.putExtra("like",likecnt);
                        intent.putExtra("comment",commentcnt);
                        intent.putExtra("user_email", user_email);
                        intent.putExtra("message", message);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }



}


