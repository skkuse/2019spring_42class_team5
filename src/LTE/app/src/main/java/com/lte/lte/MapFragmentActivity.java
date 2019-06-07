package com.lte.lte;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapFragmentActivity extends FragmentActivity implements OnMapReadyCallback {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        LatLng coord = new LatLng(37.5670135, 126.9783740);

        Toast.makeText(getApplicationContext(), "위도: " + coord.latitude + ", 경도: " + coord.longitude, Toast.LENGTH_SHORT).show();
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @UiThread

    private void addRoute(List<LatLng> L, double x, double y){
        LatLng Add = new LatLng(x, y);
        L.add(Add);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
/*        // GPS버튼 위치설정
        View locationButton = ((View) View.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
*/
        // 현재위치

        /*
        // 클릭한 위치에 마커가 생성되는 코드
        // → 타임스탬프 버튼과 연동해서 버튼을 누르면 마커생성 되는 코드로 변경해아함
        naverMap.setOnMapClickListener((point, coord) -> {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(coord.latitude, coord.longitude));
            marker.setIcon(MarkerIcons.BLACK);
            // Default: GRAY
            // 별점에따라 색상이 바뀌게 해야함
            marker.setIconTintColor(Color.GRAY);
            marker.setMap(naverMap);
        });
        */

        // 경로
        // 예시 클릭위치에 경로그리기 → GPS 연동에따라 경로그리기로 바꿀예정
        PathOverlay path = new PathOverlay();
        path.setOutlineWidth(0);
        path.setColor(Color.rgb(255, 160, 0));
        List<LatLng> coords = new ArrayList<>();
        Collections.addAll(coords,
                new LatLng(37.57152, 126.97714),
                new LatLng(37.56607, 126.98268)
        );
        path.setCoords(coords);
        path.setMap(naverMap);
        naverMap.setOnMapClickListener((point, coord) -> {
            addRoute(coords, coord.latitude, coord.longitude);
            path.setCoords(coords);
            path.setMap(naverMap);
        });
    }
}