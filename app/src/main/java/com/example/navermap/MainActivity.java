package com.example.navermap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.health.PackageHealthStats;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity implements NaverMap.OnMapClickListener, Overlay.OnClickListener, NaverMap.OnCameraChangeListener, NaverMap.OnCameraIdleListener, OnMapReadyCallback {
    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private InfoWindow infoWindow;
    private boolean isCameraAnimated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setUpMapIfNeeded();

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();//?
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        this.naverMap = naverMap;

        //현재위치 버튼 활성화
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        naverMap.addOnCameraChangeListener(this); //카메라의 이동 감지
        naverMap.addOnCameraIdleListener(this);
        naverMap.setOnMapClickListener(this);


        //지도상에서의 중심점 얻기기
        LatLng mapCenter = naverMap.getCameraPosition().target;

        fetchStoreSale(mapCenter.latitude, mapCenter.longitude, 5000);

        setUpMap();
    }



    private void fetchStoreSale(double latitude, double longitude, int radius) {
        PharmParser parser = new PharmParser();
        ArrayList<Pharm> list = null;
        try{
            list = parser.apiParserSearch();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpMapIfNeeded(){
        if(naverMap == null){
            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if(naverMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        PharmParser parser = new PharmParser();
        ArrayList<Pharm> list = null;
        try{
            list = parser.apiParserSearch();
        }catch (Exception e){
            e.printStackTrace();
        }

        for(Pharm entity : list){
            Marker marker = new Marker();
            marker.setPosition(new LatLng(entity.getYPos(), entity.getXPos()));
            marker.setCaptionText(entity.getYadmNm());

            marker.setMap(naverMap);
        }
    }


    public void onCameraChange ( int reason, boolean animated){
        isCameraAnimated = animated;
    }

    //지도가 움직이고 새로운 위치로 이동했을때 지도의 중심을 다시 잡기 위한 메서드
    public void onCameraIdle () {
        if (isCameraAnimated) {
            LatLng mapCenter = naverMap.getCameraPosition().target;
            fetchStoreSale(mapCenter.latitude, mapCenter.longitude, 5000);
        }
    }


    @Override//퍼미션 요청 수신
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_REQUEST_CODE:
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        if(infoWindow.getMarker() != null) {
            infoWindow.close();
        }
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if(overlay instanceof Marker){
            Marker marker = (Marker)overlay;
            if (marker.getInfoWindow() != null) {
                infoWindow.close();
            } else {
                infoWindow.open(marker);
            }
            return true;
        }
        return false;
    }

}
