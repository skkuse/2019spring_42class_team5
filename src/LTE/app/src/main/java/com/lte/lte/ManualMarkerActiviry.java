package com.lte.lte;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

public class ManualMarkerActiviry extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    Button btnManualMarker;
    private static Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_marker_activiry);

        btnManualMarker = findViewById(R.id.btn_manual_marker_done);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        mapFragment.getMapAsync(this);

        LatLng coord = new LatLng(37.5670135, 126.9783740);

        btnManualMarker.setOnClickListener(v -> {
            if(marker==null)
                Toast.makeText(getApplicationContext(),"위치를 클릭하세요",Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent();
                intent.putExtra("latitude",marker.getPosition().latitude);
                intent.putExtra("longitude",marker.getPosition().longitude);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // Location of Location Button
//        View locationButton = ((View) View.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
//                locationButton.getLayoutParams();
//        // position on right bottom
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        layoutParams.setMargins(0, 0, 30, 30);

        // Code for Current Location


        // Clicking maps makes Markers
        // It will be used to the timestamp function
        naverMap.setOnMapClickListener((point, coord) -> {
            if(marker!=null)
                marker.setMap(null);
            marker = new Marker();
            marker.setPosition(new LatLng(coord.latitude, coord.longitude));
            marker.setIcon(MarkerIcons.BLACK);
            // Default: GRAY
            // It will change its color with its star rank
            marker.setIconTintColor(Color.GRAY);
            marker.setMap(naverMap);
        });


    }


}
