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
import com.example.ourtravel.retrofit.ChatData;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //데이터 리스트 객체 생성
    private final ArrayList<ChatData> chatDataArrayList;
    private Context context;

    private final String TAG = this.getClass().getSimpleName();

    // 어댑터 생성자 생성 -> 리사이클러뷰 데이터와 context를 매개변수로 함.
    public ChatAdapter(ArrayList<ChatData> chatDataArrayList, Context context) {
        this.chatDataArrayList = chatDataArrayList;
        this.context = context;
    }

    // viewtype 형태의 아이템 뷰를 위한 뷰 홀더 객체 생성하는 부분.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        Log.d(TAG, "태그 onCreateViewHolder 들어옴");
        View view;
        Context Ccontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) Ccontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        // 발신 했을 때(text)
        if(viewType == ChatViewType.right)
        {
            view = inflater.inflate(R.layout.chat_right, parent, false);
            // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
            // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.
            return new rightHolder(view);
        }
        // 수신 했을 때(text)
        else if(viewType == ChatViewType.left)
        {
            view = inflater.inflate(R.layout.chat_left, parent, false);
            return new leftHolder(view);
            //ChatViewType.left 인 경우
        }
        // admin 계정이 보내는 text
        else if(viewType == ChatViewType.date)
        {
            view = inflater.inflate(R.layout.chat_center, parent, false);
            return new centerHolder(view);
        }
        // 수신 받은 사진 메세지
        else if(viewType == ChatViewType.left_photo)
        {
            view = inflater.inflate(R.layout.chat_photo_left, parent, false);
            return new photoholder(view);
        }
        // 발신 하는 사진 메세지
        else{
            view = inflater.inflate(R.layout.chat_photo_right, parent, false);
            return new rightphotoholder(view);
        }
    }

    //실제 각 뷰 홀더에 데이터를 연결해주는 메서드
    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시하는 부분.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
             ChatData chat = chatDataArrayList.get(position);
             String pre_time = null; // 바로 전 메세지 시간
//
//             if(position == 0)
//             {
//
//             }else
//             {
//                 ChatData pre_chat = chatDataArrayList.get(position-1);
//
//                 pre_time = pre_chat.getChat_time(); // 전에 보낸 메세지내용
//                 Log.e(TAG, "onBindViewHolder: pre_time:" + pre_time );
//             }
//
             String profile = chat.getProfile(); // 채팅 보낸 사람의 프로필
             String chat_message = chat.getChat_message(); // 채팅 내용
             String chat_time = chat.getChat_time(); // 채팅 보낸 시간
             String nick = chat.getNick(); // 채팅 보낸사람 닉네임
             String chat_date = chat.getChat_date(); // 채팅 보낸 최초시간 , 년웡일
             String photo_url; // 사진 url


             // 발신자 뷰홀더
        if(holder instanceof rightHolder)
        {
            // 채팅을 보낸 사람과 현재 채팅방에 들어온 사람 즉 로그인한 유저가 같은 사람이면 발신자 뷰홀더로 되어야 한다.
            ((rightHolder)holder).tx_send.setText("   "+chat_message+"   "); // 발신 메세지

            Log.e(TAG, "onBindViewHolder inside right holder : pre_time = " + pre_time );


            ((rightHolder)holder).tx_sendtime.setText(chat_time); // 발신 시간

        }
        else if (holder instanceof leftHolder){ // 수신자 뷰홀더

            // 수신자 프로필 세팅
            if(chat.getProfile().equals("basic"))
            {
                // 기본 이미지일 경우 기본 프로필 이미지로 세팅해주기
                ((leftHolder)holder).img_profile.setImageResource(R.drawable.profile2);
            }
            else
            {
                // 경로는 userimg로 설정해줘야 함.
                String url = "http://3.39.168.165/userimg/" + profile;
                Glide.with(holder.itemView.getContext()).load(url).into(((leftHolder)holder).img_profile);
            }

            ((leftHolder)holder).tx_nick.setText(nick); // 수신자 닉네임
            ((leftHolder)holder).tx_receive.setText("    "+chat_message+"    "); // 수신 메세지 내용
            ((leftHolder)holder).tx_receivetime.setText(chat_time); // 수신한 메세지 시간
        }
        // admin 계정 뷰 홀더
        else if(holder instanceof centerHolder)
        {
            // 채팅 최초시간 - holder instanceof centerHolder
            ((centerHolder)holder).tx_date.setText(chat_message); // 채팅보낸 최초 시간 , 년월일

        }
        // 수신받은 이미지 뷰홀더
        else if(holder instanceof  photoholder)
        {
            // 수신자 프로필 세팅
            if(chat.getProfile().equals("basic"))
            {
                // 기본 이미지일 경우 기본 프로필 이미지로 세팅해주기
                ((photoholder)holder).img_profile.setImageResource(R.drawable.profile2);
            }
            else
            {
                // 경로는 userimg로 설정해줘야 함.
                String url = "http://3.39.168.165/userimg/" + profile;
                Glide.with(holder.itemView.getContext()).load(url).into(((photoholder)holder).img_profile);
            }

            ((photoholder)holder).tx_nick.setText(nick); // 수신자 닉네임
            // 사진을 받을 변수가 있어야 하는데 chatdata dto에 사진을 받을 string이 없다.
            // content에 사진 이름 들어올 것. -> content는 chat_message이다.

            photo_url = "http://3.39.168.165/chatimg/" + chat_message;
            Glide.with(holder.itemView.getContext()).load(photo_url).into(((photoholder)holder).img_photo);

            ((photoholder)holder).tx_receivetime.setText(chat_time); // 수신한 메세지 시간
        }
        else // 발신한 이미지 뷰홀더
        {
            photo_url = "http://3.39.168.165/chatimg/" + chat_message;
            Glide.with(holder.itemView.getContext()).load(photo_url).into(((rightphotoholder)holder).img_photo);
            ((rightphotoholder)holder).tx_sendtime.setText(chat_time);
        }
    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatDataArrayList.get(position).getViewType();
    }

    // 발신 메세지 뷰홀더 지정
    public class rightHolder extends  RecyclerView.ViewHolder{

        TextView tx_sendtime, tx_send; // 발신 시간, 발신 메세지

        public rightHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_sendtime = itemView.findViewById(R.id.tx_sendtime); // 메시지 발신한 시간
            this.tx_send =itemView.findViewById(R.id.tx_send); // 메시지 발신 내용
        }
    }

    // 수신 메세지 뷰홀더 지정.
    public class leftHolder extends  RecyclerView.ViewHolder{

        ImageView img_profile; // 수신자 프로필
        TextView tx_nick, tx_receive, tx_receivetime; // 수신자 닉네임, 수신한 내용, 수신한 시간

        public leftHolder(@NonNull View itemView) {
            super(itemView);

            this.img_profile = itemView.findViewById(R.id.img_profile); // 수신자 프로필
            this.tx_nick = itemView.findViewById(R.id.tx_nick); // 수신자 닉네임
            this.tx_receive = itemView.findViewById(R.id.tx_receive); // 수신한 내용
            this.tx_receivetime = itemView.findViewById(R.id.tx_receivetime); // 수신한 시간
        }
    }

    // 숫자 가운데 holder
    public static class centerHolder extends  RecyclerView.ViewHolder{

        TextView tx_date; // 채팅 최초 시간

        public centerHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_date = itemView.findViewById(R.id.tx_date); // 채팅 최초 시간
        }
    }

    // 사진 holder
    public static class photoholder extends  RecyclerView.ViewHolder{

        ImageView img_profile, img_photo; // 수신자 프로필, 사진
        TextView tx_nick, tx_receivetime; // 수신자 닉네임, 수신한 내용, 수신한 시간

        public photoholder(@NonNull View itemView) {
            super(itemView);

            this.img_profile = itemView.findViewById(R.id.img_profile); // 수신자 프로필
            this.tx_nick = itemView.findViewById(R.id.tx_nick); // 수신자 닉네임
            this.img_photo = itemView.findViewById(R.id.img_photo); // 수신한 사진
            this.tx_receivetime = itemView.findViewById(R.id.tx_receivetime); // 수신한 시간
        }
    }

    // 사진 holder
    public static class rightphotoholder extends  RecyclerView.ViewHolder{

        ImageView img_photo; // 수신자 프로필, 사진
        TextView tx_sendtime; // 수신자 닉네임, 수신한 내용, 수신한 시간

        public rightphotoholder(@NonNull View itemView) {
            super(itemView);


            this.img_photo = itemView.findViewById(R.id.img_photo); // 수신한 사진
            this.tx_sendtime = itemView.findViewById(R.id.tx_sendtime); // 수신한 시간
        }
    }
}
