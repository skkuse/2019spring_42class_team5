package com.lte.lte;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.clans.fab.FloatingActionMenu;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private SharedManagerUtil mSpUtil;

    private FloatingActionMenu fam;
    private com.github.clans.fab.FloatingActionButton fab1, fab2, fab3;
    private Button btn_picture;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private Boolean locationChecking = false;
    private NaverMap naverMapObj;
    private MapView NmapView;
    private double r1;
    private double r2;
    private double x;
    private double y;

    private static NaverMap.OnLocationChangeListener listner;
    private static List<LatLng> coords;

    private static PathOverlay path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSpUtil = SharedManagerUtil.getInstance(MainActivity.this);

        // 로그인 페이지에서 회원 ID 정보 받아 옴
        Intent intent = new Intent(this.getIntent());
        String UserID = intent.getStringExtra("UserID");


        btn_picture = (Button) findViewById(R.id.btn_AddPicture);

        fam = findViewById(R.id.fam);
        fab1 = findViewById(R.id.fab1); //경로 저장
        fab2 = findViewById(R.id.fab2); //타임스탬프
        fab3 = findViewById(R.id.fab3);  //로그아웃

        btn_picture.setOnClickListener(this);
        fam.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        fab1.setLabelText("경로 저장");

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        NmapView = mapFragment.getMapView();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @UiThread

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMapObj = naverMap;

/*        // Location of Location Button
        View locationButton = ((View) View.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
*/

//        recordRoute(naverMap);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_AddPicture:
                Intent intent = new Intent(this, AddModifyPicture.class);
                startActivity(intent);
                break;

            case R.id.fab1:
                if (!locationChecking) {
                    fab1.setLabelText("경로 저장 중지");
                    recordRoute(naverMapObj);
                } else {
                    fab1.setLabelText("경로 저장");
                    StoprecordRoute(naverMapObj);
                }
                locationChecking = !locationChecking;
                break;
            case R.id.fab2:
                Marker marker = new Marker();
                LatLng current = new LatLng(x, y);
                naverMapObj.addOnLocationChangeListener(location ->
                {
                    x = location.getLatitude();
                    y = location.getLongitude();
                });
                naverMapObj.removeOnLocationChangeListener(location -> {
                });
                marker.setPosition(current);
                marker.setIcon(MarkerIcons.BLACK);
                // Default: GRAY
                marker.setIconTintColor(Color.GRAY);
                marker.setMap(naverMapObj);
                break;
            case R.id.fab3:
                mSpUtil.setAutoLogin(false);
                Intent intent2 = new Intent(this, LogInActivity.class);
                startActivity(intent2);
                finish();
                break;
        }

    }


    public void recordRoute(@NonNull NaverMap naverMap) {
        // operate
        path = new PathOverlay();

        coords = new ArrayList<>();
        listner = location -> {
            r1 = location.getLatitude();
            r2 = location.getLongitude();
            LatLng Add = new LatLng(r1, r2);
            coords.add(Add);

            if (1 < coords.size()) {
                path.setCoords(coords);
                path.setOutlineWidth(0);
                path.setColor(Color.rgb(255, 160, 0));
                path.setMap(naverMap);
                // ready
            }
        };

        naverMap.addOnLocationChangeListener(listner);

        //end of code
    }

    public void StoprecordRoute(@NonNull NaverMap naverMap) {

        naverMap.removeOnLocationChangeListener(listner);

        Collections.addAll(coords,
                new LatLng(r1, r2),
                new LatLng(r1, r2)
        );
        if (1 < coords.size()) {
            path.setCoords(coords);
            path.setOutlineWidth(0);
            path.setColor(Color.rgb(255, 160, 0));
            path.setMap(naverMap);
            // ready
        }
    }


}

