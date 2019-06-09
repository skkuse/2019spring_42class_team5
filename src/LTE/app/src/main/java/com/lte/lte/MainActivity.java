package com.lte.lte;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private Button btn_picture, btn_timestamp;
    private FloatingActionButton fab, fab1, fab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로그인 페이지에서 회원 ID 정보 받아 옴
        Intent intent = new Intent(this.getIntent());
        String UserID = intent.getStringExtra("UserID");

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        btn_picture = (Button) findViewById(R.id.btn_AddPicture);
        btn_timestamp = (Button) findViewById(R.id.btn_TimeStamp);
        fab= (FloatingActionButton)findViewById(R.id.fab_HashTags);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1); //리스트
        fab2 = (FloatingActionButton) findViewById(R.id.fab2); //임시


        btn_picture.setOnClickListener(this);
        btn_timestamp.setOnClickListener(this);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

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
/*        // Location of Location Button
        View locationButton = ((View) View.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
*/
        // Code for Current Location

        /*
        // Clicking maps makes Markers
        // It will be used to the timestamp function
        naverMap.setOnMapClickListener((point, coord) -> {
            Marker marker = new Marker();
            marker.setPosition(new LatLng(coord.latitude, coord.longitude));
            marker.setIcon(MarkerIcons.BLACK);
            // Default: GRAY
            // It will change its color with its star rank
            marker.setIconTintColor(Color.GRAY);
            marker.setMap(naverMap);
        });
        */

        // Route
        // It will be used to the GPS route
        PathOverlay path = new PathOverlay();
        List<LatLng> coords = new ArrayList<>();
        Collections.addAll(coords,
                new LatLng(37.57152, 126.97714),
                new LatLng(37.56607, 126.98268)
        );
        path.setCoords(coords);
        path.setOutlineWidth(0);
        path.setColor(Color.rgb(255, 160, 0));
        path.setMap(naverMap);
        naverMap.setOnMapClickListener((point, coord) -> {
            addRoute(coords, coord.latitude, coord.longitude);
            path.setCoords(coords);
            path.setOutlineWidth(0);
            path.setColor(Color.rgb(255, 160, 0));
            path.setMap(naverMap);
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_AddPicture:
                Intent intent = new Intent(this, AddModifyPicture.class);
                startActivity(intent);
                break;
            case R.id.btn_TimeStamp:
                break;
            case R.id.fab_HashTags:
                anim();
                break;
            case R.id.fab1:
                anim();
                break;
            case R.id.fab2:
                anim();
                break;
        }

    }


    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

}
