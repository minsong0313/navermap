package com.example.navermap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.Activity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String key="gyhnkvw8BuHNtPGQzXT5Nluh3Ri3hGlcpEnheMdjI1gjDbZhPSEpy05ofIMaFu2a96c%2FUX%2FzOVblYrTa%2B%2Fu%2Bjg%3D%3D";//공공데이터 서비스키
    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;

    //xml변수
    private EditText edit;
    private TextView text;

    //파싱하기 위한 필드 선언
    private URL url;
    private InputStream is;
    private XmlPullParserFactory factory;
    private XmlPullParser xpp;
    private String tag;
    private int eventType;

    //xml의 값 입력 변수
    private String location; //동 이름
    private StringBuffer buffer;

    String data;

    private NaverMap naverMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //쓰레드 정책 무시
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        edit = (EditText) findViewById(R.id.edit);
        text = (TextView) findViewById(R.id.result);

        //지도
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        //현재위치 버튼 활성화
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
    }



    @Override//퍼미션 요청 수신
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case ACCESS_LOCATION_PERMISSION_REQUEST_CODE:
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    //검색 버튼 클릭 메서드
    public void mOnClick(View v){
        switch(v.getId()){
            case R.id.button:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = getXmlData();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(data);
                            }
                        });
                    }
                }).start();
                break;
        }
        //edit 검색 후 키보드 숨기기
        InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }

    //공공데이터 파싱, 출력
    private String getXmlData() {
        buffer = new StringBuffer();

        String str = edit.getText().toString();//EditText에 작성된 Text얻어오기
        location = null;
        try {
            location = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //    String query = "bDT10I5VhSNuQu1J_zZv";// 네이버 검색

        String queryUrl = "http://apis.data.go.kr/B551182/pharmacyInfoService/getParmacyBasisList?serviceKey="//요청 URL
                + key + "&emdongNm=" + location; //동 이름으로 검색

        try {
            url = new URL(queryUrl);
            is = url.openStream(); //url위치로 입력스트림 연결

            factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream 으로부터 xml 입력받기

            xpp.next();
            eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//테그 이름 얻어오기

                        if (tag.equals("item")) ;// 첫번째 태그값이랑 비교

                        else if (tag.equals("yadmNm")) {
                            buffer.append("약국명 :");
                            xpp.next();
                            buffer.append(xpp.getText());//title 요소의 TEXT 읽어오기
                            buffer.append("\n"); //줄바꿈

                        }else if (tag.equals("telno")) {
                            buffer.append("전화번호 :");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if (tag.equals("addr")) {
                            buffer.append("주소 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");

                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //테그 이름 얻어오기

                        if (tag.equals("item"))
                            buffer.append("\n");// 첫번째 검색결과종료 후 줄바꿈

                }
                eventType = xpp.next();
            } //while문 끝

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        return buffer.toString();//StringBuffer 문자열 객체 반환
    }


}
