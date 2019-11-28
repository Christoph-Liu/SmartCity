package com.example.smartcity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener,
        AMap.OnMarkerClickListener {

    private MapView mapView;
    private AMap aMap;
    private static int SEARCH_MARK = 1;
    private TextView currentPlace;
    double myLatitude;
    double myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mapView = (MapView)findViewById(R.id.map);
        aMap = mapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        /*
        隐藏logo
         */
        uiSettings.setLogoBottomMargin(-50);
        /*
        设置定位监听
         */
        aMap.setOnMyLocationChangeListener(this);
        /*
        设置marker的click监听
         */
        aMap.setOnMarkerClickListener(this);
        /*
        设置搜索框的监听
         */
        currentPlace = (TextView)findViewById(R.id.tv_place);
        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SearchActivity.this, InputTipsActivity.class);
                startActivityForResult(intent, SEARCH_MARK);
            }
        }
        );
        initialLocationStyle();
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_MARK) {
            if (resultCode == InputTipsActivity.RESULT_CODE_INPUTTIPS && data != null) {
                final Tip tip = data.getParcelableExtra("tip");
                if (tip.getName() != null) {
                    currentPlace.setText("当前地点：" + tip.getName());
                }
                PoiSearch.Query query = new PoiSearch.Query("", "150900", "");
                query.setPageSize(10);
                query.setPageNum(1);
                PoiSearch poiSearch = new PoiSearch(this, query);
                poiSearch.setBound(new PoiSearch.SearchBound(tip.getPoint(), 10000));
                poiSearch.setOnPoiSearchListener(this);
                poiSearch.searchPOIAsyn();
            }
        }
    }

    /**
     * 定位，并显示当前所在位置
     */
    protected void initialLocationStyle() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(myLocationStyle.LOCATION_TYPE_SHOW);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    /**
     * 定位成功返回坐标，搜索附近的停车场，设置监听
     */
    @Override
    public void onMyLocationChange(android.location.Location location)
    {
        if (location != null) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            PoiSearch.Query query = new PoiSearch.Query("", "150900", "");
            query.setPageSize(10);
            query.setPageNum(1);
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(myLatitude, myLongitude), 10000));
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        }

    }

    /**
     * 处理搜索到的停车场并在地图上标记
     */
    @Override
    public void onPoiSearched(com.amap.api.services.poisearch.PoiResult poiResult, int i) {
        ArrayList<PoiItem> result = poiResult.getPois();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for(PoiItem poi : result) {
            String snippet = poi.getSnippet() + '\n' + poi.getTitle();
            LatLonPoint point = poi.getLatLonPoint();
            initialMark(point, snippet);
            LatLng lat = new LatLng(point.getLatitude(), point.getLongitude());
            boundsBuilder.include(lat);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 25));
    }

    @Override
    public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {}

    /**
     * 标记函数
     */
    protected void initialMark(LatLonPoint point, String snippet) {
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.title("停车场").snippet(snippet);
        markerOption.draggable(true);
        markerOption.position(latLng);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.park));
        markerOption.anchor(0.5f, 0.5f);
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

    /**
     * 标记click回调函数，进入导航页面的入口
     */
    @Override
    public boolean onMarkerClick(Marker var1) {
        final LatLng lat = var1.getPosition();
        var1.showInfoWindow();
        /*
        这里可以添加获取车位信息的逻辑
         */
        int AvailableParkinSgpaces = 100;
        String message = String.format("%s\n\n地址：%s\n\n可用车位：%s\n\n是否要导航至此处",
                var1.getSnippet().split("\n")[1], var1.getSnippet().split("\n")[0], AvailableParkinSgpaces);
        new AlertDialog.Builder(SearchActivity.this).setTitle("将要开始导航")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(SearchActivity.this, TraceActivity.class);
                        intent.putExtra("startLatitude", myLatitude);
                        intent.putExtra("startLongitude", myLongitude);
                        intent.putExtra("targetLatitude", lat.latitude);
                        intent.putExtra("targetLongitude", lat.longitude);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        return true;
    }
    /**
     * 双击退出
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(SearchActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }
}
