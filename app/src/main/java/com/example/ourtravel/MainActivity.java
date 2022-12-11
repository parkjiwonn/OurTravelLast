package com.example.ourtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.ourtravel.Service.MyService;
import com.example.ourtravel.home.HomeActivity;
import com.example.ourtravel.retrofit.RetrofitClient;
import com.example.ourtravel.retrofit.RetrofitInterface;
import com.example.ourtravel.userinfo.LoginActivity;
import com.example.ourtravel.userinfo.PreferenceHelper;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    final String TAG = getClass().getName();
    private PreferenceHelper preferenceHelper;

    // 레트로핏
    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    private MyService myService;
    Context mContext;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: 들어옴" );

        mContext = MainActivity.this;

        preferenceHelper = new PreferenceHelper(this);

        // 레트로핏 객체 생성
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 500);

    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: 들어옴" );
        super.onStart();

        // myservice로 인텐트 보냄
        Intent intent = new Intent(this, MyService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);//service 클래스에서 onbind 호출 됨.
        // bindService를 호출하면 클라이언트가 서비스에 바인딩되도록 할 수 있다.
        // bindService 호출해 ServiceConnection 구현을 전달한다.

        // bindService의 첫번째 매개변수는 바인딩할 서비스의 이름을 명시적으로 지정하는 intent
        // 두번째 매개변수는 ServiceConnection 객체이다.
        // 세번째 매개변순는 바인딩 옵션을 나타내는 플래그 이다.
        // 일반적으로 BIND_AUTO_CREATE가 되는데 이는 서비스가 아직 활성화되지 않았을 경우
        // 서비스를 생성하기 위함이다.

        //  활동이 표시되는 동안에만 서비스와 상호작용해야 할 경우, onStart() 중에는 바인딩하고 onStop() 중에는 바인딩을 해제해야 합니다.
        //백그라운드에서 활동이 중단되었을 때도 응답을 받게 하고 싶을 경우, onCreate() 중에는 바인딩하고 onDestroy() 중에는 바인딩을 해제하면 됩니다.

        // 서비스를 실행하려는 곳(Activity)에서 startService를 써서 호출한다.
        startService(MyService.class, mConnection,null); // onstart메서드에서 서비스 자동 실행 처리할 목적이다.

    }


    // 클라이언트가 bindService()를 호출하여 서비스에 바인딩되고 이때 서비스와의 연결을 모니터링하는
    // ServiceConnection의 구현을 반드시 제공해야 한다.
    // 서비스 바인딩을 위한 콜백 메서드를 정의하는 곳.
    // 즉 클라이언트가 serviceConnection 구현과 onServiceConnected() 콜백을 사용하여 서비스에 바인딩되는
    // 방법을 보여준다.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Android 시스템이 클라이언트와 서비스 사이에 연결을 생성하면 ServiceConnection에서
        // onServiceConnected를 호출한다.
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) { // IBinder 인수 포함, 클라이언트는 이를 통해 바인드된 서비스와 통신합니다.
            Log.e(TAG, "onServiceConnected: 들어옴" );
            // 서비스와 연결되었을때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장한다.
            MyService.MyBinder binder = (MyService.MyBinder)service;
            myService = binder.getService(); // 서비스가 제공하는 메서드 호출하여 서비스 쪽 객체를 전달 받을 수 있음.
            mBound = true;

            // 시스템이 이를 호출하여 서비스의 onBind() 메서드가 반환한 IBinder를 전달한다.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            // 예기치 않은 종료(안드로이드 OS에 의한 종료)
            Log.e(TAG, "onServiceDisconnected: 들어옴" );
            // Android 시스템은 서비스로의 연결이 예기치 못하게 끊어졌을 경우(서비스가 비정상 종료되거나
            // 중단되었을때) 이를 호출한다. 클라이언트가 바인딩을 해제할 때는 호출되지 않는다.
            mBound = false;

        }
    };

    private class splashhandler implements Runnable{
        @Override
        public void run() {

            String userId = preferenceHelper.getEmail();
            String userNick = preferenceHelper.getNick();

            if(userId != "" && userNick != "")
            {
                Log.e(TAG, "run: homeactivity" );
                startActivity(new Intent(getApplication(), HomeActivity.class));
                MainActivity.this.finish();
            }else{
                Log.e(TAG, "run: loginactivity" );
                startActivity(new Intent(getApplication(), LoginActivity.class));
                MainActivity.this.finish();

            }


        }
    }

//    @Override
//    protected void onDestroy() {
//        Log.e(TAG,"MainActivity onDestroy");
//        if(mBound){
//            unbindService(mConnection);
//            mBound = false;
//        }
//        stopService(new Intent(this, MyService.class));
//
//        super.onDestroy();
//    }

    // 한 구성 요소 (activicy에서) 에서 startService를 써서 서비스를 호출하면
    // onStartCommand가 호출되고 서비스가 중단되기 전까지는 서비스를 실행 중인 상태로 유지된다.
    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras){
        // onStart 메소드에서 서비스 자동 실행 처리할 목적

        if(!MyService.SERVICE_CONNECTED){
            // service가 연결되어 있지 않으면
            Intent startService = new Intent(this, service);
            if(extras!= null && !extras.isEmpty())
            {
                Log.e(TAG, "startService: extras가 null이 아니다. " );
                Set<String> keys = extras.keySet();
                for(String key : keys){
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Log.e(TAG, "startService: service 연결 됨" );
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

}