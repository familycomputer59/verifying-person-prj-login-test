package com.example.kazuki.login_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NextActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directors);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str = bundle.getString("Key");

        textView = (TextView)findViewById(R.id.idrestext);

        textView.setText(str);

    }
}
