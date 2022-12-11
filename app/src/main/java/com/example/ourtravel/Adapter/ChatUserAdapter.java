package com.example.ourtravel.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.ChatUser;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.ArrayList;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {


    // 데이터 리스트 객체 생성
    private final ArrayList<ChatUser> chatUsers;
    private Context context;
    int pos;

    // 현재 로그인한 회원 정보 담고있는 shared 선언
    private PreferenceHelper preferenceHelper;
    String now_user_email; // 현재 로그인한 유저의 이메일
    // shared 에서 현재 로그인한 유저의 이메일 가져오기


    // TAG 이름 설정
    private final String TAG = this.getClass().getSimpleName();


    public ChatUserAdapter(ArrayList<ChatUser> chatUsers, Context context) {
        Log.e(TAG, "ChatUserAdapter: 생성자 ");
        Log.e(TAG, "ChatUserAdapter: chatUsers size :" + chatUsers.size() );
        this.chatUsers = chatUsers;
        this.context = context;
    }

    //-----------------------Custom Click Listener----------------------------

    public interface OnItemClickListener{
        void onImgClick(View v, int pos); // 이미지 클릭해서 해당 유저의 프로필로 이동
        void onKickOutClick(View v, int pos); // 유저 내쫒기위함.
    }

    // 커스텀 리스너 인터페이스 정의
    private ChatUserAdapter.OnItemClickListener clistener = null;
    // 전달된 객체를 저장할 변수 dListener 추가

    public void setOnItemClickListener(ChatUserAdapter.OnItemClickListener listener)
    {
        this.clistener = listener;
    }
    // 커스텀 리스너 객체를 전달하는 메서드

    //--------------------Custom Click Listener-----------------------


    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 chat user onCreateViewHolder 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.party_list, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        ChatUserAdapter.ViewHolder Lvh = new ChatUserAdapter.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return Lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {

        ChatUser chatUser = chatUsers.get(position);

        String profile = chatUser.getProfile();
        String nick = chatUser.getNick();
        String manager = chatUser.getManager();// 채팅방의 매니저 이메일
        String user_email = chatUser.getUser_email(); // 채팅방에 있는 유저 이메일

        // 유저 프로필 셋팅
        if(profile.equals("basic"))
        {
            // 기본 이미지일 경우 채팅방 이미지 기본사진으로 세팅해주기
            holder.img_chatuser.setImageResource(R.drawable.profile2);
        }
        else
        {
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.39.168.165/userimg/" + profile;

            // glide 라이브러리 통해서 해당 뷰홀더의 아이템뷰에 url 연결해줘서 이미지세팅해주기
            Glide.with(holder.itemView.getContext()).load(url).into(holder.img_chatuser);
        }

        // 유저 닉네임 셋팅
        holder.tx_usernick.setText(nick);

        preferenceHelper = new PreferenceHelper(context.getApplicationContext());
        now_user_email = preferenceHelper.getEmail(); // 현재 로그인한 유저의 이멜 = 아이디

        Log.e(TAG, "onBindViewHolder: now user email = "+now_user_email );

        // 현재 로그인한 유저가 매니저라면 다른 유저들을 내쫓을 수 있도록 다른 참여 인원 이름 옆에
        // 엑스 표가 있어야 한다.
        // 근데 현재 로그인한 유저가 매니저가 아니라면 다른 유저들을 내쫒을수 없다.
        // 매니저 권한
        if(manager.equals(now_user_email))
        {
            // 현재 로그인한 사람이 매니저라면
            if(user_email.equals(manager))
            {
                holder.img_star.setImageResource(R.drawable.ic_baseline_stars_24);
            }
            else
            {
                holder.img_star.setImageResource(R.drawable.ic_baseline_cancel_2433);
            }
        }
        else{
            // 현재 로그인한 사람이 매니저가 아니라면

            if(user_email.equals(manager))
            {
                holder.img_star.setImageResource(R.drawable.ic_baseline_stars_24);
            }
            else
            {
                holder.img_star.setVisibility(View.GONE);
            }

        }



    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_chatuser,img_star;
        TextView tx_usernick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.img_chatuser = itemView.findViewById(R.id.img_chatuser);
            this.tx_usernick = itemView.findViewById(R.id.tx_usernick);
            this.img_star = itemView.findViewById(R.id.img_star);

            // 유저 이미지를 클릭했을 때 해당 유저의 프로필로 이동해야 한다.
            img_chatuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(clistener != null)
                        {
                            clistener.onImgClick(view, pos);
                        }
                    }
                }
            });

            img_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(clistener != null)
                        {
                            clistener.onKickOutClick(view, pos);
                        }
                    }
                }
            });

        }
    }
}
