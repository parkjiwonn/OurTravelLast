package com.example.ourtravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.Profile.OtherUserProfileActivity;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.CommentData;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //데이터 리스트 객체 생성
    private final ArrayList<CommentData> commentDataArrayList;
    private Context context;


    int pos;

    private final String TAG = this.getClass().getSimpleName();

    public CommentAdapter(ArrayList<CommentData> commentDataArrayList, Context context)
    {
        this.commentDataArrayList = commentDataArrayList;
        this.context = context;
    }

    //-----------------------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onLikeClick(View v, int pos);
        void onReplyClick(View v, int pos);
        void onMenuClick(View v, int pos);
        void onImgClick(View v, int pos);
    }
    // 커스텀 리스너 인터페이스 정의

    private CommentAdapter.OnItemClickListener cListener = null;
    // 전달된 객체를 저장할 변수 cListener 추가

    public void setOnItemClickListener(OnItemClickListener listener){
        this.cListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //-----------------------------------Custom Click Listener-----------------------

    // viewtype 형태의 아이템 뷰를 위한 뷰 홀더 객체 생성하는 부분.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "태그 onCreateViewHolder 들어옴");
        View view;
        Context Ccontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) Ccontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        if(viewType == ViewType.comment)
        {
            view = inflater.inflate(R.layout.comment_item, parent, false);
            // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
            // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.
            return new commentHolder(view);
        }
        else if(viewType == ViewType.recomment)
        {
            view = inflater.inflate(R.layout.recomment_item, parent, false);
            return new recommentHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.deletecomment_item, parent, false);
            return new deleteHolder(view);
        }




    }

    //실제 각 뷰 홀더에 데이터를 연결해주는 메서드
    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시하는 부분.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CommentData comment = commentDataArrayList.get(position);
        String imageUrl = comment.getProfile();
        String message = comment.getMessage();
        String NowLoginUser = comment.getNowLoginUser(); // 현재 로그인하고 있는 유저의 이메일
        String commentUser = comment.getUser_email(); // 댓글을 작성한 유저 이메일


        // 댓글 뷰 홀더
        if(holder instanceof commentHolder)
        {
            //프로필
            if(comment.getProfile().equals("basic"))
            {
                ((commentHolder)holder).commentimg.setImageResource(R.drawable.profile2);
            }
            else
            {
                // 경로는 userimg로 설정해줘야 함.
                String url = "http://3.39.168.165/userimg/" + imageUrl;

                Glide.with(holder.itemView.getContext()).load(url).into(((commentHolder)holder).commentimg);
            }

            //닉네임
            ((commentHolder)holder).tx_Renick.setText(comment.getNick());
            //내용
            ((commentHolder)holder).tx_comment.setText(comment.getContent());
            //작성시간
            ((commentHolder)holder).tx_retime.setText(formatTimeString(comment.getDate()));
            //좋아요 갯수
            ((commentHolder)holder).tx_relikecnt.setText(String.valueOf(comment.getFavorite()));

            if(message.equals("true"))
            {
                ((commentHolder)holder).btn_like.setChecked(true);
            }
            else
            {
                ((commentHolder)holder).btn_like.setChecked(false);
            }

            //현재 로그인한 유저와 댓글을 작성한 유저가 같다면 댓글 메뉴가 보여야 한다.

            if(commentUser.equals(NowLoginUser))
            {
                ((commentHolder)holder).btn_ReMunu.setVisibility(View.VISIBLE);
            }
            else
            {
                ((commentHolder)holder).btn_ReMunu.setVisibility(View.INVISIBLE);
            }


        }
        // 대댓글 뷰 홀더
        else if(holder instanceof recommentHolder)
        {
            //프로필
            if(comment.getProfile().equals("basic"))
            {
                ((recommentHolder)holder).recomment_img.setImageResource(R.drawable.profile2);
            }
            else
            {
                // 경로는 userimg로 설정해줘야 함.
                String url = "http://3.39.168.165/userimg/" + imageUrl;
                Glide.with(holder.itemView.getContext()).load(url).into(((recommentHolder)holder).recomment_img);
            }

            //닉네임
            ((recommentHolder)holder).recomment_nick.setText(comment.getNick());
            //내용
            ((recommentHolder)holder).recomment_content.setText(comment.getContent());
            //작성시간
            ((recommentHolder)holder).recomment_date.setText(formatTimeString(comment.getDate()));
            //좋아요 갯수
            ((recommentHolder)holder).recomment_likecnt.setText(String.valueOf(comment.getFavorite()));

            if(message.equals("true"))
            {
                ((recommentHolder)holder).recomment_like.setChecked(true);
            }
            else
            {
                ((recommentHolder)holder).recomment_like.setChecked(false);
            }

            // 현재 로그인한 유저와 대댓글을 작성한 유저가 같다면 메뉴버튼이 보이게 하고
            // 그렇지 않다면 메뉴버튼이 보이지 않게 한다.
            if (commentUser.equals(NowLoginUser)){
                ((recommentHolder)holder).btn_ReReMenu.setVisibility(View.VISIBLE);
            }
            else{
                ((recommentHolder)holder).btn_ReReMenu.setVisibility(View.INVISIBLE);
            }
        }
        // 삭제된 뷰 홀더
        else
        {
            ((deleteHolder)holder).del_img.setImageResource(R.drawable.profile2);
            ((deleteHolder)holder).del_nick.setText("(삭제)");
            ((deleteHolder)holder).del_comment.setText("삭제된 댓글입니다.");
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
        return commentDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return commentDataArrayList.get(position).getViewType();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    // 리사이클러뷰 안에 들어갈 뷰홀더, 그리고 그 뷰 홀더에 들어갈 아이템을 지정한다.
    public class commentHolder extends RecyclerView.ViewHolder {

        ImageView commentimg; // 작성한 유저의 프로필
        TextView tx_Renick , tx_comment , tx_retime , tx_relikecnt; // 작성한 유저의 닉네임, 댓글 내용, 작성시간, 댓글 좋아요 수
        TextView tx_rereply; // 답글 달기
        ToggleButton btn_like; // 댓글 좋아요
        ImageButton btn_ReMunu; // 댓글 메뉴

        public commentHolder(@NonNull View itemView) {
            super(itemView);
            // 뷰홀더 셋팅
            //뷰 객체에 대한 참조
            // 부모 아이템 영역

            this.commentimg = itemView.findViewById(R.id.commentimg); // 유저 프로필
            this.tx_Renick = itemView.findViewById(R.id.tx_Renick); // 유저 닉네임
            this.tx_comment = itemView.findViewById(R.id.tx_comment); // 댓글 내용
            this.tx_retime = itemView.findViewById(R.id.tx_retime); // 작성 시간
            this.tx_relikecnt = itemView.findViewById(R.id.tx_relikecnt); // 좋아요 수
            this.tx_rereply = itemView.findViewById(R.id.tx_rereply); // 답글달기
            this.btn_like = itemView.findViewById(R.id.btn_like); //댓글 좋아요 버튼
            this.btn_ReMunu = itemView.findViewById(R.id.btn_ReMenu); //댓글 메뉴

            // 댓글 단 유저의 프로필 사진 클릭했을 때
            commentimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onImgClick(view, pos);
                        }
                    }
                }
            });


            // 답글 달기를 클릭했을 때
            tx_rereply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onReplyClick(view, pos);
                        }
                    }
                }
            });

            // 댓글 좋아요 버튼을 클릭했을 때
            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onLikeClick(view, pos);
                        }
                    }
                }
            });

            // 부모 댓글 메뉴를 클릭했을 때
            btn_ReMunu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onMenuClick(view, pos);
                        }
                    }
                }
            });


        }
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    // 리사이클러뷰 안에 들어갈 뷰홀더, 그리고 그 뷰 홀더에 들어갈 아이템을 지정한다.
    public class recommentHolder extends RecyclerView.ViewHolder {

        ImageView recomment_img; // 작성한 유저의 프로필
        TextView recomment_nick , recomment_content , recomment_date , recomment_likecnt; // 작성한 유저의 닉네임, 댓글 내용, 작성시간, 댓글 좋아요 수
        ImageButton btn_ReReMenu; // 대댓글 메뉴
        ToggleButton recomment_like; // 대댓글 좋아요

        public recommentHolder(@NonNull View itemView) {
            super(itemView);
            // 뷰홀더 셋팅
            //뷰 객체에 대한 참조
            // 부모 아이템 영역

            this.recomment_img = itemView.findViewById(R.id.recomment_img); // 유저 프로필
            this.recomment_nick = itemView.findViewById(R.id.recomment_nick); // 유저 닉네임
            this.recomment_content = itemView.findViewById(R.id.recomment_content); // 댓글 내용
            this.recomment_date = itemView.findViewById(R.id.recomment_date); // 작성 시간
            this.recomment_likecnt = itemView.findViewById(R.id.recomment_likecnt); // 좋아요 수
            this.btn_ReReMenu = itemView.findViewById(R.id.btn_ReReMenu);// 대댓글 메뉴
            this.recomment_like = itemView.findViewById(R.id.recomment_like); //댓글 좋아요 버튼

            // 대댓글 단 유저의 프로필 사진 클릭했을 때
            recomment_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onImgClick(view, pos);
                        }
                    }
                }
            });


            // 댓글 좋아요 버튼을 클릭했을 때
            recomment_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onLikeClick(view, pos);
                        }
                    }
                }
            });

            // 자식 댓글 메뉴 클릭했을 때
            btn_ReReMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(cListener != null)
                        {
                            cListener.onMenuClick(view, pos);
                        }
                    }
                }
            });


        }
    }

    // 댓글 삭제 됐을 때 뷰홀더 생성 해줘야 함.
    public class deleteHolder extends RecyclerView.ViewHolder{

        ImageView del_img; // 기본이미지
        TextView del_nick, del_comment; // 닉네임, 댓글 내용 삭제

        public deleteHolder(@NonNull View itemView) {
            super(itemView);

            this.del_img = itemView.findViewById(R.id.del_img); // 기본 프로필
            this.del_nick = itemView.findViewById(R.id.del_nick);// 삭제 닉네임
            this.del_comment = itemView.findViewById(R.id.del_comment);// 삭제 내용.
        }
    }

}
