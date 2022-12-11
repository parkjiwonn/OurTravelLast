package com.example.ourtravel.Weather;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

// HttpUrlconnection을 위한 클래스
// http 관련 기능을 지원하는 URLConnection이다.
// 클래스 사용의 패턴
/*
* 1. HttpURLConnection 결과를 호출 -> URL.openConnection()하고 캐스팅하여 새 항목을 얻는다.
* 2. 요청을 준비한다.(서버로 보낼 요청), 요청의 기본 속성은 URI이다. 요청 헤더에는 자격 증명, 선호하는 콘텐츠 유형 및 세션 쿠키와 같은
* 메타 데이터도 포함될 수 있다.
* 3. 선택적으로 요청 본문을 업로드한다. setDoOutput(true)
* 만약 요청 바디를 포함하고 있다면 인스턴스는 setDoOutput으로 구성되어야 한다.
* 4. 응답을 읽어라. 응답 헤더는 전형적으로 응답 바디의 콘텐츠 타입, 길이, 수정된 데이터 그리고 세션 쿠키과 같은 메타 데이터들을 포함하고 있다.
* 5. 연결을 끊어라. 응답 바디가 읽혔을 때 HttpURLConnection은 disconnect()를 호출함으로써 닫혀야 한다. 닫힘은
* 연결로 일어난 리소스들을 내보낸다. 그래서 그들은 닫히거나 다시 사용되어야 한다.
* */

public class RequestHttpUrlConnection {
    public String TAG = RequestHttpUrlConnection.class.getSimpleName();

    public String request(String _url, ContentValues _params, String method) {

        String result = "";
        try{
            Log.d(TAG, "----- request() - _url : "+_url);
            Log.d(TAG, "----- request() - _params : "+_params.toString());

            StringBuffer sbParams = new StringBuffer();
            String data = "";

            if (_params == null){
                sbParams.append("");
            } else {  //파라미터가 있는 경우
                //파라미터가 2개 이상이면 파라미터를 &로 연결할 변수 생성
                boolean isAnd = false;
                //파라미터 키와 값
                String key;
                String value;


                for(Map.Entry<String, Object> parameter : _params.valueSet()){
                    key = parameter.getKey();
                    value = parameter.getValue().toString();
                    //파라미터가 두개 이상일때, 파라미터 사이에 &로 연결
                    if (isAnd){
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=").append(value);
                    if (!isAnd){
                        if (_params.size() >= 2){
                            isAnd = true;
                        }
                    }
                }
            }
            // 파라미터들을 연결시킨 string 값
            data = sbParams.toString();

            URL url = new URL(method == "POST" ? _url : _url+"?"+data);  //URL 문자열을 이용해 URL 객체 생성
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  //URL 객체를 이용해 HttpUrlConnection 객체 생성 - 연결 객체
            // 응답 타임 아웃 설정해주기
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method); // GET으로 파라미터 전달 됨
//            conn.setRequestMethod("POST");
//            conn.setRequestMethod("GET");

            Log.d(TAG, "----- request() - url+param : "+url+"?"+data);

            // 서버로 값 전송하기
            OutputStream outputStream = conn.getOutputStream();
            // GET 파라미터 전달하기
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();

            // Response 데이터 처리
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "----- request() - responseCode : "+responseCode);

            // 만약 url에 접속이 성공하게 된다면
            if(responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                try {
                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    // 결과 값들을 읽어와라 - 날씨 api를 읽어와라
                    BufferedReader reader = new BufferedReader(in);  //응답 결과를 읽기 위한 스트림 객체 생성
                    String line = "";
                    while((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    result = builder.toString();

                    // 예외 처리 해주기
                } catch(IOException e) {
                    e.printStackTrace();
                }
                // 접속에 실패했다면 메세지 받아오기기
            } else{
                result = conn.getResponseMessage();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.d(TAG, "----- request() - result : "+result.trim());
        return result.trim();
    }
}
