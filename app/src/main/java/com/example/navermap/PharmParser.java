package com.example.navermap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

public class PharmParser {
    public final static String Pharm_URL = "http://apis.data.go.kr/B551182/pharmacyInfoService/getParmacyBasisList?serviceKey=";
    public final static String Key = "gyhnkvw8BuHNtPGQzXT5Nluh3Ri3hGlcpEnheMdjI1gjDbZhPSEpy05ofIMaFu2a96c%2FUX%2FzOVblYrTa%2B%2Fu%2Bjg%3D%3D&";

    public PharmParser() {
        try {
            apiParserSearch();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //파싱
    public ArrayList<Pharm> apiParserSearch() throws Exception {
        URL url = new URL(getURLParam(null));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");

        String tag = null;
        int event_type = xpp.getEventType();

        ArrayList<Pharm> list = new ArrayList<Pharm>();

        String XPos = null, YPos = null, yadmNm = null, radius = null;
        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
            } else if (event_type == XmlPullParser.TEXT) {
                /**
                 * 약국의 주소만 가져와본다.
                 */
                if (tag.equals("XPos")) {
                    XPos = xpp.getText();
                    System.out.println(XPos);
                } else if (tag.equals("YPos")) {
                    YPos = xpp.getText();
                } else if (tag.equals("yadmNm")) {
                    yadmNm = xpp.getText();
                }
                else if(tag.equals("radius")){
                    radius = xpp.getText();
                }
            } else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
                if (tag.equals("item")) {
                    Pharm entity = new Pharm();
                    entity.setXPos(Double.valueOf(XPos));
                    entity.setYPos(Double.valueOf(YPos));
                    entity.setYadmNm(yadmNm);
                    entity.setRadius(radius);
                    list.add(entity);
                }
            }
            event_type = xpp.next();
        }
        System.out.println(list.size());

        return list;
    }

    private String getURLParam(String search) {
        String url = Pharm_URL + Key;
        if (search != null) {
            url = url + "&yadmNm" + search;
        }
        return url;
    }
    public static void main(String[] args){
        new PharmParser();
    }
}