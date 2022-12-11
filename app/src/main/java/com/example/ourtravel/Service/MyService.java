package com.example.ourtravel.Service;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ourtravel.Chat.Chat_Activity;
import com.example.ourtravel.R;
import com.example.ourtravel.retrofit.ChatUser;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {
    // 외부로 데이터를 전달하려면 바인더 사용
    // Binder 객체는 IBinder 인터페이스 상속구현 객체입니다
    // public class Binder extends Object implements IBinder

    // 외부로 데이터를 전달하기 위해 바인더를 사용해야 한다.
    //

    private static final String TAG = MyService.class.getSimpleName();
    private Context context;

    private Thread mThread;
    public static boolean SERVICE_CONNECTED = false;
    IBinder mBinder = new MyBinder();

    private RetrofitInterface retrofitInterface;
    private PreferenceHelper preferenceHelper;
    String user_email;

    String url;

    private static String NOTIFICATION_CHANNEL_ID = "channel1";


    //=======================소켓 통신===============================
    private Handler mHandler; // 핸들러 객체 생성
    public static Socket socket;  // 소켓 객체 생성
    public static PrintWriter sendWriter; // 출력 스트림
    public static BufferedReader input;  // 입력 스트림
    private String ip = "3.39.168.165"; // 통신할 서버 ip
    private int port = 8888; // 서버 포트 지정
    String read;

    String chatdata;

    public class MyBinder extends Binder {
        public MyService getService() {
            // 서비스 객체를 리턴
            Log.e(TAG, "getService: 들어옴");
            Log.e(TAG, "MyService MyBinder return.");
            // 클라이언트가 서비스내의 공개 메서드를 호출할 수 있다.
            return MyService.this;
        }
    }

    public void sendmsg(String data) {
        Log.e(TAG, "sendmsg: data = " + data);


        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    Log.e(TAG, "run: userId :"+ now_user_email );
//                    Log.e(TAG, "run: sendmsg: "+content );
                    // userid, sendmsg 잘 찍힘.
                    Log.e(TAG, "run: sendWriter :" + sendWriter);
                    // 메세지를 보낼 때 시간과 현재의 시간을 비교해야 한다.

                    // 서버에 데이터들 json 형식으로 보내기
                    sendWriter.println(data);

                    // 사진 -> list에 넣어서 json 형식으로 보내기

                    sendWriter.flush();
                    // 채팅을 보낸 다음 et_chat이 공백이 되어야 한다.
                    //et_chat.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public MyService() {
        Log.e(TAG, "MyService: 생성자");
    }

    // 서비스에 바인딩을 제공하기 위해 onBind 콜백 메서드를 구현해야 한다.
    // 이 메서드는 클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스르 제공하는
    // IBinder 객체를 반환한다.
    // 액티비티가 서비스에 요청하면 서비스는 그에따른 결과를 반환할 수 있다.

    @Override
    public IBinder onBind(Intent intent) {
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다

        // Service 객체와 (화면단 Activity 사이에서) 데이터를 주고받을 때 사용하는 메서드
        // Activity에서 bindService() 를 실행하면 호출됨
        // 데이터를 전달할 필요가 없으면 return null;

        Log.e(TAG, "onBind: 들어옴 oncreate 되고 다음으로 실행");
        return mBinder; // 서비스 객체를 리턴

        // 리턴한 mBinder 객체는 서비스와 클라이언트 사이의 인터페이스를 정의한다.
        // 클라이언트가 mBinder 객체를 받으면 이 인터페이스를 통해 데이터를 주고받는것이 가능해진다.
        // 서비스 자신과 통신할수 있도록 만들어 준다.

    }


    // 서비스가 최소 생성될 때만 호출된다.
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: Myservice started 먼저 실행");
        this.context = this;
        MyService.SERVICE_CONNECTED = true; // service 연결이 되었는지 확인 용
        // 연결 true가 되었음.

        // onStartCommand 또는 onBind 호출하기 전에 호출된다.
        // onStartCommand 또는 onBind에서 서비스를 실행하거나 바인딩을 하고 나면
        // 서비스가 계속해서 실행되고 있는 중이 되기 때문에
        // 일회성으로 실행되며 서비스가 이미 실행중이면 onCreate()는 호출되지 않는다.

        // shared 에서 현재 로그인한 유저의 이메일 가져오기
        preferenceHelper = new PreferenceHelper(this);
        user_email = preferenceHelper.getEmail();
    }

    // 구성요소가 서비스를 시작하게 한다.
    // 서비스를 시작하도록 요청하는 메서드이다.
    // 이 메서드가 실행되면 서비스가 시작되고 백그라운드에서 무한히 실행될 수 있다.
    // startService()로 서비스를 시작할 때 호출된다.

    // 서비스가 시작되면 이를 호출한 구성요소(액티비티)와 독립적인 수명주기를 가진다.
    // 서비스는 백그라운드에서 무한히 실행 될 수 있고, 서비스를 호출한 구성요소가 소멸되어도
    // 계속 실행 될 수 있다.
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: 들어옴");
        Log.e(TAG, "MyService onStartCommand startID === " + startId); // 계속 증가되는 값


        // 소켓 통신 위한 핸들러
        mHandler = new Handler(); // 핸들러 객체 생성

        // thread 계속 돔
        if (mThread == null) {
            mThread = new Thread("My Thread") {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {

                            // 여기서 소켓 통신해주기

                            // ip 주소를 표현하기 위함.
                            InetAddress serverAddr = InetAddress.getByName(ip); // getByName : 매개변수 ip에 대응되는 inetAddress 객체를 반환한다.
                            socket = new Socket(serverAddr, port);
                            sendWriter = new PrintWriter(socket.getOutputStream()); // 보낼때 printwriter
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 입력받을 때 bufferedreader

                            // 무한 루프를 돌면서 서버에서 오는 메세지를 받기 위함.
                            while (true) {
                                // read는 string 변수. 서버에서 보낸 메세지 읽는 것.
                                read = input.readLine();
                                System.out.println("service" + read);
                                // 서버에서 전달 받은 메세지
                                // ex ) monspirit34@naver.com///hi///06 : 05 오후///95///coffee///20220819010947.jpg 로 메세지가 리턴 된다.

                                // 현재 들어온 유저가 어느 채팅방에 들어가있는지 확인해야 함.
                                retrofitInterface = RetrofitClient.getRetrofitInterface();

                                // 메세지를 받았는데 내가 속한 채팅방에서 온 채팅메세지인지 먼저 확인해야 함.
                                // 내가 어느 채팅방에 속해있는지 확인 먼저 해야 함.
                                // 그 다음 내 task 중에서 제일 최상단에 있는 액티비티가 무엇인지 먼저 파악
                                // 채팅방이면 메세지가 온 채팅방에 있는지 아닌지 확인


                                if (read != null) {
                                    // post()는 Message 객체가 아닌 Runnable 객체를 Message queue에 전달한다.
                                    Log.e(TAG, "run: 서버받은 메세지");
                                    mHandler.post(new msgUpdate(read));
                                }


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            this.interrupt();
                        }


                    }

                }
            };
            mThread.start();
        }
        return START_STICKY;
        // onStartCommand()의 return값
        // START_STICKY : 시스템이 onStartCommand() 반환 후에 서비스를 중단하면
        // 서비스를 자동으로 다시 생성하고 마지막 인텐트는 전달하지 않음.

        // 서비스가 시스템에 의해 소멸된 경우에는 서비스가 다시 시작할 여부를
        // onStartCommand의 return 값에 따라 달라질 수 있다.

        //(1) START_REDELIVER_INTENT
        //시스템이 서비스를 중단하면 서비스를 다시 생성한다.
        //그리고 이 서비스에 전달된 마지막 인텐트로 onStartCommand()를 호출한다.
        //모든 보류 인텐트가 차례로 전달된다.
        //파일 다운로드와 같은 서비스에 적합하다.

        //(2) START_STICKY
        //시스템이 서비스를 중단하면 서비스를 다시 생성한다.
        //마지막 인텐트를 전달하지 않고 null 인텐트로 onStartcommand()를 호출한다.
        //명령을 실행하진 않지만 작업을 기다리는 미디어 플레이어와 같은 서비스에 적합하다.

        //(3) START_NOT_STICKY
        //시스템이 서비스를 중단하면 서비스를 재생성하지 않는다.
        //다시 시작하려는 경우에 적합하다.

    }

    // 서비스가 소멸될 때 호출출
    @Override
    public void onDestroy() {
        Log.e(TAG, "MyService onDestroy");

        super.onDestroy();
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;

        }
        SERVICE_CONNECTED = false;

    }

    class msgUpdate implements Runnable{

        private String msg;

        private static final String TAG = "Myservice msgUpdate";


        // 생성자 -> 여기서 str은 json 형식으로 데이터 정리된 채팅 데이터
        public msgUpdate(String str) {
            Log.e(TAG, "msgUpdate: 서버에서 메세지 받음 :"+ str );
            this.msg=str;
        }


        @Override
        public void run() {

            try{
                // 서버에서 보낸 채팅 데이터는 JSON 형식의 string 값이다.
                JSONObject jsonObject = new JSONObject(msg);
                int room_num = Integer.parseInt(jsonObject.getString("roomNum")); // 문자열 -> 정수 변환 , 채팅방 숫자 표헌으로 구분
                String nick = jsonObject.getString("now_user_nick"); // 채팅 보낸 사람 닉네임
                String content = jsonObject.getString("content"); // 채팅 내용

                Log.e(TAG, "run: room_num =" + room_num );
                Log.e(TAG, "run: now_user_email =" + user_email );

                // 현재 앱 실행한 유저가 해당 채팅방에 들어가있는지 아닌지 확인 하기
                retrofitInterface.CheckUserList(room_num, user_email).enqueue(new Callback<ChatUser>() {
                    @Override
                    public void onResponse(Call<ChatUser> call, Response<ChatUser> response) {
                        ChatUser result = response.body();
                        // 참여하고 있는지 아닌지에 대한 상태값
                        String status = result.getStatus();
                        String img = result.getMessage(); // 채팅방 이미지
                        String title= result.getNick(); // 채팅방 제목
                        Log.e(TAG, "onResponse: result = " + result );
                        Log.e(TAG, "onResponse: status =" + status );
                        // 채팅방에 참여하고 있지 않으면 false, 참여하고 있으면 true

                        if(status.equals("true"))
                        {
                            // 만약 현재 앱 실행한 유저가 해당 채팅방에 참여하고 있다면
                            // 이 유저가 현재 어떤 액티비티를 보고 있는지 확인해야한다.
                            // 띄워져있는 액티비티 중 가장 위에 있는 액티비티 가져오기.
                            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            // ActivityManager 를 사용해 가장 최상단에 있는 액티비티를 String 형태로 가져온다.
                            List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                            ComponentName componentName = info.get(0).topActivity;
                            String ActivityName = componentName.getShortClassName().substring(1);

                            Log.e(TAG, "onClick: AcivityName :" + ActivityName);

                            // 1. 채팅 액티비티가 아닌 경우 / 채팅방 멤버
                            // 2. 채팅 액티비티인데 다른 채팅방인 경우 / 채팅방 멤버

                            if(ActivityName.equals("Chat.Chat_Activity"))
                            {
                                // 채팅 액티비티 이긴 한데 해당 채팅방이 아닌 경우
                                Log.e(TAG, "onResponse: 현재 채팅방에 있음" );

                                if(Chat_Activity.roomNum == room_num)
                                {
                                    // 같은 채팅방이라면
                                    Log.e(TAG, "onResponse: 같은 채팅방에 있음" );
                                }
                                else
                                {
                                    // 같은 채팅방에 없다면
                                    // 알림을 띄워줘야 한다.
                                    NotificationSomethings(img, title, nick, content,room_num);

                                }
                            }
                            else{
                                //  다른 액티비티인데 채팅방 멤버인 경우
                                //  알림을 띄워줘야 한다.
                                NotificationSomethings(img, title, nick, content,room_num);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ChatUser> call, Throwable t) {

                    }
                });


            }catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

    public void NotificationSomethings(String img, String title, String nick, String content, int roomnum){

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, Chat_Activity.class);
        notificationIntent.putExtra("roomNum", roomnum); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) //BitMap 이미지 요구
                .setContentTitle(title)
                .setContentText(nick +"  :  " +content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 채팅 액티비티로 이동하도록 설정
                .setAutoCancel(true);




        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;

        // 경로는 userimg로 설정해줘야 함.

        if(img.equals("basic"))
        {
            img = "img.webp";
            url = "http://3.39.168.165/companyimg/" + img;
        }
        else{
            url = "http://3.39.168.165/companyimg/" + img;
        }

        //채팅방 사진으로 나와야 함.
        Glide.with(getApplicationContext()).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {



                builder.setLargeIcon(resource);
                notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });



    }


}