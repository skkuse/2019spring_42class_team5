package com.lte.lte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

// 회원가입 입력 정보 받는 페이지
// 작성자 : 신아름
// 유저 DB에 회원가입 정보 입력 작성자 : 김준형

public class SignupPage extends Activity {

    private SharedManagerUtil mSpUtil;

    private EditText etUserName; // User Name
    private EditText etNickname; // User Nickname
    private EditText etPhoneNum;
    private EditText etUserID;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnDone;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUserName = findViewById(R.id.etUserName);
        etNickname = findViewById(R.id.etNickname);
        etPhoneNum = findViewById(R.id.etPhoneNum);
        etUserID = findViewById(R.id.etUserID);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);



        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);

        // 비밀번호 일치 검사
        // 작성자 : 김준형
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = etPasswordConfirm.getText().toString();

                if (password.equals(confirm)) {
                    etPassword.setBackgroundColor(Color.GREEN);
                    etPasswordConfirm.setBackgroundColor(Color.GREEN);
                } else {
                    etPassword.setBackgroundColor(Color.RED);
                    etPasswordConfirm.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이름 입력 확인
                if (etUserName.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserName.requestFocus();
                    return;
                }

                // 닉네임 입력 확인
                if (etNickname.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "닉네임을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etNickname.requestFocus();
                    return;
                }

                // 전화번호 입력 확인
                if (etPhoneNum.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "전화번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPhoneNum.requestFocus();
                    return;
                }

                // 이메일 입력 확인
                if (etUserID.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "아이디를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserID.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if (etPassword.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if (etPasswordConfirm.getText().toString().length() == 0) {
                    Toast.makeText(SignupPage.this, "비밀번호 확인이 필요합니다!", Toast.LENGTH_SHORT).show();
                    etPasswordConfirm.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                    Toast.makeText(SignupPage.this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }

                Intent result = new Intent();
                result.putExtra("email", etUserID.getText().toString());

                // 자신을 호출한 Activity로 데이터를 보낸다.
                setResult(RESULT_OK, result);
                insert();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // DB에 내용 저장
    // 작성자 : 김준형
    public void insert() {
        String UserName = etUserName.getText().toString();
        String UserID = etUserID.getText().toString();
        String Password = etPassword.getText().toString();
        String PhoneNum = etPhoneNum.getText().toString();
        String NickName = etNickname.getText().toString();

        insertToDatabase(UserName, UserID, Password, PhoneNum, NickName);
    }

    private void insertToDatabase(String UserName, String UserID, String Password, String PhoneNum, String NickName) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;//private added

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupPage.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String UserName = params[0];
                    String UserID = params[1];
                    String Password = params[2];
                    String PhoneNum = params[3];
                    String NickName = params[4];

                    String link = "http://"+mSpUtil.getServeriP()+"/login.php";
                    String data = URLEncoder.encode("UserName", "UTF-8") + "=" + URLEncoder.encode(UserName, "UTF-8");
                    data += "&" + URLEncoder.encode("UserID", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8");
                    data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");
                    data += "&" + URLEncoder.encode("PhoneNum", "UTF-8") + "=" + URLEncoder.encode(PhoneNum, "UTF-8");
                    data += "&" + URLEncoder.encode("NickName", "UTF-8") + "=" + URLEncoder.encode(NickName, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
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
        InsertData task = new InsertData();
        task.execute(UserName, UserID, Password, PhoneNum, NickName);
    }


}
