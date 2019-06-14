package com.lte.lte;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private SharedManagerUtil mSpUtil;
    private Boolean isAutoLogin;

    private EditText etUserID;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegist;
    private CheckBox cbAutoLogin;

    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mSpUtil = SharedManagerUtil.getInstance(LogInActivity.this);
        isAutoLogin = mSpUtil.getAutoLogin();

        etUserID = (EditText) findViewById(R.id.etUserID);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegist = (Button) findViewById(R.id.btnRegist);

        cbAutoLogin = findViewById(R.id.cbAutoLogin);


        if (isAutoLogin) {
            cbAutoLogin.setChecked(true);
            etUserID.setText(mSpUtil.getUserID());
            etPassword.setText(mSpUtil.getUserPW());
            etUserID.setEnabled(false);
            etPassword.setEnabled(false);
            // 여기에 DB와 내용 비교해야 함
            dialog = ProgressDialog.show(LogInActivity.this, "",
                    "확인 중...", true);
            new Thread(this::login).start();
        }


        // 회원가입 버튼 클릭
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 페이지 오픈
                Intent intent = new Intent(getApplicationContext(), SignupPage.class);

                // SINGLE_TOP : 이미 만들어진게 있으면 그걸 쓰고, 없으면 만들어서 써라
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // intent를 보내면서 다음 액티비티로부터 데이터를 받기 위해 식별번호(1000)을 준다.
                startActivityForResult(intent, 1000);
            }


        });


        // 로그인 버튼 눌렀을 때
        btnLogin.setOnClickListener(new View.OnClickListener() {

            private void run() {
                login();
            }

            @Override
            public void onClick(View v) {
                // 여기에 DB와 내용 비교해야 함
                dialog = ProgressDialog.show(LogInActivity.this, "",
                        "확인 중...", true);
                new Thread(this::run).start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // setResult를 통해 받아온 요청번호, 상태, 데이터
        Log.d("RESULT", requestCode + "");
        Log.d("RESULT", resultCode + "");
        Log.d("RESULT", data + "");

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(LogInActivity.this, "회원가입을 완료했습니다!", Toast.LENGTH_SHORT).show();
            etUserID.setText(data.getStringExtra("ID"));
        }
    }

    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://"+mSpUtil.getServeriP()+"/signup.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("UserID", etUserID.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("Password", etPassword.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = getThreadSafeClient().execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = getThreadSafeClient().execute(httppost, responseHandler);
            runOnUiThread(() -> {
                dialog.dismiss();
            });

            mSpUtil.setAutoLogin(cbAutoLogin.isChecked());
            if (response.equalsIgnoreCase("User Found")) {
                if (cbAutoLogin.isChecked()) {
                    mSpUtil.setAutoLogin(true);
                    mSpUtil.setUserID(etUserID.getText().toString());
                    mSpUtil.setUserPW(etPassword.getText().toString());
                }
                // 로그인 내용 확인되면 Main Activity 호출
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                // 회원 ID를 Main Activity로 보내서 DB 정보 검색 Key 값으로 이용
                intent.putExtra("UserID", String.valueOf(etUserID.getText()));
                startActivity(intent);
                finish();
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(LogInActivity.this, "아이디 혹은 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                    etUserID.setEnabled(true);
                    etPassword.setEnabled(true);
                });
            }
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public static DefaultHttpClient getThreadSafeClient() {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

                mgr.getSchemeRegistry()), params);
        return client;
    }

}
