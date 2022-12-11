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

// 내가 보낸 친구 요청
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private final ArrayList<OngoingData> ongoingDataArrayList;
    private Context context;

    public FriendRequestAdapter(ArrayList<OngoingData> ongoingDataArrayList, Context context) {
        this.ongoingDataArrayList = ongoingDataArrayList;
        this.context = context;
    }

    //--------------------Custom Click Listener-----------------------
    public interface OnItemClickListener{
        void onCancleClick(View v, int pos);

    }
    // 커스텀 리스너 인터페이스 정의

    private FriendRequestAdapter.OnItemClickListener dListener = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemClickListener(FriendRequestAdapter.OnItemClickListener listener)
    {
        this.dListener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //--------------------Custom Click Listener-----------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context hcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) hcontext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate (R.layout.friend_request_item, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        FriendRequestAdapter.ViewHolder hvh = new FriendRequestAdapter.ViewHolder(view);
        // 뷰홀더 생성 , 리턴값이 뷰홀더이다.
        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        ImageButton btn_cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_nick = itemView.findViewById(R.id.tx_nick);
            this.profile_img = itemView.findViewById(R.id.profile_img);
            this.btn_cancel = itemView.findViewById(R.id.btn_cancel);

            // 친구 요청 취소 버튼을 눌렀을 때
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
