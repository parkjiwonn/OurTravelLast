package com.example.ourtravel.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.ourtravel.Chat.Chat_Activity;
import com.example.ourtravel.retrofit.ChatRoomData;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    // 데이터 리스트 객체 생성
    private final ArrayList<ChatRoomData> chatRoomDataArrayList;
    private Context context;

    // TAG 이름 설정
    private final String TAG = this.getClass().getSimpleName();

    // 어댑터 생성자 생성
    public ChatListAdapter(ArrayList<ChatRoomData> chatRoomDataArrayList, Context context) {
        this.chatRoomDataArrayList = chatRoomDataArrayList;
        this.context = context;
    }

    // viewtype 형태의 아이템 뷰를 위한 뷰 홀더 객체 생성하는 부분.
    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context dcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) dcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate(R.layout.chat_list, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        ChatListAdapter.ViewHolder cvh = new ChatListAdapter.ViewHolder(view);
        // 뷰홀더 생성되는 부분

        return cvh;
    }

    //실제 각 뷰 홀더에 데이터를 연결해주는 메서드
    //position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시하는 부분.
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {

        ChatRoomData chatRoomData = chatRoomDataArrayList.get(position);
        // 레트로핏 통신시 받을 DTO

        //사용자 데이터 리스트의 position 값을 가져와라.
        //그래야 사용자데이터리스트의 positon에 해당하는 데이터를 바인딩해줄수있기때문에.

        // 채팅방 이미지 바인딩
        String imageUrl = chatRoomData.getPhoto();
        String lastmsg = chatRoomData.getLast_msg(); // 채팅방 마지막 메세지
        int participant = chatRoomData.getParticipant(); // 채팅방 참여 인원

        if(imageUrl.equals("basic"))
        {
            // 기본 이미지일 경우 채팅방 이미지 기본사진으로 세팅해주기
            holder.img_chat.setImageResource(R.drawable.img);
        }
        else
        {
            // 경로는 userimg로 설정해줘야 함.
            String url = "http://3.39.168.165/companyimg/" + imageUrl;

            // glide 라이브러리 통해서 해당 뷰홀더의 아이템뷰에 url 연결해줘서 이미지세팅해주기
            Glide.with(holder.itemView.getContext()).load(url).into(holder.img_chat);
        }

        // 채팅방 제목 바인딩
        holder.tx_title.setText(chatRoomData.getRoom_name());
        // 채팅방 마지막 메시지 바인딩

        if(lastmsg.equals("img"))
        {
            holder.tx_message.setText("사진을 보냈습니다.");
        }
        else{
            holder.tx_message.setText(chatRoomData.getLast_msg());
        }


        // 채팅방 참여 인원 바인딩
        holder.tx_participant.setText(String.valueOf(participant));
        // 채팅방 전체 모집 인원 바인딩
        holder.tx_people.setText(String.valueOf(chatRoomData.getPeople()));
        // 채팅방 마지막 메세지 전송 시간)
        holder.tx_lasttime.setText(chatRoomData.getLast_msgtime());


    }

    // 전체 아이템 갯수 리턴
    @Override
    public int getItemCount() {
        // 리사이클러뷰에 셋팅될 아이템들의 갯수
        return chatRoomDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_chat;
        TextView tx_title, tx_message, tx_participant, tx_people, tx_lasttime;

        public ViewHolder(@NonNull View view) {
            super(view);

            this.img_chat = itemView.findViewById(R.id.img_chat); // 채팅방 이미지
            this.tx_title = itemView.findViewById(R.id.tx_title); // 채팅방 제목
            this.tx_message = itemView.findViewById(R.id.tx_message); // 체팅방 마지막 메세지
            this.tx_participant = itemView.findViewById(R.id.tx_participant); // 채팅방 참여인원
            this.tx_people = itemView.findViewById(R.id.tx_people); // 채팅방 전체 모집 인원
            this.tx_lasttime = itemView.findViewById(R.id.tx_lasttime); // 마지막 메세지 전송 시간

            // 아이템 클릭 리스너
            // 채팅방 선택
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition(); // 각 아이템의 포지션값

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        // 리사이클러뷰가 갖고 있는 데이터에서 포지션값에 해당하는 값들을 뽑아오기.
                        ChatRoomData item = chatRoomDataArrayList.get(pos);

                        int roomNum = item.getRoom_num(); // 채팅방 번호
                        String made_time = item.getMade_time(); // 채팅방 생성 날짜
                        String manager = item.getManager(); // 채팅방 방장
                        int people = item.getPeople(); // 채팅방 총 인원
                        String room_name = item.getRoom_name(); // 채팅방 이름
                        // 참여 인원도 같이 보내줘야 함.

                        // 채팅방 액티비티로 이동시키기
                        Intent intent = new Intent(context, Chat_Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("roomNum", roomNum); // 채팅방 번호
                        context.startActivity(intent);

                    }
                }
            });
        }


    }
}
