package com.lte.lte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private static String route_create_time, route_end_time;


    private static PathOverlay path;

    // DB 데이터 JSON 불러와서 자르기 test
    private String pictureJsonString;
    private String routeJsonString;


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
        // FusedLocationSource 생성
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        // Set the map
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        NmapView = mapFragment.getMapView();
        mapFragment.getMapAsync(this);

        // JSON parsing 테스트해 보기 위한 임의의 string
        pictureJsonString = "{\"Picture\" : [{" +
                "\"user_id\" : \"test\"," +
                "\"image_path\" : \"https://www.google.co.kr/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png\"," +
                "\"create_time\" : \"2019-06-15 08:28\"," +
                "\"x_coordinate\" : \"37.566535\"," +
                "\"y_coordinate\" : \"126.977969\"," +
                "\"hashtag\" : \"kitties\"," +
                "\"starpoint\" : \"4\"," +
                "\"text\" : \"여기 좋았다\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"image_path\" : \"https://www.google.co.kr/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png\"," +
                "\"create_time\" : \"2019-06-17 08:28\"," +
                "\"x_coordinate\" : \"37.5789\"," +
                "\"y_coordinate\" : \"126.9765\"," +
                "\"hashtag\" : \"happy\"," +
                "\"starpoint\" : \"5\"," +
                "\"text\" : \"굿굿\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"image_path\" : \"https://www.google.co.kr/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png\"," +
                "\"create_time\" : \"2019-06-14 08:28\"," +
                "\"x_coordinate\" : \"37.56999\"," +
                "\"y_coordinate\" : \"126.977999\"," +
                "\"hashtag\" : \"ramen\"," +
                "\"starpoint\" : \"3\"," +
                "\"text\" : \"보통\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"image_path\" : null," +
                "\"create_time\" : \"2019-06-14 08:28\"," +
                "\"x_coordinate\" : \"37.56809\"," +
                "\"y_coordinate\" : \"126.977809\"," +
                "\"hashtag\" : null," +
                "\"starpoint\" : null," +
                "\"text\" : null" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"image_path\" : \"https://www.google.co.kr/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png\"," +
                "\"create_time\" : \"2019-06-12 08:28\"," +
                "\"x_coordinate\" : \"37.55444\"," +
                "\"y_coordinate\" : \"126.96789\"," +
                "\"hashtag\" : \"happy\"," +
                "\"starpoint\" : \"2\"," +
                "\"text\" : \"그저 그랬음\"" +
                "}" +
                "" +
                "]" +
                "}";

        routeJsonString = "{" +
                "\"Route\" : [" +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:28\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : { \"x\" : \"37.566535\", \"y\" : \"126.977969\"}," +
                "\"route_order\" : \"0\"," +
                "\"route_id\" : \"1\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:28\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : { \"x\" : \"37.5666\", \"y\" : \"126.978\"}," +
                "\"route_order\" : \"1\"," +
                "\"route_id\" : \"1\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:28\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : {\"x\" : \"37.566\", \"y\" : \"126.979\"}," +
                "\"route_order\" : \"2\"," +
                "\"route_id\" : \"1\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:41\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : {\"x\" : \"37.56\", \"y\" : \"126.97\"}," +
                "\"route_order\" : \"0\"," +
                "\"route_id\" : \"2\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:41\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : { \"x\" : \"37.55\", \"y\" : \"126.97\"}," +
                "\"route_order\" : \"1\"," +
                "\"route_id\" : \"2\"" +
                "}," +
                "{" +
                "\"user_id\" : \"test\"," +
                "\"create_time\" : \"2019-06-15 08:41\"," +
                "\"end_time\" : \"2019-06-15 09:28\"," +
                "\"route_info\" : { \"x\" : \"37.54\", \"y\" : \"126.9\"}," +
                "\"route_order\" : \"2\"," +
                "\"route_id\" : \"2\"" +
                "}" +
                "]" +
                "}";

    }

    private void showPicture() {
        String TAG_JSON = "Picture";
        String TAG_USERID = "user_id";
        String TAG_imagePath = "image_path";
        String TAG_creattime = "create_time";
        String TAG_x = "x_coordinate";
        String TAG_y = "y_coordinate";
        String TAG_hashtag = "hashtag";
        String TAG_star = "starpoint";
        String TAG_text = "text";

        List<Marker> markers = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(pictureJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String user_id = item.getString(TAG_USERID);
                String create_time = item.getString(TAG_creattime);
                double x_coordinate = Double.parseDouble(item.getString(TAG_x));
                double y_coordinate = Double.parseDouble(item.getString(TAG_y));
                String starpoint = item.getString(TAG_star); // null 존재할 수도 있으므로 String으로 받음
                String image_path = item.getString(TAG_imagePath);

                // 마커 생성
                Marker marker = new Marker();
                LatLng current = new LatLng(x_coordinate, y_coordinate);

                marker.setPosition(current);
                marker.setIcon(MarkerIcons.BLACK);
                marker.setCaptionText(create_time);
                marker.setCaptionRequestedWidth(250);
                marker.setCaptionColor(Color.BLUE);
                marker.setCaptionHaloColor(Color.rgb(200, 255, 200));

                switch (starpoint) {
                    case "5":
                    case "4.5":
                        marker.setIconTintColor(Color.BLUE);
                        break;
                    case "4":
                    case "3.5":
                        marker.setIconTintColor(Color.rgb(102, 204, 255));
                        break;
                    case "3":
                    case "2.5":
                        marker.setIconTintColor(Color.GREEN);
                        break;
                    case "2":
                    case "1.5":
                        marker.setIconTintColor(Color.YELLOW);
                        break;
                    case "1":
                    case "0.5":
                        marker.setIconTintColor(Color.rgb(255, 111, 97));
                        break;
                    default:
                        // Default: GRAY
                        marker.setIconTintColor(Color.GRAY);
                        break;
                }

                markers.add(marker);
            }

            // 지도 위에 다중 마커 표시
            for (Marker marker : markers) {
                marker.setMap(naverMapObj);
            }


        } catch (JSONException e) {
            Log.d("cobluelei", "showResult : ", e);
        }

    }

    private void showRoute() {
        String TAG_JSON = "Route";
        String TAG_USERID = "user_id";
        String TAG_creattime = "create_time";
        String TAG_endtime = "end_time";
        String TAG_routeInfo = "route_info";
        String TAG_routeOrder = "route_order";
        String TAG_routeId = "route_id";

        int[] route_id_list = new int[1000];
        List<LatLng> one_route = new ArrayList<>();
        List<List<LatLng>> routes = new ArrayList<>();

        // 경로 불러오기 실패
        /*
        try {

            for (int x = 0; x < 1000; x++) {
                JSONObject jsonObject = new JSONObject(routeJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                one_route = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    String user_id = item.getString(TAG_USERID);

                    JSONObject route_info = item.getJSONObject(TAG_routeInfo);
                    double x_coordinate = Double.parseDouble(route_info.getString("x"));
                    double y_coordinate = Double.parseDouble(route_info.getString("y"));

                    int route_order = Integer.parseInt(item.getString(TAG_routeOrder));
                    int route_id = Integer.parseInt(item.getString(TAG_routeId));

                    for (int y = 0; y < 1000; y++) {
                        if (route_id == x) {
                            if (route_order == y) {
                                one_route.add(y, new LatLng(x_coordinate, y_coordinate));
                            }
                        }
                    }

                    route_id_list[route_id] = route_id;
                }
                routes.add(x, one_route);
            }

            // 현재 저장되어 있는 경로의 수
            int max_route_id = route_id_list[0];

            for(int i = 0; i < route_id_list.length; i++) {
                if(max_route_id < route_id_list[i]) {
                    max_route_id = route_id_list[i];
                }
            }

            for(int i = 0; i < routes.size(); i++) {
                if (1 < routes.get(i).size()) {
                    path.setCoords(routes.get(i));
                    path.setOutlineWidth(0);
                    path.setColor(Color.rgb(255, 160, 0));
                    path.setMap(naverMapObj);
                    // ready
                }
            }



        } catch(JSONException e){
                Log.d("cobluelei", "showResult : ", e);
        }
        */
    }
    // onRequestPermissionResult() in FusedLocationSource calls the result (위치반환 구현체에 결과 전달)
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
        // 위치추적 기능
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMapObj = naverMap;

        showPicture();
        showRoute();

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

                String currenttime = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss").format(new Date());


                try {
                    insertToDatabase(mSpUtil.getUserID(), currenttime, current.latitude, current.longitude);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fab3:
                mSpUtil.setAutoLogin(false);
                Intent intent2 = new Intent(this, LogInActivity.class);
                startActivity(intent2);
                finish();
                break;
        }

    }

    // 경로 기록 시작
    public void recordRoute(@NonNull NaverMap naverMap) {
        // operate
        path = new PathOverlay();

        coords = new ArrayList<>();
        route_create_time = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss").format(new Date()); // 기록 시작 시간

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
    // 경로 기록 종료
    public void StoprecordRoute(@NonNull NaverMap naverMap) {

        naverMap.removeOnLocationChangeListener(listner);
        route_end_time = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss").format(new Date()); // 기록 종료 시간
        Collections.addAll(coords,
                new LatLng(r1, r2),
                new LatLng(r1, r2)
        );
        if (1 < coords.size()) {
            path.setCoords(coords);
            path.setOutlineWidth(0);
            path.setColor(Color.rgb(255, 160, 0));
            path.setMap(naverMap);
        }
    }


    private void insertToDatabase(String UserID, String date, double lat, double lot) throws UnsupportedEncodingException {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;//private added

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {


                    String link = "http://" + mSpUtil.getServeriP() + "//image.php";


                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(params[0]);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "Exception: " + e.getMessage();
                }
            }
        }


        String data = URLEncoder.encode("UserID", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8");
        data += "&" + URLEncoder.encode("Create_time", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
        data += "&" + URLEncoder.encode("x_coordinate", "UTF-8") + "=" + URLEncoder.encode(Double.toString(lot), "UTF-8");
        data += "&" + URLEncoder.encode("y_coordinate", "UTF-8") + "=" + URLEncoder.encode(Double.toString(lat), "UTF-8");
        data += "&" + URLEncoder.encode("hashtag", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");

        data += "&" + URLEncoder.encode("starpoint", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
        data += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
        data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");

        InsertData task = new InsertData();
        task.execute(data);
    }

}

