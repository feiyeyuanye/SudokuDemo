package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NineGridView nine = findViewById(R.id.nine);
        nine.setOnPasswordFinishListener(new NineGridView.OnPasswordFinishListener() {
            @Override
            public void onPasswrodFinish(String password) {
                Toast.makeText(MainActivity.this, "你输入的密码是" + password, Toast.LENGTH_LONG).show();
            }
        });
    }
}
