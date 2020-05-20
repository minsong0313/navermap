package com.example.navermap;

import android.database.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PharmApi {
    String UTL = "http://apis.data.go.kr";
    @GET("/B551182/pharmacyInfoService/getParmacyBasisList?serviceKey=gyhnkvw8BuHNtPGQzXT5Nluh3Ri3hGlcpEnheMdjI1gjDbZhPSEpy05ofIMaFu2a96c%2FUX%2FzOVblYrTa%2B%2Fu%2Bjg%3D%3D")
    Observable<Pharm> getPharm();

}
