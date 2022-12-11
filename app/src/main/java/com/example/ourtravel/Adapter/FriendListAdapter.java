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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.FriendData;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{

    // 데이터 리스트 객체 생성
    private final ArrayList<FriendData> friendDataArrayList;
    private Context context;


    private final String TAG = this.getClass().getSimpleName();

    public FriendListAdapter(ArrayList<FriendData> friendDataArrayList, Context context) {
        this.friendDataArrayList = friendDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context hcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) hcontext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate (R.layout.friend_list_item, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        FriendListAdapter.ViewHolder hvh = new FriendListAdapter.ViewHolder(view);
        // 뷰홀더 생성 , 리턴값이 뷰홀더이다.
        return hvh;


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendData  item = friendDataArrayList.get(position);

        String img_url = item.getProfile();
        String user_nick =item.getNick();
        int border = item.getViewtype(); // viewtype =0 이면 그냥 이웃, = 1 이면 서로이웃

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

        // 그냥 이웃이면
        if(border == 0)
        {
           holder.btn_neighbor.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_2422));
        }
        else{
            // 서로이웃이면
            holder.btn_neighbor.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_people_24));
        }

        // 유저 닉넴
        holder.tx_nick.setText(user_nick);

    }

    @Override
    public int getItemCount() {
        return friendDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tx_nick;
        ImageView profile_img;
        ImageButton btn_neighbor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_nick = itemView.findViewById(R.id.tx_nick);
            this.profile_img = itemView.findViewById(R.id.profile_img);
            this.btn_neighbor = itemView.findViewById(R.id.btn_neighbor);

        }
    }
}
