package com.lte.lte;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

///Log in, Sign in

public class LogInActivity extends AppCompatActivity {

    Button btnTemporary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        btnTemporary = (Button) findViewById(R.id.btn_temporary);

        btnTemporary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        로그인 성공하면 마지막 부분에
//        Intent intent = new Intent();
//        setResult(RESULT_OK, intent);
//        finish();
//        코드 추가해 주시면 됩니다

    }


}
