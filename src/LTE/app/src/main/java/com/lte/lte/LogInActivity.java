package com.lte.lte;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    private EditText etUserID;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etUserID = (EditText) findViewById(R.id.etUserID);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegist = (Button) findViewById(R.id.btnRegist);

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
            @Override
            public void onClick(View v) {
                // 여기에 DB와 내용 비교해야 함

                // 로그인 내용 확인되면 Main Activity 호출
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                // 회원 ID를 Main Activity로 보내서 DB 정보 검색 Key 값으로 이용
                intent.putExtra("UserID", String.valueOf(etUserID.getText()));
                startActivity(intent);
                finish(); // 로그인 Activity는 종료됨
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

        if(requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(LogInActivity.this, "회원가입을 완료했습니다!", Toast.LENGTH_SHORT).show();
            etUserID.setText(data.getStringExtra("ID"));
        }
    }

}
