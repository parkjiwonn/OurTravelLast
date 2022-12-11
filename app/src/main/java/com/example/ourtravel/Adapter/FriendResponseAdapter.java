package com.example.ourtravel.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.OngoingData;

import java.util.ArrayList;

public class FriendResponseAdapter extends RecyclerView.Adapter<FriendResponseAdapter.ViewHolder>{


    // 데이터 리스트 객체 생성
    private final ArrayList<OngoingData> ongoingDataArrayList;
    private Context context;


    private final String TAG = this.getClass().getSimpleName();

    public FriendResponseAdapter(ArrayList<OngoingData> ongoingDataArrayList, Context context) {
        this.ongoingDataArrayList = ongoingDataArrayList;
        this.context = context;
    }

    //--------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onAcceptClick(View v, int pos);
        void onCancleClick(View v, int pos);

    }
    // 커스텀 리스너 인터페이스 정의

    private FriendResponseAdapter.OnItemClickListener dListener = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemClickListener(FriendResponseAdapter.OnItemClickListener listener)
    {
        this.dListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //--------------------Custom Click Listener-----------------------



    @NonNull
    @Override
    public FriendResponseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.friend_response_item, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        FriendResponseAdapter.ViewHolder dvh = new FriendResponseAdapter.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendResponseAdapter.ViewHolder holder, int position) {
        OngoingData item = ongoingDataArrayList.get(position);

        String img_url = item.getProfile();
        String user_nick =item.getNick();

        // 유저 닉네임, 사진 url 받아서
        // 기본 사진인지 아닌지 분기처리해서 아이템 세팅해주기.

        // 유저 프로필 사진
        if(img_url.equals("basic"))
        {
            holder.profile_img.setImageResource(R.drawable.profile2);

        }
        else
        {
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.39.168.165/userimg/" + img_url;

            Glide.with(holder.itemView.getContext()).load(url).into(holder.profile_img);
        }

        // 유저 닉넴
        holder.tx_nick.setText(user_nick);
    }

    @Override
    public int getItemCount() {
        return ongoingDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tx_nick;
        ImageView profile_img;
        ImageButton btn_accept, btn_cancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_nick = itemView.findViewById(R.id.tx_nick);
            this.profile_img = itemView.findViewById(R.id.profile_img);
            this.btn_accept = itemView.findViewById(R.id.btn_accept);
            this.btn_cancel = itemView.findViewById(R.id.btn_cancel);

            // 친구 요청 수락 버튼을 눌렀을 때
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onAcceptClick(view, pos);
                        }
                    }
                }
            });

            // 친구 요청 거절 버튼을 눌렀을 때
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(dListener != null)
                        {
                            dListener.onCancleClick(view, pos);
                        }
                    }
                }
            });
        }
    }
}
