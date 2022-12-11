package com.example.ourtravel.Weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class WeatherActivity extends AppCompatActivity {

    public static String TAG = "["+WeatherActivity.class.getSimpleName() +"] ";
    Context context = WeatherActivity.this;


    ImageView iv_weather;
    TextView tv_temp, tv_main, tv_description;
    TextView tv_wind, tv_cloud, tv_humidity,tx_announce;

    String strUrl = "";  //통신할 URL
    NetworkTask networkTask = null;
    String lat;
    String lon;
    String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        strUrl = getString(R.string.weather_url)+"data/2.5/weather";  //Strings.xml 의 weather_url 로 통신할 URL 사용

        Intent intent = getIntent();
        lat = (String)intent.getExtras().get("lat"); // 위도
        lon = (String)intent.getExtras().get("lon"); // 경도
        initView();
        requestNetwork();
    }

    /* view 를 설정하는 메소드 */
    private void initView() {

        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_main = (TextView) findViewById(R.id.tv_main);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_cloud = (TextView) findViewById(R.id.tv_cloud);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tx_announce = (TextView) findViewById(R.id.tx_announce);
    }


    /* NetworkTask 를 요청하기 위한 메소드 */
    private void requestNetwork() {
        ContentValues values = new ContentValues();
        values.put("lat", lat);
        values.put("lon", lon);
        values.put("appid", getString(R.string.weather_app_id));

        // AsyncTask 의 경우 onCreate나 onResume에서 execute를 통해 불러오면 된다.
        // 생성자가 있을 때 생성자를 통해서 파라미터 값을 넘겨주어 실행하면 된다.
        networkTask = new NetworkTask(context, strUrl, values);
        networkTask.execute();
    }


    /* 비동기 처리를 위해 AsyncTask 상속한 NetworkTask 클래스 */
    // AsyncTask<Params, Progress, Result>
    // Params : doInBackground 파라미터 타입, execute의 메서드 인자 값이 된다.
    // Progress : doInBackground 작업 시 진행 단위의 타입이며 onProgessUpdate의 파라미터 타입이다.
    // Result : doInBackground의 리턴값으로 onPostExecute 파라미터 타입이 된다.
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        Context context;
        String url = "";
        ContentValues values;

        public NetworkTask(Context context, String url, ContentValues values) {
            this.context = context;
            this.url = url;
            this.values = values;
        }

        // AsyncTask로 백그라운드 작업을 실행하기 전에 실행되는 부분
        // 이 부분에는 로딩 중 과 같은 다이얼로그를 띄우는 등 스레드 작업 이전에 수행할 동작을 구현하면 됨.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // 실질적으로 진행 할 작업을 작성하면 된다. 그 작업이 비동기로 진행 되는 것.
        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            // HttpUrlConnection 연결을 위한 클래스 객체 생성해야 함.
            RequestHttpUrlConnection requestHttpUrlConnection = new RequestHttpUrlConnection();
            // 반환 값으로 파라미터 담긴 클래스 객체 반환하기
            result = requestHttpUrlConnection.request(url, values, "GET");  //HttpURLConnection 통신 요청 - GET으로 요청방식 파라미터로 전달

            Log.d(TAG, "NetworkTask >> doInBackground() - result : " + result);
            return result;
        }

        //
        @Override
        protected void onProgressUpdate(Void... values) {
        }

        // doInBackground작업이 끝난 후 결과 파라미터 리턴 받아오면 실행되는 부분으로서, 비동기 작업이 종료된 이 후 실행될 동작을
        // 구현하면 된다.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "NetworkTask >> onPostExecute() - result : " + result);

            // 서버에서 받아온 json 형식의 날씨 결과들이 null이 아니라면 json 파싱을 해줘야 한다.
            if (result != null && !result.equals("")) {
                JsonParser jp = new JsonParser();
                JsonObject jsonObject = (JsonObject) jp.parse(result);
                // sys, weather, main, wind, clouds 로 분류되어 있는 결과들을 분리해줘야 한다.
                JsonObject jsonObjectSys = (JsonObject) jp.parse(jsonObject.get("sys").getAsJsonObject().toString());
                JsonObject jsonObjectWeather = (JsonObject) jp.parse(jsonObject.get("weather").getAsJsonArray().get(0).toString());
                JsonObject jsonObjectMain = (JsonObject) jp.parse(jsonObject.get("main").getAsJsonObject().toString());
                JsonObject jsonObjectWind = (JsonObject) jp.parse(jsonObject.get("wind").getAsJsonObject().toString());
                JsonObject jsonObjectClouds = (JsonObject) jp.parse(jsonObject.get("clouds").getAsJsonObject().toString());

                // 서버에서 dto로 결과 세팅해주기 - 클래스 weathermodel 객체 생성
                // dto - 데이터를 주고받을 포맷.
                WeatherModel model = new WeatherModel();
                // 이름과 나라, 아이콘, 날씨 상태, 구름,습도,풍속 id 가져오기
                model.setName(jsonObject.get("name").toString().replaceAll("\"",""));
                model.setCountry(jsonObjectSys.get("country").toString().replaceAll("\"",""));
                // 받아온 icon이름을 가지고 image 세팅해줘야 한다.
                model.setIcon(getString(R.string.weather_url)+"img/w/" + jsonObjectWeather.get("icon").toString().replaceAll("\"","") + ".png");
                icon = jsonObjectWeather.get("icon").toString();
                model.setTemp(jsonObjectMain.get("temp").getAsDouble() - 273.15);
                model.setMain(jsonObjectWeather.get("main").toString().replaceAll("\"",""));
                model.setDescription(jsonObjectWeather.get("description").toString().replaceAll("\"",""));
                model.setWind(jsonObjectWind.get("speed").getAsDouble());
                model.setClouds(jsonObjectClouds.get("all").getAsDouble());
                model.setHumidity(jsonObjectMain.get("humidity").getAsDouble());
                model.setId(jsonObjectWeather.get("id").getAsInt());

                setWeatherData(model);  //UI 업데이트

            } else {
                // 서버와 통신이 되지 않았다면 dialog띄워서 통신이 되지 않았다고 안내해주기
                showFailPop();
            }
        }

        // 비동기 동작이 취소될 때 실행되는 메서드로서 오류처리 같은 행위를 하면 된다.
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }  //NetworkTask End


    /* 통신하여 받아온 날씨 데이터를 통해 UI 업데이트 메소드 */
    private void setWeatherData(WeatherModel model) {
        Log.d(TAG, "setWeatherData");

        Glide.with(context).load(model.getIcon())  //Glide 라이브러리를 이용하여 ImageView 에 url 로 이미지 지정
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(iv_weather);
        Log.e(TAG, "setWeatherData: 날씨 id = " + model.getId() );
        tv_temp.setText(doubleToStrFormat(2, model.getTemp()) + " 'C");  //소수점 2번째 자리까지 반올림하기
        tv_main.setText(model.getMain());
        tv_description.setText(model.getDescription());
        Log.e(TAG, "setWeatherData: 날씨 icon = " + icon );
        tv_wind.setText(doubleToStrFormat(2, model.getWind()) + " m/s");
        tv_cloud.setText(doubleToStrFormat(2, model.getClouds()) + " %");
        tv_humidity.setText(doubleToStrFormat(2, model.getHumidity()) + " %");
        Log.e(TAG, "setWeatherData: setannounce 전" );
        SetAnnounce(icon);
        Log.e(TAG, "setWeatherData: setannounce 후" );
    }

    // textview 여행 알리미 세팅하기
    private void SetAnnounce(String icon) {
        Log.e(TAG, "SetAnnounce: 들어옴" );

        Log.e(TAG, "SetAnnounce: icon = " + icon );

        // 날씨를 구분해주는 string 값이 "10n"으로 출력되서 맨앞과 맨뒤의 문자를 split 해줘야함.
        String word = icon.substring(1,4);

        // 현재 시간 받아오기
        long now = System.currentTimeMillis();
        Date date = new Date(1665039451);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);

        Log.e(TAG, "SetAnnounce: 날짜 = " + getTime );

        // 날씨에 따른 여행 안내문구 변경해주기

        if(word.equals("01d") || word.equals("01n"))
        {
            //clear sky
            tx_announce.setText("날씨가 화창해요! \n여행하기 좋은 날씨입니다~");
        }
        else if(word.equals("02d") || word.equals("02n")){

            //few clouds
            tx_announce.setText("구름이 조금 있지만 여행하기 좋은 날씨입니다~");
        }
        else if(word.equals("03d") || word.equals("03n"))
        {
            //scattered clouds
            tx_announce.setText("조금 흐려요! \n그래도 여행하기 좋은 날씨입니다~");
        }
        else if(word.equals("04d") || word.equals("04n"))
        {
            //	broken clouds
            tx_announce.setText("구름이 많아요! \n혹시 모르니 우산 챙겨가세요~");
        }
        else if(word.equals("09d") || word.equals("09n"))
        {
            // shower rain
            tx_announce.setText("소나기가 오고있어요! \n여행가기전 우산 꼭 챙겨나가세요~");
        }
        else if(word.equals("10d") || word.equals("10n"))
        {
            //rain
            tx_announce.setText("비가 오고 있어요!! \n우산 챙기시고 여행시 주의해주세요~");
        }
        else if(word.equals("11d") || word.equals("11n"))
        {
            //thunderstorm
            tx_announce.setText("천둥 번개가 치고있어요! \n여행시 주의해주세요!!");
        }
        else if(word.equals("13d") || word.equals("13n"))
        {
            //snow
            tx_announce.setText("눈이 오고 있어요! \n 여행할때 두꺼운옷 입고가세요~ 미끄러지지 않게 조심하세요~");
        }
        else if(word.equals("50d") || word.equals("50n"))
        {
            //mist
            tx_announce.setText("안개낀 날씨예요! \n여행시 앞을 조심해주세요!");
        }



    }


    /* 통신 실패시 AlertDialog 표시하는 메소드 */
    private void showFailPop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setMessage("통신실패");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /* 소수점 n번째 자리까지 반올림하기 */
    private String doubleToStrFormat(int n, double value) {
        return String.format("%."+n+"f", value);
    }

}