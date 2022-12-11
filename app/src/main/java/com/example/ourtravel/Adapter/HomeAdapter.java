package com.example.ourtravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.HomeData;
import com.example.ourtravel.home.RcompanyActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final ArrayList<HomeData> homeDataArrayList;
    private Context context;
    int getContentsNum;
    int pos;


    private final String TAG = this.getClass().getSimpleName();

    public HomeAdapter(ArrayList<HomeData> homeDataArrayList, Context context) {
        this.homeDataArrayList = homeDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context hcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) hcontext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        // layoutinflater는 xml에 미리 정의해둔 툴을 실제 메모리에 올려준다. inflater는 부풀리다라는 의미

        View view = inflater.inflate (R.layout.companyitem, parent, false);
        // Xml에 덩의된 resource를 view 객체로 반환해주는 역할을 한다.
        // 레이아웃정의부에서 실행되고 메모리 로딩이 돼 화면에 띄워주는 역할.


        //false는 바로 인플레이션 하지 x, true는 바로 인플에이션 한다.
        HomeAdapter.ViewHolder hvh = new HomeAdapter.ViewHolder(view);
        // 뷰홀더 생성 , 리턴값이 뷰홀더이다.
        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        HomeData home = homeDataArrayList.get(position);
        //사용자 데이터 리스트의 position 값을 가져와라.
        //그래야 사용자데이터리스트의 positon에 해당하는 데이터를 바인딩해줄수있기때문에.

        String finish = home.getTx_finish();
        String start = home.getTx_start();
        int completion = home.getCompletion();
        holder.tx_title.setText(home.getTx_title());
        holder.tx_start.setText(home.getTx_start());
        holder.tx_finish.setText(home.getTx_finish());
        holder.tx_main.setText(home.getTx_main());
        holder.tx_sub.setText(home.getTx_sub());
        holder.tx_attend.setText(home.getAttend());
        holder.tx_people.setText(home.getTx_company());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date currentTime = new Date();
        String date = format.format(currentTime);
        System.out.println("date = "+date);
        System.out.println("completion =" + completion);

        try {
            Date toDate = format.parse(date);
            Date endDate = format.parse(finish);
            Date startDate = format.parse(start);

            int compare = endDate.compareTo(toDate);
            int start_compare = startDate.compareTo(toDate);

            System.out.println("compare:"+ compare);
            System.out.println("start_compare:" + start_compare);
            System.out.println("start :" + start);
            System.out.println("finish :" + finish);



            if(compare >= 0) {
                System.out.println("compare = "+compare);
                System.out.println("동행 모집 기간 유효");
                holder.tx_condition.setText("  동행 모집 중  ");

                if(completion == 1)
                {
                    holder.tx_condition.setText("  동행 모집 완료  ");
                    holder.tx_condition.setBackgroundColor(Color.parseColor("#71CF4F"));
                }
                else{

                    if(start_compare >=0 )
                    {

                    }
                    else{
                        // 동행 시작 날짜보다 현재 날짜가 더 지났다.
                        // 동행 중이라는 뜻
                        holder.tx_condition.setText("  동행 중  ");
                        holder.tx_condition.setBackgroundColor(Color.parseColor("#FFFB8C00"));
                    }

                }





            } else {
                System.out.println("compare = "+compare);
                System.out.println("동행 모집 기간 완료");
                holder.tx_condition.setText("  동행 종료  ");
                holder.tx_condition.setBackgroundColor(Color.parseColor("#343535"));

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String imageUrl = home.getImg();
        Log.e("TAG", "onBindViewHolder: " + imageUrl );

        if(imageUrl.equals("basic"))
        {
            holder.img.setImageResource(R.drawable.img); // 옆에다가 주석처리.
        }
        else{

            String url = "http://3.39.168.165/companyimg/" + imageUrl;

            Glide.with(holder.itemView.getContext()).load(url).apply(new RequestOptions().centerCrop()).into(holder.img);
        }

        context = holder.itemView.getContext();


    }

    @Override
    public int getItemCount() {
        return homeDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       public TextView tx_title, tx_start , tx_finish, tx_main , tx_sub,tx_attend,tx_people,tx_condition ;
       public ImageView img;
        public ViewHolder(@NonNull View view) {
            super(view);

            this.tx_title = itemView.findViewById(R.id.tx_title); // 동행글 제목
            this.tx_start = itemView.findViewById(R.id.tx_start); // 동행 시작 날짜
            this.tx_finish = itemView.findViewById(R.id.tx_finish); // 동행 마지막 날짜
            this.tx_main = itemView.findViewById(R.id.tx_main); // 동행 메인 장소
            this.tx_sub = itemView.findViewById(R.id.tx_sub); // 동행 서브 장소
            this.tx_attend = itemView.findViewById(R.id.tx_attend); // 현재 동행 참여 인원
            this.tx_people = itemView.findViewById(R.id.tx_people); // 동행 모집 인원
            this.img = itemView.findViewById(R.id.img); // 게시글 배너 사진
            this.tx_condition = itemView.findViewById(R.id.tx_condition); // 동행글 모집 중

            view.setClickable(true);
            // 어댑터를 통해 만들어진 각 아이템 뷰는 " 뷰홀더" 객체에 저장되어 화면에 표시되고 필요에 따라
            // 생성 또는 재활용 된다.
            // 아이템 뷰에서 클릭 이벤트를 직접 처리하고 아이템 뷰는 뷰홀더 객체가 가지고있으니
            // 아이템 클릭 이벤트는 뷰홀더에서 작성하면 된다.
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 클릭 이벤트가 발생된 아이템 위치를 알아내야 한다.

                    pos = getAdapterPosition();
                    // getAdaperPosition() 메서드가 리턴하는 값은 어댑터 내 아이템의 위치이지만 , 리턴값이 no position인지 검사해줘야한다.
                    // notifydatasetchanged() 에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서 뷰홀더가 참조하는 아이템이
                    // 어댑터에서 삭제되면 getadapterposition() 메서드는 no_position을 리턴하지 때문이다.

                    if(pos != RecyclerView.NO_POSITION)
                    {
                            // 클릭 이벤트가 발생한 아이템 위치를 알아냈으니 어댑터가 참조하고 있는 데이터 리스트로 부터 데이터를 가져오면 된다.

                            HomeData item = homeDataArrayList.get(pos);
                            getContentsNum = item.getId();
                            String title = item.getTx_title();
                            String user_email = item.getUser_email();


                            Toast.makeText(context, "["+title+"] 동행글을 선택했습니다. ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, RcompanyActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent.putExtra("id", getContentsNum);
                            intent.putExtra("user_email", user_email);

                            context.startActivity(intent);
                    }
                        // 이건 어댑터 범주 안에서만 유효한 방법임
                        // 액티비티나 프래그먼트에서 클릭이벤트를 처리하고 싶을때가 있음 그럴때는 어댑터에 직접 리스너 인터페이스를 정의한 다음
                        // 액티비티나 프래그먼트에서 리스너 객체를 생성하고 어탭터에 전달해 호출되도록 만드는 것이다. 보통 커스텀 리스너라고 부른다.
                }
            });

        }
    }
}
