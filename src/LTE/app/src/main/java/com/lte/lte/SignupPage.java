package com.lte.lte;

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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignupPage extends Activity {

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

        etUserName = (EditText) findViewById(R.id.etUserName);
        etNickname = (EditText) findViewById(R.id.etNickname);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etUserID = (EditText) findViewById(R.id.etUserID);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);

        btnDone = (Button) findViewById(R.id.btnDone);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // 비밀번호 일치 검사
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirm = etPasswordConfirm.getText().toString();

                if( password.equals(confirm) ) {
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
                if( etUserName.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserName.requestFocus();
                    return;
                }

                // 닉네임 입력 확인
                if( etNickname.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "닉네임을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etNickname.requestFocus();
                    return;
                }

                // 전화번호 입력 확인
                if( etPhoneNum.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "전화번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPhoneNum.requestFocus();
                    return;
                }

                // 이메일 입력 확인
                if( etUserID.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "아이디를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etUserID.requestFocus();
                    return;
                }

                // 비밀번호 입력 확인
                if( etPassword.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if( etPasswordConfirm.getText().toString().length() == 0 ) {
                    Toast.makeText(SignupPage.this, "비밀번호 확인이 필요합니다!", Toast.LENGTH_SHORT).show();
                    etPasswordConfirm.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if( !etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()) ) {
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

}
