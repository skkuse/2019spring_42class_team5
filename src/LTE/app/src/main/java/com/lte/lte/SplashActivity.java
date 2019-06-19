package com.lte.lte;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

// 처음 앱 실행 시, 위치 정보 권한 획득 필요함을 알려 줌
// 작성자 : 배경률

public class SplashActivity extends AppCompatActivity {
    public static final int ACTIVITY_RESULT_LOGIN = 2;
    private SharedManagerUtil mSpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSpUtil = SharedManagerUtil.getInstance(SplashActivity.this);
        mSpUtil.setServerIP("115.145.226.139");

        tedPermission();
    }

    private void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

                //       Temporary
                Intent intent=  new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 요청 실패
                finish();

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }



}
